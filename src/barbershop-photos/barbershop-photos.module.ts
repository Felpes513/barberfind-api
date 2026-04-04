import { Module } from '@nestjs/common';
import { BarbershopPhotosController } from './barbershop-photos.controller';
import { BarbershopPhotosService } from './barbershop-photos.service';

@Module({
  controllers: [BarbershopPhotosController],
  providers: [BarbershopPhotosService],
})
export class BarbershopPhotosModule {}
