import { Body, Controller, Get, Param, Patch, Post } from '@nestjs/common';
import { HttpCode, HttpStatus } from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiBody,
  ApiOkResponse,
  ApiOperation,
  ApiParam,
  ApiTags,
} from '@nestjs/swagger';
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { Roles } from '../common/roles.decorator';
import { AppointmentsService } from './appointments.service';
import {
  AppointmentCancelDto,
  AppointmentCompleteDto,
  AppointmentCreateDto,
} from './dto/appointment.dto';

@ApiTags('Appointments')
@Controller('appointments')
export class AppointmentsController {
  constructor(private readonly appointments: AppointmentsService) {}

  @Roles('CLIENT')
  @Post()
  @HttpCode(HttpStatus.CREATED)
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Criar agendamento (CLIENT)' })
  @ApiBody({ type: AppointmentCreateDto })
  @ApiOkResponse({ description: 'Agendamento criado com sucesso' })
  create(@CurrentUser() user: JwtUser, @Body() body: AppointmentCreateDto) {
    return this.appointments.create(user.userId, body);
  }

  @Roles('CLIENT')
  @Get('me')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Listar meus agendamentos (CLIENT)' })
  @ApiOkResponse({ description: 'Lista de agendamentos do cliente' })
  listMine(@CurrentUser() user: JwtUser) {
    return this.appointments.listMine(user.userId);
  }

  @Roles('BARBER', 'OWNER')
  @Patch(':id/confirm')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Confirmar agendamento (BARBER/OWNER)' })
  @ApiParam({ name: 'id', example: 'clx123-appointment-id' })
  @ApiOkResponse({ description: 'Agendamento confirmado' })
  confirm(@Param('id') id: string, @CurrentUser() user: JwtUser) {
    return this.appointments.confirm(id, user.userId, user.role);
  }

  @Roles('BARBER', 'OWNER')
  @Patch(':id/complete')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Marcar agendamento como concluido (BARBER/OWNER)' })
  @ApiParam({ name: 'id', example: 'clx123-appointment-id' })
  @ApiBody({ type: AppointmentCompleteDto, required: false })
  @ApiOkResponse({ description: 'Agendamento concluido' })
  complete(
    @Param('id') id: string,
    @CurrentUser() user: JwtUser,
    @Body() body?: AppointmentCompleteDto,
  ) {
    return this.appointments.complete(id, user.userId, user.role, body);
  }

  @Roles('BARBER', 'OWNER')
  @Patch(':id/no-show')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Marcar no-show no agendamento (BARBER/OWNER)' })
  @ApiParam({ name: 'id', example: 'clx123-appointment-id' })
  @ApiOkResponse({ description: 'Agendamento marcado como no-show' })
  noShow(@Param('id') id: string, @CurrentUser() user: JwtUser) {
    return this.appointments.noShow(id, user.userId, user.role);
  }

  @Patch(':id/cancel')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Cancelar agendamento (usuario autenticado)' })
  @ApiParam({ name: 'id', example: 'clx123-appointment-id' })
  @ApiBody({ type: AppointmentCancelDto, required: false })
  @ApiOkResponse({ description: 'Agendamento cancelado' })
  cancel(
    @Param('id') id: string,
    @CurrentUser() user: JwtUser,
    @Body() body?: AppointmentCancelDto,
  ) {
    return this.appointments.cancel(id, user.userId, user.role, body);
  }
}
