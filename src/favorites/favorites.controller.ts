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
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { FavoritesService } from './favorites.service';

@ApiTags('Favorites')
@ApiBearerAuth('access-token')
@Controller('users/:userId/favorites')
export class FavoritesController {
  constructor(private readonly favorites: FavoritesService) {}

  @Get()
  @ApiOperation({ summary: 'Listar favoritos do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiOkResponse({ description: 'Favoritos retornados' })
  list(@Param('userId') userId: string, @CurrentUser() user: JwtUser) {
    return this.favorites.list(userId, user.userId);
  }

  @Post()
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Adicionar favorito para o usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        barberId: { type: 'string', example: 'clx123-barber-id' },
        barbershopId: { type: 'string', example: 'clx123-barbershop-id' },
      },
      example: { barberId: 'clx123-barber-id' },
    },
  })
  @ApiOkResponse({ description: 'Favorito adicionado' })
  add(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: { barberId?: string; barbershopId?: string },
  ) {
    return this.favorites.add(userId, user.userId, body);
  }

  @Delete(':favoriteId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiOperation({ summary: 'Remover favorito do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiParam({ name: 'favoriteId', example: 'clx123-favorite-id' })
  @ApiNoContentResponse({ description: 'Favorito removido' })
  async remove(
    @Param('userId') userId: string,
    @Param('favoriteId') favoriteId: string,
    @CurrentUser() user: JwtUser,
  ) {
    await this.favorites.remove(userId, user.userId, favoriteId);
  }
}
