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

@ApiTags('Users')
@ApiBearerAuth('access-token')
@Controller('users/:userId')
export class UsersController {
  constructor(private readonly users: UsersService) {}

  @Patch()
  @ApiOperation({ summary: 'Atualizar perfil do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiBody({ type: UpdateUserDto })
  @ApiOkResponse({ description: 'Perfil atualizado com sucesso' })
  updateUser(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: UpdateUserDto,
  ) {
    return this.users.updateUser(userId, user.userId, body);
  }

  @Put('password')
  @ApiOperation({ summary: 'Alterar senha do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiBody({ type: ChangePasswordDto })
  @ApiOkResponse({ description: 'Senha alterada com sucesso' })
  changePassword(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: ChangePasswordDto,
  ) {
    return this.users.changePassword(userId, user.userId, body);
  }

  @Get('payment-methods')
  @ApiOperation({ summary: 'Listar metodos de pagamento do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiOkResponse({ description: 'Metodos de pagamento retornados' })
  listPaymentMethods(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.users.listPaymentMethods(userId, user.userId);
  }

  @Post('payment-methods')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Adicionar metodo de pagamento para usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiBody({ type: PaymentMethodDto })
  @ApiOkResponse({ description: 'Metodo de pagamento adicionado' })
  addPaymentMethod(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: PaymentMethodDto,
  ) {
    return this.users.addPaymentMethod(userId, user.userId, body);
  }

  @Delete('payment-methods/:methodId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiOperation({ summary: 'Remover metodo de pagamento do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiParam({ name: 'methodId', example: 'clx123-method-id' })
  @ApiNoContentResponse({ description: 'Metodo removido com sucesso' })
  async deletePaymentMethod(
    @Param('userId') userId: string,
    @Param('methodId') methodId: string,
    @CurrentUser() user: JwtUser,
  ) {
    await this.users.deletePaymentMethod(userId, user.userId, methodId);
  }

  @Get('preferences')
  @ApiOperation({ summary: 'Obter preferencias do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiOkResponse({ description: 'Preferencias retornadas' })
  getPreferences(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
  ) {
    return this.users.getPreferences(userId, user.userId);
  }

  @Put('preferences')
  @ApiOperation({ summary: 'Atualizar preferencias do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiBody({ type: UserPreferencesDto })
  @ApiOkResponse({ description: 'Preferencias atualizadas' })
  updatePreferences(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: UserPreferencesDto,
  ) {
    return this.users.updatePreferences(userId, user.userId, body);
  }

  @Post('accept-terms')
  @ApiOperation({ summary: 'Registrar aceite de termos' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiBody({ type: AcceptTermsDto })
  @ApiOkResponse({ description: 'Aceite de termos registrado' })
  acceptTerms(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: AcceptTermsDto,
  ) {
    return this.users.acceptTerms(userId, user.userId, body);
  }

  @Get('documents')
  @ApiOperation({ summary: 'Listar documentos do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiOkResponse({ description: 'Documentos retornados' })
  listDocuments(@Param('userId') userId: string, @CurrentUser() user: JwtUser) {
    return this.users.listDocuments(userId, user.userId);
  }

  @Post('documents')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Adicionar documento do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiBody({ type: DocumentCreateDto })
  @ApiOkResponse({ description: 'Documento adicionado' })
  addDocument(
    @Param('userId') userId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: DocumentCreateDto,
  ) {
    return this.users.addDocument(userId, user.userId, body);
  }

  @Put('documents/:documentId')
  @ApiOperation({ summary: 'Atualizar documento do usuario' })
  @ApiParam({ name: 'userId', example: 'clx123-user-id' })
  @ApiParam({ name: 'documentId', example: 'clx123-document-id' })
  @ApiBody({ type: DocumentUpdateDto })
  @ApiOkResponse({ description: 'Documento atualizado' })
  updateDocument(
    @Param('userId') userId: string,
    @Param('documentId') documentId: string,
    @CurrentUser() user: JwtUser,
    @Body() body: DocumentUpdateDto,
  ) {
    return this.users.updateDocument(userId, user.userId, documentId, body);
  }
}
