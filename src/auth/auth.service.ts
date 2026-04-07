import { randomUUID } from 'crypto';
import {
  ConflictException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { generateCuid } from '../common/cuid';
import { normalizeEmail, normalizePhone } from '../common/normalizers';
import { PrismaService } from '../prisma/prisma.service';
import { hmacKeyBytes } from './jwt-key.util';
import { LoginDto } from './dto/login.dto';
import { RegisterClientDto } from './dto/register-client.dto';
import { RegisterBarberDto } from './dto/register-barber.dto';
import { RegisterOwnerDto } from './dto/register-owner.dto';

@Injectable()
export class AuthService {
  constructor(
    private readonly prisma: PrismaService,
    private readonly jwt: JwtService,
    private readonly config: ConfigService,
  ) {}

  async login(dto: LoginDto) {
    const email = normalizeEmail(dto.email);
    if (!email) throw new UnauthorizedException('invalid_credentials');

    const user = await this.prisma.user.findFirst({
      where: { email: { equals: email, mode: 'insensitive' } },
    });
    if (!user?.password_hash) {
      throw new UnauthorizedException('invalid_credentials');
    }

    const ok = await bcrypt.compare(dto.password, user.password_hash);
    if (!ok) throw new UnauthorizedException('invalid_credentials');

    const token = await this.signToken(user.id, user.role);
    return {
      token,
      userId: user.id,
      role: user.role,
      name: user.name,
      email: user.email,
    };
  }

  async logout(token: string | undefined) {
    if (!token) return;
    const exists = await this.prisma.revokedToken.findUnique({
      where: { token },
    });
    if (exists) return;
    await this.prisma.revokedToken.create({
      data: {
        id: randomUUID(),
        token,
        revoked_at: new Date(),
      },
    });
  }

  async registerClient(dto: RegisterClientDto) {
    const email = normalizeEmail(dto.email)!;
    const phone = normalizePhone(dto.phone);

    if (
      await this.prisma.user.findFirst({
        where: { email: { equals: email, mode: 'insensitive' } },
      })
    ) {
      throw new ConflictException('email_already_in_use');
    }
    if (phone && (await this.prisma.user.findFirst({ where: { phone } }))) {
      throw new ConflictException('phone_already_in_use');
    }

    const hash = await bcrypt.hash(dto.password, 10);
    const u = await this.prisma.user.create({
      data: {
        id: generateCuid(),
        role: 'CLIENT',
        name: dto.name,
        email,
        phone: phone ?? undefined,
        password_hash: hash,
        birth_date: dto.birthDate
          ? new Date(`${dto.birthDate}T12:00:00Z`)
          : undefined,
        hair_type: dto.hairType,
        hair_texture: dto.hairTexture,
        has_beard: dto.hasBeard,
        created_at: new Date(),
        updated_at: new Date(),
      },
    });

    return {
      message: 'registered',
      data: { id: u.id, role: u.role, email: u.email },
    };
  }

  async registerBarber(dto: RegisterBarberDto) {
    const email = normalizeEmail(dto.email)!;
    const phone =
      dto.phone != null && dto.phone !== '' ? normalizePhone(dto.phone) : null;

    if (
      await this.prisma.user.findFirst({
        where: { email: { equals: email, mode: 'insensitive' } },
      })
    ) {
      throw new ConflictException('email_already_in_use');
    }
    if (phone && (await this.prisma.user.findFirst({ where: { phone } }))) {
      throw new ConflictException('phone_already_in_use');
    }

    const now = new Date();
    const hash = await bcrypt.hash(dto.password, 10);
    const userId = generateCuid();
    const barberId = generateCuid();

    await this.prisma.$transaction(async (tx) => {
      await tx.user.create({
        data: {
          id: userId,
          name: dto.name,
          email,
          phone: phone ?? undefined,
          role: 'BARBER',
          password_hash: hash,
          created_at: now,
          updated_at: now,
        },
      });
      await tx.barber.create({
        data: {
          id: barberId,
          user_id: userId,
          name: dto.name,
          bio: dto.bio,
          years_experience: dto.yearsExperience,
          rating: 0,
          created_at: now,
          updated_at: now,
        },
      });
    });

    return {
      userId,
      barberId,
      name: dto.name,
      email,
    };
  }

  async registerOwner(dto: RegisterOwnerDto) {
    const email = dto.email.trim().toLowerCase();
    const phone = dto.phone.trim();

    if (
      await this.prisma.user.findFirst({
        where: { email: { equals: email, mode: 'insensitive' } },
      })
    ) {
      throw new ConflictException('email_already_in_use');
    }
    if (await this.prisma.user.findFirst({ where: { phone } })) {
      throw new ConflictException('phone_already_in_use');
    }

    const now = new Date();
    const hash = await bcrypt.hash(dto.password, 10);
    const u = await this.prisma.user.create({
      data: {
        id: generateCuid(),
        role: 'OWNER',
        name: dto.name,
        email,
        phone,
        password_hash: hash,
        created_at: now,
        updated_at: now,
      },
    });

    return {
      userId: u.id,
      name: u.name,
      email: u.email,
      role: u.role,
    };
  }

  private async signToken(userId: string, role: string) {
    const ms = Number(this.config.get('JWT_EXPIRATION_MS', '86400000'));
    const secret = hmacKeyBytes(this.config.getOrThrow<string>('JWT_SECRET'));
    return this.jwt.signAsync(
      { role },
      {
        subject: userId,
        secret,
        expiresIn: Math.max(1, Math.floor(ms / 1000)),
      },
    );
  }
}
