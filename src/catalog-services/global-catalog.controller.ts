import { Body, Controller, Get, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { Public } from '../common/public.decorator';
import { Roles } from '../common/roles.decorator';
import { CatalogServicesService } from './catalog-services.service';
import { ServiceCreateDto } from './dto/service-create.dto';

@Controller('services')
export class GlobalCatalogController {
  constructor(private readonly catalog: CatalogServicesService) {}

  @Roles('OWNER')
  @Post()
  @HttpCode(HttpStatus.CREATED)
  createGlobal(@Body() body: ServiceCreateDto) {
    return this.catalog.createGlobal(body);
  }

  @Public()
  @Get()
  listGlobal() {
    return this.catalog.listGlobal();
  }
}
