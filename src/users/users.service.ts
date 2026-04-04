import {
  ConflictException,
  ForbiddenException,
  Injectable,
  NotFoundException,
  UnprocessableEntityException,
} from '@nestjs/common';
import { Prisma } from '@prisma/client';
import * as bcrypt from 'bcrypt';
import { generateCuid } from '../common/cuid';
import { normalizePhone } from '../common/normalizers';
import { PrismaService } from '../prisma/prisma.service';
import {
  AcceptTermsDto,
  ChangePasswordDto,
  DocumentCreateDto,
  DocumentUpdateDto,
  PaymentMethodDto,
  UpdateUserDto,
  UserPreferencesDto,
} from './dto/user-dtos';

@Injectable()
export class UsersService {
  constructor(private readonly prisma: PrismaService) {}

  private ensureSelf(targetUserId: string, authUserId: string) {
    if (targetUserId !== authUserId) {
      throw new ForbiddenException('forbidden');
    }
  }

  async updateUser(targetUserId: string, authUserId: string, dto: UpdateUserDto) {
    this.ensureSelf(targetUserId, authUserId);
    const user = await this.prisma.user.findUnique({ where: { id: targetUserId } });
    if (!user) throw new NotFoundException('user_not_found');

    const data: Prisma.UserUpdateInput = { updated_at: new Date() };
    if (dto.name !== undefined) data.name = dto.name;
    if (dto.birthDate !== undefined) {
      data.birth_date = new Date(`${dto.birthDate}T12:00:00Z`);
    }
    if (dto.hairType !== undefined) data.hair_type = dto.hairType;
    if (dto.hairTexture !== undefined) data.hair_texture = dto.hairTexture;
    if (dto.hasBeard !== undefined) data.has_beard = dto.hasBeard;

    if (dto.phone !== undefined) {
      const normalized = normalizePhone(dto.phone)!;
      const conflict = await this.prisma.user.findFirst({
        where: { phone: normalized, id: { not: targetUserId } },
      });
      if (conflict) throw new ConflictException('phone_already_in_use');
      data.phone = normalized;
    }

    await this.prisma.user.update({
      where: { id: targetUserId },
      data,
    });

    if (user.role === 'BARBER') {
      const barber = await this.prisma.barber.findUnique({
        where: { user_id: targetUserId },
      });
      if (!barber) throw new NotFoundException('barber_profile_not_found');
      const bData: Prisma.BarberUpdateInput = { updated_at: new Date() };
      if (dto.bio !== undefined) bData.bio = dto.bio;
      if (dto.yearsExperience !== undefined) {
        bData.years_experience = dto.yearsExperience;
      }
      if (dto.name !== undefined) bData.name = dto.name;
      if (Object.keys(bData).length > 1) {
        await this.prisma.barber.update({
          where: { id: barber.id },
          data: bData,
        });
      }
    }

    return { message: 'updated' };
  }

  async changePassword(
    targetUserId: string,
    authUserId: string,
    dto: ChangePasswordDto,
  ) {
    this.ensureSelf(targetUserId, authUserId);
    const user = await this.prisma.user.findUnique({ where: { id: targetUserId } });
    if (!user?.password_hash) throw new NotFoundException('user_not_found');

    const ok = await bcrypt.compare(dto.currentPassword, user.password_hash);
    if (!ok) {
      throw new UnprocessableEntityException('current_password_incorrect');
    }
    const same = await bcrypt.compare(dto.newPassword, user.password_hash);
    if (same) {
      throw new UnprocessableEntityException('new_password_same_as_current');
    }

    await this.prisma.user.update({
      where: { id: targetUserId },
      data: {
        password_hash: await bcrypt.hash(dto.newPassword, 10),
        updated_at: new Date(),
      },
    });
    return { message: 'password_updated' };
  }

  async listPaymentMethods(targetUserId: string, authUserId: string) {
    this.ensureSelf(targetUserId, authUserId);
    const rows = await this.prisma.userPaymentMethod.findMany({
      where: { user_id: targetUserId },
    });
    return rows.map((m) => ({
      id: m.id,
      provider: m.provider,
      cardType: m.card_type,
      lastFourDigits: m.last_four_digits,
      brand: m.brand,
      createdAt: m.created_at,
    }));
  }

  async addPaymentMethod(
    targetUserId: string,
    authUserId: string,
    dto: PaymentMethodDto,
  ) {
    this.ensureSelf(targetUserId, authUserId);
    const user = await this.prisma.user.findUnique({ where: { id: targetUserId } });
    if (!user) throw new NotFoundException('user_not_found');

    const existing = await this.prisma.userPaymentMethod.findMany({
      where: { user_id: targetUserId },
    });
    if (existing.some((p) => p.provider_customer_id === dto.providerCustomerId)) {
      throw new ConflictException('payment_method_already_exists');
    }

    const m = await this.prisma.userPaymentMethod.create({
      data: {
        id: generateCuid(),
        user_id: targetUserId,
        provider: dto.provider,
        provider_customer_id: dto.providerCustomerId,
        card_type: dto.cardType,
        last_four_digits: dto.lastFourDigits,
        brand: dto.brand,
        created_at: new Date(),
      },
    });
    return {
      id: m.id,
      provider: m.provider,
      cardType: m.card_type,
      lastFourDigits: m.last_four_digits,
      brand: m.brand,
      createdAt: m.created_at,
    };
  }

