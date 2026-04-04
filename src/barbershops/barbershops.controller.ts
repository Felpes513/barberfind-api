import {
  Body,
  Controller,
  Get,
  Param,
  Patch,
  Post,
  Put,
} from '@nestjs/common';
import { CurrentUser } from '../common/current-user.decorator';
import { Roles } from '../common/roles.decorator';
import { Public } from '../common/public.decorator';
import { BarbershopsService } from './barbershops.service';
import { BarbershopCreateDto } from './dto/barbershop-create.dto';
import { BarbershopStatusDto } from './dto/barbershop-status.dto';
import { BarbershopUpdateDto } from './dto/barbershop-update.dto';

@Controller()
export class BarbershopsController {
  constructor(private readonly barbershops: BarbershopsService) {}

  @Public()
  @Get('barbershops')
  listPublic() {
    return this.barbershops.listPublic();
  }

  @Public()
  @Get('barbershops/:id')
  getPublic(@Param('id') id: string) {
    return this.barbershops.getPublicById(id);
  }

  @Roles('OWNER')
  @Post('barbershops')
  create(
    @CurrentUser() user: { userId: string },
    @Body() body: BarbershopCreateDto,
  ) {
    return this.barbershops.create(user.userId, body);
  }

  @Roles('OWNER')
  @Put('barbershops/:id')
  update(
    @Param('id') id: string,
    @CurrentUser() user: { userId: string },
    @Body() body: BarbershopUpdateDto,
  ) {
    return this.barbershops.update(id, user.userId, body);
  }

  @Roles('OWNER')
  @Patch('barbershops/:id/status')
  setStatus(
    @Param('id') id: string,
    @CurrentUser() user: { userId: string },
    @Body() body: BarbershopStatusDto,
  ) {
    return this.barbershops.setStatus(id, user.userId, body);
  }

  @Roles('OWNER')
  @Get('owners/me/barbershops')
  listMine(@CurrentUser() user: { userId: string }) {
    return this.barbershops.listMine(user.userId);
  }
}
