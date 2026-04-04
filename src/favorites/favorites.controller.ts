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
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { FavoritesService } from './favorites.service';

@Controller('users/:userId/favorites')
export class FavoritesController {
  constructor(private readonly favorites: FavoritesService) {}

  @Get()
  list(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.favorites.list(userId, user.userId);
  }

  @Post()
  @HttpCode(HttpStatus.CREATED)
  add(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: { barberId?: string; barbershopId?: string },
  ) {
    return this.favorites.add(userId, user.userId, body);
  }

  @Delete(':favoriteId')
  @HttpCode(HttpStatus.NO_CONTENT)
  async remove(
    @Param('userId') userId: string,
    @Param('favoriteId') favoriteId: string,
    @CurrentUser() user: JwtUser,
  ) {
    await this.favorites.remove(userId, user.userId, favoriteId);
  }
}
