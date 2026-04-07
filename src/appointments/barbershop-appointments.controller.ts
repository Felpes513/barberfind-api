import { Controller, Get, Param } from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiOkResponse,
  ApiOperation,
  ApiParam,
  ApiTags,
} from '@nestjs/swagger';
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { Roles } from '../common/roles.decorator';
import { AppointmentsService } from './appointments.service';

@ApiTags('Barbershop Appointments')
@Controller('barbershops/:barbershopId/appointments')
export class BarbershopAppointmentsController {
  constructor(private readonly appointments: AppointmentsService) {}

  @Roles('OWNER', 'BARBER')
  @Get()
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Listar agendamentos da barbearia (OWNER/BARBER)' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiOkResponse({ description: 'Agendamentos da barbearia retornados' })
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
