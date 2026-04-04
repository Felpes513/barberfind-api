import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  Patch,
  Post,
  Put,
} from '@nestjs/common';
import { CurrentUser, JwtUser } from '../common/current-user.decorator';
import {
  AcceptTermsDto,
  ChangePasswordDto,
  DocumentCreateDto,
  DocumentUpdateDto,
  PaymentMethodDto,
  UpdateUserDto,
  UserPreferencesDto,
} from './dto/user-dtos';
import { UsersService } from './users.service';

@Controller('users/:userId')
export class UsersController {
  constructor(private readonly users: UsersService) {}

  @Patch()
  updateUser(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: UpdateUserDto,
  ) {
    return this.users.updateUser(userId, user.userId, body);
  }

  @Put('password')
  changePassword(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: ChangePasswordDto,
  ) {
    return this.users.changePassword(userId, user.userId, body);
  }

  @Get('payment-methods')
  listPaymentMethods(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.users.listPaymentMethods(userId, user.userId);
  }

  @Post('payment-methods')
  @HttpCode(HttpStatus.CREATED)
  addPaymentMethod(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: PaymentMethodDto,
  ) {
    return this.users.addPaymentMethod(userId, user.userId, body);
  }

  @Delete('payment-methods/:methodId')
  @HttpCode(HttpStatus.NO_CONTENT)
  async deletePaymentMethod(
    @Param('userId') userId: string,
    @Param('methodId') methodId: string,
    @CurrentUser() user: JwtUser,
  ) {
    await this.users.deletePaymentMethod(userId, user.userId, methodId);
  }

  @Get('preferences')
  getPreferences(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.users.getPreferences(userId, user.userId);
  }

  @Put('preferences')
  updatePreferences(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: UserPreferencesDto,
  ) {
    return this.users.updatePreferences(userId, user.userId, body);
  }

  @Post('accept-terms')
  acceptTerms(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: AcceptTermsDto,
  ) {
    return this.users.acceptTerms(userId, user.userId, body);
  }

  @Get('documents')
  listDocuments(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.users.listDocuments(userId, user.userId);
  }

  @Post('documents')
  @HttpCode(HttpStatus.CREATED)
  addDocument(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: DocumentCreateDto,
  ) {
    return this.users.addDocument(userId, user.userId, body);
  }

  @Put('documents/:documentId')
  updateDocument(
    @Param('userId') userId: string,
    @Param('documentId') documentId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: DocumentUpdateDto,
  ) {
    return this.users.updateDocument(userId, user.userId, documentId, body);
  }
}
