import { BadRequestException, Injectable } from '@nestjs/common';
import { generateCuid } from '../common/cuid';
import { formatTimeFromDate, parseTimeToDate } from '../common/time.util';
import { PrismaService } from '../prisma/prisma.service';
import { BarbershopCreateDto } from './dto/barbershop-create.dto';
import { BarbershopStatusDto } from './dto/barbershop-status.dto';
import { BarbershopUpdateDto } from './dto/barbershop-update.dto';

@Injectable()
export class BarbershopsService {
  constructor(private readonly prisma: PrismaService) {}

  private toResponse(b: {
    id: string;
    owner_user_id: string | null;
    name: string | null;
    cnpj: string | null;
    description: string | null;
    phone: string | null;
    email: string | null;
    address: string | null;
    neighborhood: string | null;
    city: string | null;
    state: string | null;
    opening_time: Date | null;
    closing_time: Date | null;
    is_active: boolean;
    is_headquarter: boolean;
    parent_barbershop_id: string | null;
  }) {
    return {
      id: b.id,
      ownerUserId: b.owner_user_id,
      name: b.name,
      cnpj: b.cnpj,
      description: b.description,
      phone: b.phone,
      email: b.email,
      address: b.address,
      neighborhood: b.neighborhood,
      city: b.city,
      state: b.state,
      openingTime: formatTimeFromDate(b.opening_time),
      closingTime: formatTimeFromDate(b.closing_time),
      isActive: b.is_active,
      isHeadquarter: b.is_headquarter,
      parentBarbershopId: b.parent_barbershop_id,
    };
  }

  async listPublic() {
    const rows = await this.prisma.barbershop.findMany({
      where: { is_active: true },
    });
    return rows.map((b) => this.toResponse(b));
  }

  async getPublicById(id: string) {
    const b = await this.prisma.barbershop.findFirst({
      where: { id, is_active: true },
    });
    if (!b) throw new BadRequestException('Barbearia não encontrada.');
    return this.toResponse(b);
  }

  async create(ownerUserId: string, req: BarbershopCreateDto) {
    let isHeadquarter = req.isHeadquarter ?? true;
    const parentId = req.parentBarbershopId;

    if (isHeadquarter && parentId?.trim()) {
      throw new BadRequestException('Matriz não pode ter parentBarbershopId.');
    }
    if (!isHeadquarter && (!parentId || !parentId.trim())) {
      throw new BadRequestException('Filial precisa de parentBarbershopId.');
    }
    if (!isHeadquarter) {
      const p = await this.prisma.barbershop.findUnique({ where: { id: parentId } });
      if (!p) throw new BadRequestException('Barbearia matriz (parent) não encontrada.');
    }

    const b = await this.prisma.barbershop.create({
      data: {
        id: generateCuid(),
        owner_user_id: ownerUserId,
        name: req.name,
        cnpj: req.cnpj,
        description: req.description,
        phone: req.phone,
        email: req.email,
        address: req.address,
        neighborhood: req.neighborhood,
        city: req.city,
        state: req.state,
        opening_time: parseTimeToDate(req.openingTime ?? null),
        closing_time: parseTimeToDate(req.closingTime ?? null),
        is_headquarter: isHeadquarter,
        parent_barbershop_id: isHeadquarter ? null : parentId ?? null,
        is_active: true,
        created_at: new Date(),
        updated_at: new Date(),
      },
    });
    return this.toResponse(b);
  }

  async update(id: string, ownerUserId: string, req: BarbershopUpdateDto) {
    const b = await this.prisma.barbershop.findFirst({
      where: { id, owner_user_id: ownerUserId },
    });
    if (!b) {
      throw new BadRequestException('Barbearia não encontrada ou não pertence ao dono.');
    }

    const updated = await this.prisma.barbershop.update({
      where: { id },
      data: {
        name: req.name ?? undefined,
        description: req.description ?? undefined,
        phone: req.phone ?? undefined,
        email: req.email ?? undefined,
        address: req.address ?? undefined,
        neighborhood: req.neighborhood ?? undefined,
        city: req.city ?? undefined,
        state: req.state ?? undefined,
        opening_time:
          req.openingTime !== undefined
            ? parseTimeToDate(req.openingTime)
            : undefined,
        closing_time:
          req.closingTime !== undefined
            ? parseTimeToDate(req.closingTime)
            : undefined,
        updated_at: new Date(),
      },
    });
    return this.toResponse(updated);
  }

  async setStatus(id: string, ownerUserId: string, req: BarbershopStatusDto) {
    const b = await this.prisma.barbershop.findFirst({
      where: { id, owner_user_id: ownerUserId },
    });
    if (!b) {
      throw new BadRequestException('Barbearia não encontrada ou não pertence ao dono.');
    }
    const updated = await this.prisma.barbershop.update({
      where: { id },
      data: { is_active: req.isActive === true, updated_at: new Date() },
    });
    return this.toResponse(updated);
  }

  async listMine(ownerUserId: string) {
    const rows = await this.prisma.barbershop.findMany({
      where: { owner_user_id: ownerUserId },
    });
    return rows.map((b) => this.toResponse(b));
  }
}
