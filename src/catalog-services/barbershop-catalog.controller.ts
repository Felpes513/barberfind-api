import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  Post,
  Put,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiBody,
  ApiOkResponse,
  ApiOperation,
  ApiParam,
  ApiTags,
} from '@nestjs/swagger';
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { Public } from '../common/public.decorator';
import { Roles } from '../common/roles.decorator';
import { CatalogServicesService } from './catalog-services.service';
import {
  BarbershopServiceLinkDto,
  BarbershopServiceUpdateDto,
} from './dto/barbershop-service-link.dto';

@ApiTags('Barbershop Services')
@Controller('barbershops/:barbershopId/services')
export class BarbershopCatalogController {
  constructor(private readonly catalog: CatalogServicesService) {}

  @Public()
  @Get()
  @ApiOperation({ summary: 'Listar servicos de uma barbearia' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiOkResponse({ description: 'Servicos da barbearia retornados' })
  listByBarbershop(@Param('barbershopId') barbershopId: string) {
    return this.catalog.listByBarbershop(barbershopId);
  }

  @Roles('OWNER')
  @Post()
  @HttpCode(HttpStatus.CREATED)
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Vincular servico global a barbearia (OWNER)' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiBody({ type: BarbershopServiceLinkDto })
  @ApiOkResponse({ description: 'Servico vinculado a barbearia' })
  link(
    @Param('barbershopId') barbershopId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: BarbershopServiceLinkDto,
  ) {
    return this.catalog.link(barbershopId, user.userId, body);
  }

  @Roles('OWNER')
  @Put(':linkId')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Atualizar preco customizado do servico (OWNER)' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiParam({ name: 'linkId', example: 'clx123-link-id' })
  @ApiBody({ type: BarbershopServiceUpdateDto })
  @ApiOkResponse({ description: 'Vinculo atualizado' })
  updateLink(
    @Param('barbershopId') barbershopId: string,
    @Param('linkId') linkId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: BarbershopServiceUpdateDto,
  ) {
    return this.catalog.updateLink(barbershopId, linkId, user.userId, body);
  }

  @Roles('OWNER')
  @Delete(':linkId')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Desvincular servico da barbearia (OWNER)' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiParam({ name: 'linkId', example: 'clx123-link-id' })
  @ApiOkResponse({ description: 'Servico desvinculado' })
  async unlink(
    @Param('barbershopId') barbershopId: string,
    @Param('linkId') linkId: string,
    @CurrentUser() user: JwtUser,
  ) {
    await this.catalog.unlink(barbershopId, linkId, user.userId);
    return { message: 'unlinked' };
  }
}
