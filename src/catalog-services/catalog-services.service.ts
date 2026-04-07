import {
  ConflictException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Prisma } from '@prisma/client';
import { generateCuid } from '../common/cuid';
import { decToNumber } from '../common/decimal.util';
import { PrismaService } from '../prisma/prisma.service';
import {
  BarbershopServiceLinkDto,
  BarbershopServiceUpdateDto,
} from './dto/barbershop-service-link.dto';
import { ServiceCreateDto } from './dto/service-create.dto';

@Injectable()
export class CatalogServicesService {
  constructor(private readonly prisma: PrismaService) {}

  private async checkOwner(barbershopId: string, userId: string) {
    const b = await this.prisma.barbershop.findFirst({
      where: { id: barbershopId, owner_user_id: userId },
    });
    if (!b) {
      throw new ForbiddenException('not_owner_or_barbershop_not_found');
    }
  }

  async createGlobal(req: ServiceCreateDto) {
    const s = await this.prisma.catalogService.create({
      data: {
        id: generateCuid(),
        name: req.name,
        description: req.description,
        base_price: new Prisma.Decimal(req.basePrice),
        duration_minutes: req.durationMinutes,
      },
    });
    return this.toServiceResponse(s);
  }

  async listGlobal() {
    const rows = await this.prisma.catalogService.findMany();
    return rows.map((s) => this.toServiceResponse(s));
  }

  async link(
    barbershopId: string,
    ownerUserId: string,
    req: BarbershopServiceLinkDto,
  ) {
    await this.checkOwner(barbershopId, ownerUserId);

    const service = await this.prisma.catalogService.findUnique({
      where: { id: req.serviceId },
    });
    if (!service) throw new NotFoundException('service_not_found');

    const dup = await this.prisma.barbershopServiceLink.findFirst({
      where: { barbershop_id: barbershopId, service_id: req.serviceId },
    });
    if (dup) throw new ConflictException('service_already_linked');

    const link = await this.prisma.barbershopServiceLink.create({
      data: {
        id: generateCuid(),
        barbershop_id: barbershopId,
        service_id: req.serviceId,
        custom_price:
          req.customPrice != null ? new Prisma.Decimal(req.customPrice) : null,
      },
    });
    return this.toBarbershopServiceResponse(link, service);
  }

  async listByBarbershop(barbershopId: string) {
    const links = await this.prisma.barbershopServiceLink.findMany({
      where: { barbershop_id: barbershopId },
    });
    const out = [];
    for (const link of links) {
      const service = await this.prisma.catalogService.findUniqueOrThrow({
        where: { id: link.service_id! },
      });
      out.push(this.toBarbershopServiceResponse(link, service));
    }
    return out;
  }

  async updateLink(
    barbershopId: string,
    linkId: string,
    ownerUserId: string,
    req: BarbershopServiceUpdateDto,
  ) {
    await this.checkOwner(barbershopId, ownerUserId);
    const link = await this.prisma.barbershopServiceLink.findFirst({
      where: { id: linkId, barbershop_id: barbershopId },
    });
    if (!link) throw new NotFoundException('link_not_found');

    const updated = await this.prisma.barbershopServiceLink.update({
      where: { id: linkId },
      data: { custom_price: new Prisma.Decimal(req.customPrice) },
    });
    const service = await this.prisma.catalogService.findUniqueOrThrow({
      where: { id: updated.service_id! },
    });
    return this.toBarbershopServiceResponse(updated, service);
  }

  async unlink(barbershopId: string, linkId: string, ownerUserId: string) {
    await this.checkOwner(barbershopId, ownerUserId);
    const link = await this.prisma.barbershopServiceLink.findFirst({
      where: { id: linkId, barbershop_id: barbershopId },
    });
    if (!link) throw new NotFoundException('link_not_found');
    await this.prisma.barbershopServiceLink.delete({ where: { id: linkId } });
  }

  private toServiceResponse(s: {
    id: string;
    name: string | null;
    description: string | null;
    base_price: unknown;
    duration_minutes: number | null;
  }) {
    return {
      id: s.id,
      name: s.name,
      description: s.description,
      basePrice: decToNumber(s.base_price),
      durationMinutes: s.duration_minutes,
    };
  }

  private toBarbershopServiceResponse(
    link: { id: string; service_id: string | null; custom_price: unknown },
    s: {
      id: string;
      name: string | null;
      description: string | null;
      duration_minutes: number | null;
      base_price: unknown;
    },
  ) {
    const base = decToNumber(s.base_price);
    const custom = decToNumber(link.custom_price);
    const effective = custom != null ? custom : base;
    return {
      id: link.id,
      serviceId: s.id,
      serviceName: s.name,
      serviceDescription: s.description,
      durationMinutes: s.duration_minutes,
      basePrice: base,
      customPrice: custom,
      effectivePrice: effective,
    };
  }
}
