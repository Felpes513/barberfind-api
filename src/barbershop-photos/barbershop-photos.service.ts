import {
  BadRequestException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { generateCuid } from '../common/cuid';
import { PrismaService } from '../prisma/prisma.service';

const ALLOWED_TYPES = new Set([
  'image/jpeg',
  'image/png',
  'image/webp',
  'image/gif',
]);
const MAX_BASE64_LENGTH = 6_800_000;

@Injectable()
export class BarbershopPhotosService {
  constructor(private readonly prisma: PrismaService) {}

  private async checkOwner(barbershopId: string, userId: string) {
    const b = await this.prisma.barbershop.findFirst({
      where: { id: barbershopId, owner_user_id: userId },
    });
    if (!b) {
      throw new ForbiddenException('not_owner_or_barbershop_not_found');
    }
  }

  async list(barbershopId: string) {
    const rows = await this.prisma.barbershopPhoto.findMany({
      where: { barbershop_id: barbershopId },
    });
    return rows.map((p) => ({
      id: p.id,
      dataUrl: p.image_url,
      createdAt: p.created_at,
    }));
  }

  async add(barbershopId: string, ownerUserId: string, body: { imageData: string; mediaType: string }) {
    await this.checkOwner(barbershopId, ownerUserId);

    const mediaType = body.mediaType.toLowerCase();
    if (!ALLOWED_TYPES.has(mediaType)) {
      throw new BadRequestException(
        'invalid_media_type. Allowed: image/jpeg, image/png, image/webp, image/gif',
      );
    }
    if (body.imageData.length > MAX_BASE64_LENGTH) {
      throw new BadRequestException('image_too_large. Max size: 5MB');
    }
    try {
      Buffer.from(body.imageData.replace(/\s/g, ''), 'base64');
    } catch {
      throw new BadRequestException('invalid_base64');
    }

    const dataUrl = `data:${mediaType};base64,${body.imageData}`;
    const photo = await this.prisma.barbershopPhoto.create({
      data: {
        id: generateCuid(),
        barbershop_id: barbershopId,
        image_url: dataUrl,
        created_at: new Date(),
      },
    });
    return {
      id: photo.id,
      dataUrl: photo.image_url,
      createdAt: photo.created_at,
    };
  }

  async delete(barbershopId: string, ownerUserId: string, photoId: string) {
    await this.checkOwner(barbershopId, ownerUserId);
    const photo = await this.prisma.barbershopPhoto.findFirst({
      where: { id: photoId, barbershop_id: barbershopId },
    });
    if (!photo) throw new NotFoundException('photo_not_found');
    await this.prisma.barbershopPhoto.delete({ where: { id: photoId } });
  }
}
