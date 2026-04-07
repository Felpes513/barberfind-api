import {
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  Patch,
  Post,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiNoContentResponse,
  ApiOkResponse,
  ApiOperation,
  ApiParam,
  ApiTags,
} from '@nestjs/swagger';
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { Public } from '../common/public.decorator';
import { Roles } from '../common/roles.decorator';
import { BarbershopBarbersService } from './barbershop-barbers.service';

@ApiTags('Barbershop Barbers')
@Controller('barbershops/:barbershopId/barbers')
export class BarbershopBarbersController {
  constructor(private readonly links: BarbershopBarbersService) {}

  @Public()
  @Get()
  @ApiOperation({ summary: 'Listar barbeiros vinculados a barbearia' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiOkResponse({ description: 'Barbeiros listados com sucesso' })
  list(@Param('barbershopId') barbershopId: string) {
    return this.links.list(barbershopId);
  }

  @Roles('BARBER')
  @Post('request')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Solicitar vinculacao com barbearia (BARBER)' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiOkResponse({ description: 'Solicitacao enviada' })
  request(
    @Param('barbershopId') barbershopId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.links.requestEntry(barbershopId, user.userId);
  }

  @Roles('OWNER')
  @Patch(':linkId/approve')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Aprovar solicitacao de barbeiro (OWNER)' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiParam({ name: 'linkId', example: 'clx123-link-id' })
  @ApiOkResponse({ description: 'Solicitacao aprovada' })
  approve(
    @Param('barbershopId') barbershopId: string,
    @Param('linkId') linkId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.links.approve(barbershopId, user.userId, linkId);
  }

  @Roles('OWNER')
  @Patch(':linkId/reject')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Rejeitar solicitacao de barbeiro (OWNER)' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiParam({ name: 'linkId', example: 'clx123-link-id' })
  @ApiOkResponse({ description: 'Solicitacao rejeitada' })
  reject(
    @Param('barbershopId') barbershopId: string,
    @Param('linkId') linkId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.links.reject(barbershopId, user.userId, linkId);
  }

  @Roles('OWNER', 'BARBER')
  @Delete(':linkId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Remover vinculo de barbeiro/barbearia' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiParam({ name: 'linkId', example: 'clx123-link-id' })
  @ApiNoContentResponse({ description: 'Vinculo removido' })
  async remove(
    @Param('barbershopId') barbershopId: string,
    @Param('linkId') linkId: string,
    @CurrentUser() user: JwtUser,
  ) {
    await this.links.remove(barbershopId, user.userId, user.role, linkId);
  }
}
