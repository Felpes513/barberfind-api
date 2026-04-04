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
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { Public } from '../common/public.decorator';
import { Roles } from '../common/roles.decorator';
import { BarbershopBarbersService } from './barbershop-barbers.service';

@Controller('barbershops/:barbershopId/barbers')
export class BarbershopBarbersController {
  constructor(private readonly links: BarbershopBarbersService) {}

  @Public()
  @Get()
  list(@Param('barbershopId') barbershopId: string) {
    return this.links.list(barbershopId);
  }

  @Roles('BARBER')
  @Post('request')
  request(
    @Param('barbershopId') barbershopId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.links.requestEntry(barbershopId, user.userId);
  }

  @Roles('OWNER')
  @Patch(':linkId/approve')
  approve(
    @Param('barbershopId') barbershopId: string,
    @Param('linkId') linkId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.links.approve(barbershopId, user.userId, linkId);
  }

  @Roles('OWNER')
  @Patch(':linkId/reject')
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
  async remove(
    @Param('barbershopId') barbershopId: string,
    @Param('linkId') linkId: string,
    @CurrentUser() user: JwtUser,
  ) {
    await this.links.remove(barbershopId, user.userId, user.role, linkId);
  }
}
