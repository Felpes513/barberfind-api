import {
  ConflictException,
  ForbiddenException,
  Injectable,
  NotFoundException,
  UnprocessableEntityException,
} from '@nestjs/common';
import { generateCuid } from '../common/cuid';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class FavoritesService {
  constructor(private readonly prisma: PrismaService) {}

  private checkOwnership(targetUserId: string, authUserId: string) {
    if (targetUserId !== authUserId) {
      throw new ForbiddenException('forbidden');
    }
  }

  async list(targetUserId: string, authUserId: string) {
    this.checkOwnership(targetUserId, authUserId);
    const rows = await this.prisma.favorite.findMany({
      where: { user_id: targetUserId },
      include: { barber: true, barbershop: true },
    });
    return rows.map((f) => ({
      id: f.id,
      barberId: f.barber_id,
      barberName: f.barber?.name ?? null,
      barbershopId: f.barbershop_id,
      barbershopName: f.barbershop?.name ?? null,
      createdAt: f.created_at,
    }));
  }

  async add(
    targetUserId: string,
    authUserId: string,
    body: { barberId?: string; barbershopId?: string },
  ) {
    this.checkOwnership(targetUserId, authUserId);
    const hasBarber = body.barberId != null;
    const hasBarbershop = body.barbershopId != null;
    if (!hasBarber && !hasBarbershop) {
      throw new UnprocessableEntityException(
        'barber_id_or_barbershop_id_required',
      );
    }
    if (hasBarber && hasBarbershop) {
      throw new UnprocessableEntityException(
        'only_one_of_barber_id_or_barbershop_id_allowed',
      );
    }

    const user = await this.prisma.user.findUnique({
      where: { id: targetUserId },
    });
    if (!user) throw new NotFoundException('user_not_found');

    if (hasBarber) {
      const dup = await this.prisma.favorite.findFirst({
        where: { user_id: targetUserId, barber_id: body.barberId },
      });
      if (dup) throw new ConflictException('barber_already_favorited');
      const barber = await this.prisma.barber.findUnique({
        where: { id: body.barberId },
      });
      if (!barber) throw new NotFoundException('barber_not_found');
      const f = await this.prisma.favorite.create({
        data: {
          id: generateCuid(),
          user_id: targetUserId,
          barber_id: body.barberId,
          created_at: new Date(),
        },
        include: { barber: true, barbershop: true },
      });
      return this.toResponse(f);
    }

    const dup = await this.prisma.favorite.findFirst({
      where: { user_id: targetUserId, barbershop_id: body.barbershopId },
    });
    if (dup) throw new ConflictException('barbershop_already_favorited');
    const shop = await this.prisma.barbershop.findUnique({
      where: { id: body.barbershopId },
    });
    if (!shop) throw new NotFoundException('barbershop_not_found');
    const f = await this.prisma.favorite.create({
      data: {
        id: generateCuid(),
        user_id: targetUserId,
        barbershop_id: body.barbershopId,
        created_at: new Date(),
      },
      include: { barber: true, barbershop: true },
    });
    return this.toResponse(f);
  }

  private toResponse(f: {
    id: string;
    barber_id: string | null;
    barbershop_id: string | null;
    created_at: Date | null;
    barber: { name: string | null } | null;
    barbershop: { name: string | null } | null;
  }) {
    return {
      id: f.id,
      barberId: f.barber_id,
      barberName: f.barber?.name ?? null,
      barbershopId: f.barbershop_id,
      barbershopName: f.barbershop?.name ?? null,
      createdAt: f.created_at,
    };
  }

  async remove(targetUserId: string, authUserId: string, favoriteId: string) {
    this.checkOwnership(targetUserId, authUserId);
    const f = await this.prisma.favorite.findFirst({
      where: { id: favoriteId, user_id: targetUserId },
    });
    if (!f) throw new NotFoundException('favorite_not_found');
    await this.prisma.favorite.delete({ where: { id: favoriteId } });
  }
}
