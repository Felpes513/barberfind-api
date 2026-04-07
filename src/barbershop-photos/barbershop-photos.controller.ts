import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  Post,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiBody,
  ApiNoContentResponse,
  ApiOkResponse,
  ApiOperation,
  ApiParam,
  ApiTags,
} from '@nestjs/swagger';
import { CurrentUser } from '../common/current-user.decorator';
import { Public } from '../common/public.decorator';
import { Roles } from '../common/roles.decorator';
import { BarbershopPhotosService } from './barbershop-photos.service';
import { PhotoAddDto } from './dto/photo-add.dto';

@ApiTags('Barbershop Photos')
@Controller('barbershops/:barbershopId/photos')
export class BarbershopPhotosController {
  constructor(private readonly photos: BarbershopPhotosService) {}

  @Public()
  @Get()
  @ApiOperation({ summary: 'Listar fotos da barbearia' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiOkResponse({ description: 'Fotos da barbearia retornadas' })
  list(@Param('barbershopId') barbershopId: string) {
    return this.photos.list(barbershopId);
  }

  @Roles('OWNER')
  @Post()
  @HttpCode(HttpStatus.CREATED)
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Adicionar foto na barbearia (OWNER)' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiBody({ type: PhotoAddDto })
  @ApiOkResponse({ description: 'Foto adicionada com sucesso' })
  add(
    @Param('barbershopId') barbershopId: string,
    @CurrentUser() user: { userId: string },
    @Body() body: PhotoAddDto,
  ) {
    return this.photos.add(barbershopId, user.userId, body);
  }

  @Roles('OWNER')
  @Delete(':photoId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Remover foto da barbearia (OWNER)' })
  @ApiParam({ name: 'barbershopId', example: 'clx123-barbershop-id' })
  @ApiParam({ name: 'photoId', example: 'clx123-photo-id' })
  @ApiNoContentResponse({ description: 'Foto removida com sucesso' })
  async remove(
    @Param('barbershopId') barbershopId: string,
    @Param('photoId') photoId: string,
    @CurrentUser() user: { userId: string },
  ) {
    await this.photos.delete(barbershopId, user.userId, photoId);
  }
}
