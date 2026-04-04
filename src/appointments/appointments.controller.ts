import { Body, Controller, Get, Param, Patch, Post } from '@nestjs/common';
import { HttpCode, HttpStatus } from '@nestjs/common';
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { Roles } from '../common/roles.decorator';
import { AppointmentsService } from './appointments.service';
import {
  AppointmentCancelDto,
  AppointmentCompleteDto,
  AppointmentCreateDto,
} from './dto/appointment.dto';

@Controller('appointments')
export class AppointmentsController {
  constructor(private readonly appointments: AppointmentsService) {}

  @Roles('CLIENT')
  @Post()
  @HttpCode(HttpStatus.CREATED)
  create(@CurrentUser() user: JwtUser, @Body() body: AppointmentCreateDto) {
    return this.appointments.create(user.userId, body);
  }

  @Roles('CLIENT')
  @Get('me')
  listMine(@CurrentUser() user: JwtUser) {
    return this.appointments.listMine(user.userId);
  }

  @Roles('BARBER', 'OWNER')
  @Patch(':id/confirm')
  confirm(@Param('id') id: string, @CurrentUser() user: JwtUser) {
    return this.appointments.confirm(id, user.userId, user.role);
  }

  @Roles('BARBER', 'OWNER')
  @Patch(':id/complete')
  complete(
    @Param('id') id: string,
    @CurrentUser() user: JwtUser,
    @Body() body?: AppointmentCompleteDto,
  ) {
    return this.appointments.complete(id, user.userId, user.role, body);
  }

  @Roles('BARBER', 'OWNER')
  @Patch(':id/no-show')
  noShow(@Param('id') id: string, @CurrentUser() user: JwtUser) {
    return this.appointments.noShow(id, user.userId, user.role);
  }

  @Patch(':id/cancel')
  cancel(
    @Param('id') id: string,
    @CurrentUser() user: JwtUser,
    @Body() body?: AppointmentCancelDto,
  ) {
    return this.appointments.cancel(id, user.userId, user.role, body);
  }
}
