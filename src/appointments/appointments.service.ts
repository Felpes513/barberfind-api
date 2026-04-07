import {
  BadRequestException,
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
  AppointmentCancelDto,
  AppointmentCompleteDto,
  AppointmentCreateDto,
} from './dto/appointment.dto';

@Injectable()
export class AppointmentsService {
  constructor(private readonly prisma: PrismaService) {}

  private async getServiceMeta(serviceId: string) {
    const s = await this.prisma.catalogService.findUnique({
      where: { id: serviceId },
    });
    if (!s) return null;
    return {
      name: s.name,
      duration: s.duration_minutes,
      basePrice: s.base_price,
    };
  }

  private toResponse(
    a: {
      id: string;
      user_id: string | null;
      barbershop_id: string | null;
      barber_id: string | null;
      service_id: string | null;
      final_price: unknown;
      payment_method: string | null;
      scheduled_at: Date | null;
      completed_at: Date | null;
      status: string;
      cancellation_reason: string | null;
      created_at: Date | null;
    },
    serviceName: string | null,
    duration: number | null,
  ) {
    return {
      id: a.id,
      userId: a.user_id,
      barbershopId: a.barbershop_id,
      barberId: a.barber_id,
      serviceId: a.service_id,
      serviceName,
      durationMinutes: duration,
      finalPrice: decToNumber(a.final_price),
      paymentMethod: a.payment_method,
      scheduledAt: a.scheduled_at,
      completedAt: a.completed_at,
      status: a.status,
      cancellationReason: a.cancellation_reason,
      createdAt: a.created_at,
    };
  }

  async create(userId: string, dto: AppointmentCreateDto) {
    const scheduledAt = new Date(dto.scheduledAt);
    if (scheduledAt.getTime() <= Date.now()) {
      throw new BadRequestException('scheduled_at_must_be_future');
    }

    const barbershop = await this.prisma.barbershop.findUnique({
      where: { id: dto.barbershopId },
    });
    if (!barbershop) throw new NotFoundException('barbershop_not_found');
    if (!barbershop.is_active)
      throw new BadRequestException('barbershop_inactive');

    const barber = await this.prisma.barber.findUnique({
      where: { id: dto.barberId },
    });
    if (!barber) throw new NotFoundException('barber_not_found');

    const link = await this.prisma.barbershopBarber.findFirst({
      where: { barbershop_id: dto.barbershopId, barber_id: dto.barberId },
    });
    if (!link) throw new BadRequestException('barber_not_in_barbershop');
    if (link.active !== true) {
      throw new BadRequestException('barber_not_active_in_barbershop');
    }

    const service = await this.prisma.catalogService.findUnique({
      where: { id: dto.serviceId },
    });
    if (!service) throw new NotFoundException('service_not_found');

    const conflict = await this.prisma.appointment.findFirst({
      where: {
        barber_id: dto.barberId,
        scheduled_at: scheduledAt,
        status: { not: 'CANCELLED' },
      },
    });
    if (conflict) throw new ConflictException('barber_time_slot_unavailable');

    const appointment = await this.prisma.appointment.create({
      data: {
        id: generateCuid(),
        user_id: userId,
        barbershop_id: dto.barbershopId,
        barber_id: dto.barberId,
        service_id: dto.serviceId,
        scheduled_at: scheduledAt,
        status: 'PENDING',
        payment_method: dto.paymentMethod,
        final_price: service.base_price,
        created_at: new Date(),
      },
    });

    return this.toResponse(appointment, service.name, service.duration_minutes);
  }

  async listMine(userId: string) {
    const rows = await this.prisma.appointment.findMany({
      where: { user_id: userId },
      orderBy: { scheduled_at: 'desc' },
    });
    const out = [];
    for (const a of rows) {
      const meta = a.service_id
        ? await this.getServiceMeta(a.service_id)
        : null;
      out.push(this.toResponse(a, meta?.name ?? null, meta?.duration ?? null));
    }
    return out;
  }

