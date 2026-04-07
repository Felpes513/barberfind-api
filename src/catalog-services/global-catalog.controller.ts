import {
  Body,
  Controller,
  Get,
  HttpCode,
  HttpStatus,
  Post,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiBody,
  ApiOkResponse,
  ApiOperation,
  ApiTags,
} from '@nestjs/swagger';
import { Public } from '../common/public.decorator';
import { Roles } from '../common/roles.decorator';
import { CatalogServicesService } from './catalog-services.service';
import { ServiceCreateDto } from './dto/service-create.dto';

@ApiTags('Global Services')
@Controller('services')
export class GlobalCatalogController {
  constructor(private readonly catalog: CatalogServicesService) {}

  @Roles('OWNER')
  @Post()
  @HttpCode(HttpStatus.CREATED)
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Criar servico global do catalogo (OWNER)' })
  @ApiBody({ type: ServiceCreateDto })
  @ApiOkResponse({ description: 'Servico global criado' })
  createGlobal(@Body() body: ServiceCreateDto) {
    return this.catalog.createGlobal(body);
  }

  @Public()
  @Get()
  @ApiOperation({ summary: 'Listar catalogo global de servicos' })
  @ApiOkResponse({ description: 'Servicos globais retornados' })
  listGlobal() {
    return this.catalog.listGlobal();
  }
}
