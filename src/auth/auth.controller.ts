import {
  Body,
  Controller,
  Headers,
  HttpCode,
  HttpStatus,
  Post,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiBody,
  ApiCreatedResponse,
  ApiNoContentResponse,
  ApiOperation,
  ApiTags,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { Public } from '../common/public.decorator';
import { AuthService } from './auth.service';
import { LoginDto } from './dto/login.dto';
import { RegisterBarberDto } from './dto/register-barber.dto';
import { RegisterClientDto } from './dto/register-client.dto';
import { RegisterOwnerDto } from './dto/register-owner.dto';

@ApiTags('Auth')
@Controller('auth')
export class AuthController {
  constructor(private readonly auth: AuthService) {}

  @Public()
  @Post('login')
  @ApiOperation({ summary: 'Autenticar usuario e gerar token JWT' })
  @ApiBody({
    type: LoginDto,
    examples: {
      default: {
        value: { email: 'cliente@barberfind.com', password: 'SenhaForte123' },
      },
    },
  })
  @ApiCreatedResponse({
    description: 'Login realizado com sucesso',
    schema: {
      example: {
        accessToken: 'jwt.token.aqui',
        refreshToken: 'refresh.token.aqui',
        user: { id: 'clx123', role: 'CLIENT', email: 'cliente@barberfind.com' },
      },
    },
  })
  async login(@Body() dto: LoginDto) {
    return this.auth.login(dto);
  }

  @Post('logout')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiBearerAuth('access-token')
  @ApiOperation({ summary: 'Encerrar sessao do usuario autenticado' })
  @ApiNoContentResponse({ description: 'Logout realizado com sucesso' })
  @ApiUnauthorizedResponse({ description: 'Token ausente ou invalido' })
  async logout(@Headers('authorization') authHeader?: string) {
    let token: string | undefined;
    if (authHeader?.startsWith('Bearer ')) {
      token = authHeader.slice(7);
    }
    await this.auth.logout(token);
  }

  @Public()
  @Post('register/client')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Registrar conta de cliente' })
  @ApiBody({ type: RegisterClientDto })
  @ApiCreatedResponse({ description: 'Cliente criado com sucesso' })
  async registerClient(@Body() dto: RegisterClientDto) {
    return this.auth.registerClient(dto);
  }

  @Public()
  @Post('register/barber')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Registrar conta de barbeiro' })
  @ApiBody({ type: RegisterBarberDto })
  @ApiCreatedResponse({ description: 'Barbeiro criado com sucesso' })
  async registerBarber(@Body() dto: RegisterBarberDto) {
    return this.auth.registerBarber(dto);
  }

  @Public()
  @Post('register/owner')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Registrar conta de dono de barbearia' })
  @ApiBody({ type: RegisterOwnerDto })
  @ApiCreatedResponse({ description: 'Owner criado com sucesso' })
  async registerOwner(@Body() dto: RegisterOwnerDto) {
    return this.auth.registerOwner(dto);
  }
}