  async listByBarbershop(barbershopId: string, userId: string, role: string) {
    if (role === 'OWNER') {
      const b = await this.prisma.barbershop.findFirst({
        where: { id: barbershopId, owner_user_id: userId },
      });
      if (!b) throw new ForbiddenException('forbidden');
    } else if (role === 'BARBER') {
      const barber = await this.prisma.barber.findUnique({
        where: { user_id: userId },
      });
      if (!barber) throw new ForbiddenException('forbidden');
      const link = await this.prisma.barbershopBarber.findFirst({
        where: { barbershop_id: barbershopId, barber_id: barber.id },
      });
      if (!link) throw new ForbiddenException('barber_not_in_barbershop');
    } else {
      throw new ForbiddenException('forbidden');
    }

    const rows = await this.prisma.appointment.findMany({
      where: { barbershop_id: barbershopId },
      orderBy: { scheduled_at: 'desc' },
    });
    const out = [];
    for (const a of rows) {
      const meta = a.service_id
        ? await this.getServiceMeta(a.service_id)
        : null;
      out.push(this.toResponse(a, meta?.name ?? null, meta?.duration ?? null));
    }
    return out;
  }

  private async getAndCheckStaff(
    appointmentId: string,
    userId: string,
    role: string,
  ) {
    const a = await this.prisma.appointment.findUnique({
      where: { id: appointmentId },
    });
    if (!a) throw new NotFoundException('appointment_not_found');

    if (role === 'OWNER') {
      const b = await this.prisma.barbershop.findFirst({
        where: { id: a.barbershop_id!, owner_user_id: userId },
      });
      if (!b) throw new ForbiddenException('forbidden');
    } else if (role === 'BARBER') {
      const barber = await this.prisma.barber.findUnique({
        where: { user_id: userId },
      });
      if (!barber || a.barber_id !== barber.id)
        throw new ForbiddenException('forbidden');
    } else {
      throw new ForbiddenException('forbidden');
    }
    return a;
  }

  async confirm(appointmentId: string, userId: string, role: string) {
    const a = await this.getAndCheckStaff(appointmentId, userId, role);
    if (a.status !== 'PENDING')
      throw new BadRequestException('only_pending_can_be_confirmed');
    const updated = await this.prisma.appointment.update({
      where: { id: appointmentId },
      data: { status: 'CONFIRMED' },
    });
    const meta = updated.service_id
      ? await this.getServiceMeta(updated.service_id)
      : null;
    return this.toResponse(updated, meta?.name ?? null, meta?.duration ?? null);
  }

  async complete(
    appointmentId: string,
    userId: string,
    role: string,
    dto?: AppointmentCompleteDto,
  ) {
    const a = await this.getAndCheckStaff(appointmentId, userId, role);
    if (a.status !== 'CONFIRMED') {
      throw new BadRequestException('only_confirmed_can_be_completed');
    }
    const data: Prisma.AppointmentUpdateInput = {
      status: 'COMPLETED',
      completed_at: new Date(),
    };
    if (dto?.finalPrice != null) {
      data.final_price = new Prisma.Decimal(dto.finalPrice);
    }
    if (dto?.paymentMethod != null) {
      data.payment_method = dto.paymentMethod;
    }
    const updated = await this.prisma.appointment.update({
      where: { id: appointmentId },
      data,
    });
    const meta = updated.service_id
      ? await this.getServiceMeta(updated.service_id)
      : null;
    return this.toResponse(updated, meta?.name ?? null, meta?.duration ?? null);
  }

  async noShow(appointmentId: string, userId: string, role: string) {
    const a = await this.getAndCheckStaff(appointmentId, userId, role);
    if (a.status !== 'CONFIRMED') {
      throw new BadRequestException('only_confirmed_can_be_no_show');
    }
    const updated = await this.prisma.appointment.update({
      where: { id: appointmentId },
      data: { status: 'NO_SHOW' },
    });
    const meta = updated.service_id
      ? await this.getServiceMeta(updated.service_id)
      : null;
    return this.toResponse(updated, meta?.name ?? null, meta?.duration ?? null);
  }

  async cancel(
    appointmentId: string,
    userId: string,
    role: string,
    dto?: AppointmentCancelDto,
  ) {
    let a;
    if (role === 'CLIENT') {
      a = await this.prisma.appointment.findFirst({
        where: { id: appointmentId, user_id: userId },
      });
      if (!a) throw new NotFoundException('appointment_not_found');
    } else {
      a = await this.getAndCheckStaff(appointmentId, userId, role);
    }

    if (a.status === 'COMPLETED' || a.status === 'CANCELLED') {
      throw new BadRequestException('cannot_cancel_this_status');
    }

    const updated = await this.prisma.appointment.update({
      where: { id: appointmentId },
      data: {
        status: 'CANCELLED',
        cancellation_reason: dto?.cancellationReason ?? null,
      },
    });
    const meta = updated.service_id
      ? await this.getServiceMeta(updated.service_id)
      : null;
    return this.toResponse(updated, meta?.name ?? null, meta?.duration ?? null);
  }
}
