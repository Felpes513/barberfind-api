import { Module } from '@nestjs/common';
import { AppointmentsController } from './appointments.controller';
import { AppointmentsService } from './appointments.service';
import { BarbershopAppointmentsController } from './barbershop-appointments.controller';

@Module({
  controllers: [AppointmentsController, BarbershopAppointmentsController],
  providers: [AppointmentsService],
})
export class AppointmentsModule {}
