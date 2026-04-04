import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { APP_GUARD } from '@nestjs/core';
import { ScheduleModule } from '@nestjs/schedule';
import { AppController } from './app.controller';
import { AppointmentsModule } from './appointments/appointments.module';
import { AuthModule } from './auth/auth.module';
import { JwtAuthGuard } from './auth/jwt-auth.guard';
import { RolesGuard } from './auth/roles.guard';
import { BarbershopBarbersModule } from './barbershop-barbers/barbershop-barbers.module';
import { BarbershopPhotosModule } from './barbershop-photos/barbershop-photos.module';
import { BarbershopsModule } from './barbershops/barbershops.module';
import { CatalogServicesModule } from './catalog-services/catalog-services.module';
import { FavoritesModule } from './favorites/favorites.module';
import { KeepAliveModule } from './keep-alive/keep-alive.module';
import { PrismaModule } from './prisma/prisma.module';
import { UsersModule } from './users/users.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    ScheduleModule.forRoot(),
    KeepAliveModule,
    PrismaModule,
    AuthModule,
    BarbershopsModule,
    BarbershopPhotosModule,
    BarbershopBarbersModule,
    CatalogServicesModule,
    AppointmentsModule,
    FavoritesModule,
    UsersModule,
  ],
  controllers: [AppController],
  providers: [
    { provide: APP_GUARD, useClass: JwtAuthGuard },
    { provide: APP_GUARD, useClass: RolesGuard },
  ],
})
export class AppModule {}
