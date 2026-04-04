import {
  Body,
  Controller,
  Headers,
  HttpCode,
  HttpStatus,
  Post,
} from '@nestjs/common';
import { Public } from '../common/public.decorator';
import { AuthService } from './auth.service';
import { LoginDto } from './dto/login.dto';
import { RegisterBarberDto } from './dto/register-barber.dto';
import { RegisterClientDto } from './dto/register-client.dto';
import { RegisterOwnerDto } from './dto/register-owner.dto';

@Controller('auth')
export class AuthController {
  constructor(private readonly auth: AuthService) {}

  @Public()
  @Post('login')
  async login(@Body() dto: LoginDto) {
    return this.auth.login(dto);
  }

  @Post('logout')
  @HttpCode(HttpStatus.NO_CONTENT)
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
  async registerClient(@Body() dto: RegisterClientDto) {
    return this.auth.registerClient(dto);
  }

  @Public()
  @Post('register/barber')
  @HttpCode(HttpStatus.CREATED)
  async registerBarber(@Body() dto: RegisterBarberDto) {
    return this.auth.registerBarber(dto);
  }

  @Public()
  @Post('register/owner')
  @HttpCode(HttpStatus.CREATED)
  async registerOwner(@Body() dto: RegisterOwnerDto) {
    return this.auth.registerOwner(dto);
  }
}
