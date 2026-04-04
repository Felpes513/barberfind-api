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
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import { Public } from '../common/public.decorator';
import { Roles } from '../common/roles.decorator';
import { CatalogServicesService } from './catalog-services.service';
import {
  BarbershopServiceLinkDto,
  BarbershopServiceUpdateDto,
} from './dto/barbershop-service-link.dto';

@Controller('barbershops/:barbershopId/services')
export class BarbershopCatalogController {
  constructor(private readonly catalog: CatalogServicesService) {}

  @Public()
  @Get()
  listByBarbershop(@Param('barbershopId') barbershopId: string) {
    return this.catalog.listByBarbershop(barbershopId);
  }

  @Roles('OWNER')
  @Post()
  @HttpCode(HttpStatus.CREATED)
  link(
    @Param('barbershopId') barbershopId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: BarbershopServiceLinkDto,
  ) {
    return this.catalog.link(barbershopId, user.userId, body);
  }

  @Roles('OWNER')
  @Put(':linkId')
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
  async unlink(
    @Param('barbershopId') barbershopId: string,
    @Param('linkId') linkId: string,
    @CurrentUser() user: JwtUser,
  ) {
    await this.catalog.unlink(barbershopId, linkId, user.userId);
    return { message: 'unlinked' };
  }
}
