import { Injectable, UnauthorizedException } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { PassportStrategy } from '@nestjs/passport';
import { Request } from 'express';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { PrismaService } from '../prisma/prisma.service';
import { hmacKeyBytes } from './jwt-key.util';

type JwtPayload = { sub: string; role: string };

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy, 'jwt') {
  constructor(
    config: ConfigService,
    private readonly prisma: PrismaService,
  ) {
    const secret = config.getOrThrow<string>('JWT_SECRET');
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: hmacKeyBytes(secret),
      passReqToCallback: true,
    });
  }

  async validate(req: Request, payload: JwtPayload) {
    const auth = req.headers.authorization;
    const token =
      auth && auth.startsWith('Bearer ') ? auth.slice(7).trim() : null;
    if (!token) throw new UnauthorizedException();

    const revoked = await this.prisma.revokedToken.findUnique({
      where: { token },
    });
    if (revoked) {
      throw new UnauthorizedException('Token revoked');
    }

    return { userId: payload.sub, role: payload.role };
  }
}
