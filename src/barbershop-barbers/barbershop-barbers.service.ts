import {
  ConflictException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { generateCuid } from '../common/cuid';
import { decToNumber } from '../common/decimal.util';
import { formatTimeFromDate } from '../common/time.util';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class BarbershopBarbersService {
  constructor(private readonly prisma: PrismaService) {}

  private async checkOwner(barbershopId: string, userId: string) {
    const b = await this.prisma.barbershop.findFirst({
      where: { id: barbershopId, owner_user_id: userId },
    });
    if (!b) {
      throw new ForbiddenException('not_owner_or_barbershop_not_found');
    }
  }

  private async getLinkOrThrow(barbershopId: string, linkId: string) {
    const link = await this.prisma.barbershopBarber.findUnique({
      where: { id: linkId },
    });
    if (!link || link.barbershop_id !== barbershopId) {
      throw new NotFoundException('link_not_found');
    }
    return link;
  }

  async requestEntry(barbershopId: string, userId: string) {
    const barber = await this.prisma.barber.findUnique({
      where: { user_id: userId },
    });
    if (!barber) throw new ForbiddenException('user_is_not_a_barber');

    const shop = await this.prisma.barbershop.findUnique({
      where: { id: barbershopId },
    });
    if (!shop) throw new NotFoundException('barbershop_not_found');

    const exists = await this.prisma.barbershopBarber.findFirst({
      where: { barbershop_id: barbershopId, barber_id: barber.id },
    });
    if (exists) throw new ConflictException('barber_already_linked');

    const link = await this.prisma.barbershopBarber.create({
      data: {
        id: generateCuid(),
        barbershop_id: barbershopId,
        barber_id: barber.id,
        active: false,
      },
    });

    return {
      linkId: link.id,
      barbershopId,
      barberId: barber.id,
      active: false,
      message: 'entry_requested_pending_approval',
    };
  }

  async approve(barbershopId: string, ownerUserId: string, linkId: string) {
    await this.checkOwner(barbershopId, ownerUserId);
    const link = await this.getLinkOrThrow(barbershopId, linkId);
    const updated = await this.prisma.barbershopBarber.update({
      where: { id: link.id },
      data: { active: true },
    });
    return {
      linkId: updated.id,
      barbershopId,
      barberId: updated.barber_id,
      active: true,
      message: 'barber_approved',
    };
  }

  async reject(barbershopId: string, ownerUserId: string, linkId: string) {
    await this.checkOwner(barbershopId, ownerUserId);
    const link = await this.getLinkOrThrow(barbershopId, linkId);
    const updated = await this.prisma.barbershopBarber.update({
      where: { id: link.id },
      data: { active: false },
    });
    return {
      linkId: updated.id,
      barbershopId,
      barberId: updated.barber_id,
      active: false,
      message: 'barber_deactivated',
    };
  }

  async remove(
    barbershopId: string,
    userId: string,
    role: string,
    linkId: string,
  ) {
    const link = await this.getLinkOrThrow(barbershopId, linkId);

    if (role === 'OWNER') {
      await this.checkOwner(barbershopId, userId);
    } else if (role === 'BARBER') {
      const barber = await this.prisma.barber.findUnique({
        where: { user_id: userId },
      });
      if (!barber || link.barber_id !== barber.id) {
        throw new ForbiddenException('forbidden');
      }
    } else {
      throw new ForbiddenException('forbidden');
    }

    await this.prisma.barbershopBarber.delete({ where: { id: linkId } });
  }

  async list(barbershopId: string) {
    const shop = await this.prisma.barbershop.findUnique({
      where: { id: barbershopId },
    });
    if (!shop) throw new NotFoundException('barbershop_not_found');

    const links = await this.prisma.barbershopBarber.findMany({
      where: { barbershop_id: barbershopId },
    });

    const out = [];
    for (const link of links) {
      const barber = await this.prisma.barber.findUniqueOrThrow({
        where: { id: link.barber_id },
      });

      const barberSvcLinks = await this.prisma.barberServiceLink.findMany({
        where: { barber_id: barber.id },
      });
      const services = [];
      for (const bs of barberSvcLinks) {
        const s = await this.prisma.catalogService.findUnique({
          where: { id: bs.service_id },
        });
        if (s) {
          services.push({
            serviceId: s.id,
            serviceName: s.name,
            durationMinutes: s.duration_minutes,
            basePrice: decToNumber(s.base_price),
          });
        }
      }

      const availabilityRows = await this.prisma.barberAvailability.findMany({
        where: { barber_id: barber.id },
      });
      const availability = availabilityRows.map((a) => ({
        weekday: a.weekday,
        startTime: formatTimeFromDate(a.start_time),
        endTime: formatTimeFromDate(a.end_time),
      }));

      const portfolioRows = await this.prisma.barberPortfolio.findMany({
        where: { barber_id: barber.id },
      });
      const portfolio = portfolioRows.map((p) => ({
        id: p.id,
        imageUrl: p.image_url,
        hairType: p.hair_type,
        styleTag: p.style_tag,
        createdAt: p.created_at,
      }));

      out.push({
        linkId: link.id,
        active: link.active,
        barberId: barber.id,
        userId: barber.user_id,
        name: barber.name,
        bio: barber.bio,
        yearsExperience: barber.years_experience,
        rating: decToNumber(barber.rating),
        services,
        availability,
        portfolio,
      });
    }
    return out;
  }
}
