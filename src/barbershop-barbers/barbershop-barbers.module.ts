import { Module } from '@nestjs/common';
import { BarbershopBarbersController } from './barbershop-barbers.controller';
import { BarbershopBarbersService } from './barbershop-barbers.service';

@Module({
  controllers: [BarbershopBarbersController],
  providers: [BarbershopBarbersService],
})
export class BarbershopBarbersModule {}
