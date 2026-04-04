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
import { CurrentUser } from '../common/current-user.decorator';
import { Public } from '../common/public.decorator';
import { Roles } from '../common/roles.decorator';
import { BarbershopPhotosService } from './barbershop-photos.service';
import { PhotoAddDto } from './dto/photo-add.dto';

@Controller('barbershops/:barbershopId/photos')
export class BarbershopPhotosController {
  constructor(private readonly photos: BarbershopPhotosService) {}

  @Public()
  @Get()
  list(@Param('barbershopId') barbershopId: string) {
    return this.photos.list(barbershopId);
  }

  @Roles('OWNER')
  @Post()
  @HttpCode(HttpStatus.CREATED)
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
  async remove(
    @Param('barbershopId') barbershopId: string,
    @Param('photoId') photoId: string,
    @CurrentUser() user: { userId: string },
  ) {
    await this.photos.delete(barbershopId, user.userId, photoId);
  }
}
