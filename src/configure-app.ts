import { ValidationPipe } from '@nestjs/common';
import { INestApplication, RequestMethod } from '@nestjs/common';

export function configureApp(app: INestApplication) {
  const origins =
    process.env.CORS_ORIGINS?.split(',').map((s) => s.trim()).filter(Boolean) ??
    ['http://localhost:5173'];
  app.enableCors({ origin: origins, credentials: true });

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
    }),
  );

  app.setGlobalPrefix('api', {
    exclude: [
      { path: 'init', method: RequestMethod.GET },
      { path: 'initi', method: RequestMethod.GET },
    ],
  });
}