  async deletePaymentMethod(
    targetUserId: string,
    authUserId: string,
    methodId: string,
  ) {
    this.ensureSelf(targetUserId, authUserId);
    const m = await this.prisma.userPaymentMethod.findFirst({
      where: { id: methodId, user_id: targetUserId },
    });
    if (!m) throw new NotFoundException('payment_method_not_found');
    await this.prisma.userPaymentMethod.delete({ where: { id: methodId } });
  }

  async getPreferences(targetUserId: string, authUserId: string) {
    this.ensureSelf(targetUserId, authUserId);
    const prefs = await this.prisma.userPreferences.findUnique({
      where: { user_id: targetUserId },
    });
    if (!prefs) {
      return {
        id: '_default',
        theme: 'LIGHT',
        language: 'pt-BR',
        updatedAt: null,
      };
    }
    return {
      id: prefs.id,
      theme: prefs.theme,
      language: prefs.language,
      updatedAt: prefs.updated_at,
    };
  }

  async updatePreferences(
    targetUserId: string,
    authUserId: string,
    dto: UserPreferencesDto,
  ) {
    this.ensureSelf(targetUserId, authUserId);
    let prefs = await this.prisma.userPreferences.findUnique({
      where: { user_id: targetUserId },
    });
    if (!prefs) {
      const user = await this.prisma.user.findUnique({ where: { id: targetUserId } });
      if (!user) throw new NotFoundException('user_not_found');
      prefs = await this.prisma.userPreferences.create({
        data: {
          id: generateCuid(),
          user_id: targetUserId,
          theme: dto.theme ?? 'LIGHT',
          language: dto.language ?? 'pt-BR',
          updated_at: new Date(),
        },
      });
    } else {
      prefs = await this.prisma.userPreferences.update({
        where: { user_id: targetUserId },
        data: {
          theme: dto.theme ?? undefined,
          language: dto.language ?? undefined,
          updated_at: new Date(),
        },
      });
    }
    return {
      id: prefs.id,
      theme: prefs.theme,
      language: prefs.language,
      updatedAt: prefs.updated_at,
    };
  }

  async acceptTerms(
    targetUserId: string,
    authUserId: string,
    dto: AcceptTermsDto,
  ) {
    this.ensureSelf(targetUserId, authUserId);
    if (dto.accepted === false) {
      throw new UnprocessableEntityException('terms_must_be_accepted');
    }
    const user = await this.prisma.user.findUnique({ where: { id: targetUserId } });
    if (!user) throw new NotFoundException('user_not_found');

    const latest = await this.prisma.termsAcceptance.findFirst({
      where: { user_id: targetUserId },
      orderBy: { accepted_at: 'desc' },
    });
    if (latest?.terms_version === dto.termsVersion) {
      throw new ConflictException('terms_version_already_accepted');
    }

    const acceptance = await this.prisma.termsAcceptance.create({
      data: {
        id: generateCuid(),
        user_id: targetUserId,
        terms_version: dto.termsVersion,
        accepted_at: new Date(),
      },
    });
    return {
      message: 'terms_accepted',
      termsVersion: acceptance.terms_version,
      acceptedAt: acceptance.accepted_at,
    };
  }

  async listDocuments(targetUserId: string, authUserId: string) {
    this.ensureSelf(targetUserId, authUserId);
    const rows = await this.prisma.userDocument.findMany({
      where: { user_id: targetUserId },
    });
    return rows.map((d) => ({
      id: d.id,
      documentType: d.document_type,
      documentNumber: d.document_number,
      createdAt: d.created_at,
    }));
  }

  async addDocument(
    targetUserId: string,
    authUserId: string,
    dto: DocumentCreateDto,
  ) {
    this.ensureSelf(targetUserId, authUserId);
    const user = await this.prisma.user.findUnique({ where: { id: targetUserId } });
    if (!user) throw new NotFoundException('user_not_found');
    const doc = await this.prisma.userDocument.create({
      data: {
        id: generateCuid(),
        user_id: targetUserId,
        document_type: dto.documentType,
        document_number: dto.documentNumber,
        created_at: new Date(),
      },
    });
    return {
      id: doc.id,
      documentType: doc.document_type,
      documentNumber: doc.document_number,
      createdAt: doc.created_at,
    };
  }

  async updateDocument(
    targetUserId: string,
    authUserId: string,
    documentId: string,
    dto: DocumentUpdateDto,
  ) {
    this.ensureSelf(targetUserId, authUserId);
    const doc = await this.prisma.userDocument.findFirst({
      where: { id: documentId, user_id: targetUserId },
    });
    if (!doc) throw new NotFoundException('document_not_found');
    const updated = await this.prisma.userDocument.update({
      where: { id: documentId },
      data: {
        document_type: dto.documentType ?? undefined,
        document_number: dto.documentNumber ?? undefined,
      },
    });
    return {
      id: updated.id,
      documentType: updated.document_type,
      documentNumber: updated.document_number,
      createdAt: updated.created_at,
    };
  }
}
