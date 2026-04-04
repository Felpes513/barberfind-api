import { Controller, Get, Param } from '@nestjs/common';
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { Roles } from '../common/roles.decorator';
import { AppointmentsService } from './appointments.service';

@Controller('barbershops/:barbershopId/appointments')
export class BarbershopAppointmentsController {
  constructor(private readonly appointments: AppointmentsService) {}

  @Roles('OWNER', 'BARBER')
  @Get()
  list(
    @Param('barbershopId') barbershopId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.appointments.listByBarbershop(
      barbershopId,
      user.userId,
      user.role,
    );
  }
}
