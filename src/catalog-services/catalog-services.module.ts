import { Module } from '@nestjs/common';
import { BarbershopCatalogController } from './barbershop-catalog.controller';
import { CatalogServicesService } from './catalog-services.service';
import { GlobalCatalogController } from './global-catalog.controller';

@Module({
  controllers: [GlobalCatalogController, BarbershopCatalogController],
  providers: [CatalogServicesService],
})
export class CatalogServicesModule {}
