import { Body, Controller, Get, Param, Patch, Post, Put } from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiBody,
  ApiOkResponse,
  ApiOperation,
  ApiParam,
  ApiTags,
} from '@nestjs/swagger';
import { CurrentUser } from '../common/current-user.decorator';
import { Roles } from '../common/roles.decorator';
import { Public } from '../common/public.decorator';
import { BarbershopsService } from './barbershops.service';
import { BarbershopCreateDto } from './dto/barbershop-create.dto';
import { BarbershopStatusDto } from './dto/barbershop-status.dto';
import { BarbershopUpdateDto } from './dto/barbershop-update.dto';

@ApiTags('Barbershops')
@Controller()
export class BarbershopsController {
  constructor(private readonly barbershops: BarbershopsService) {}

  @Public()
  @Get('barbershops')
  @ApiOperation({ summary: 'Listar barbearias publicas' })
  @ApiOkResponse({ description: 'Lista de barbearias retornada' })
  listPublic() {
    return this.barbershops.listPublic();
  }

  @Public()
  @Get('barbershops/:id')
  @ApiOperation({ summary: 'Buscar detalhes publicos de uma barbearia' })
  @ApiParam({ name: 'id', example: 'clx123-barbershop-id' })
  @ApiOkResponse({ description: 'Barbearia encontrada' })
  getPublic(@Param('id') id: string) {
    return this.barbershops.getPublicById(id);
  }

  @Roles('OWNER')
  @Post('barbershops')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Criar nova barbearia (OWNER)' })
  @ApiBody({ type: BarbershopCreateDto })
  @ApiOkResponse({ description: 'Barbearia criada com sucesso' })
  create(
    @CurrentUser() user: { userId: string },
    @Body() body: BarbershopCreateDto,
  ) {
    return this.barbershops.create(user.userId, body);
  }

  @Roles('OWNER')
  @Put('barbershops/:id')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Atualizar dados da barbearia (OWNER)' })
  @ApiParam({ name: 'id', example: 'clx123-barbershop-id' })
  @ApiBody({ type: BarbershopUpdateDto })
  @ApiOkResponse({ description: 'Barbearia atualizada' })
  update(
    @Param('id') id: string,
    @CurrentUser() user: { userId: string },
    @Body() body: BarbershopUpdateDto,
  ) {
    return this.barbershops.update(id, user.userId, body);
  }

  @Roles('OWNER')
  @Patch('barbershops/:id/status')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Ativar ou desativar barbearia (OWNER)' })
  @ApiParam({ name: 'id', example: 'clx123-barbershop-id' })
  @ApiBody({ type: BarbershopStatusDto })
  @ApiOkResponse({ description: 'Status da barbearia atualizado' })
  setStatus(
    @Param('id') id: string,
    @CurrentUser() user: { userId: string },
    @Body() body: BarbershopStatusDto,
  ) {
    return this.barbershops.setStatus(id, user.userId, body);
  }

  @Roles('OWNER')
  @Get('owners/me/barbershops')
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Listar barbearias do owner autenticado' })
  @ApiOkResponse({ description: 'Lista de barbearias do owner' })
  listMine(@CurrentUser() user: { userId: string }) {
    return this.barbershops.listMine(user.userId);
  }
}
