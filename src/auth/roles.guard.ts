import {
  CanActivate,
  ExecutionContext,
  ForbiddenException,
  Injectable,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { AppRole, ROLES_KEY } from '../common/roles.decorator';
import { JwtUser } from '../common/current-user.decorator';

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private reflector: Reflector) {}

  canActivate(context: ExecutionContext): boolean {
    const roles = this.reflector.getAllAndOverride<AppRole[]>(ROLES_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);
    if (!roles?.length) return true;

    const req = context.switchToHttp().getRequest();
    const user = req.user as JwtUser | undefined;
    if (!user?.role) throw new ForbiddenException('forbidden');

    if (!roles.includes(user.role as AppRole)) {
      throw new ForbiddenException('forbidden');
    }
    return true;
  }
}
