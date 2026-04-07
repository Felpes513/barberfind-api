import { ValidationPipe } from '@nestjs/common';
import { INestApplication, RequestMethod } from '@nestjs/common';
import { Request, Response } from 'express';

export function configureApp(app: INestApplication) {
  const origins = process.env.CORS_ORIGINS?.split(',')
    .map((s) => s.trim())
    .filter(Boolean) ?? ['http://localhost:5173'];
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

  if (process.env.NODE_ENV === 'development') {
    app.use((req: Request, res: Response, next: () => void) => {
      res.on('finish', () => {
        console.log(
          `[HTTP] ${req.method} ${req.originalUrl} -> ${res.statusCode}`,
        );
      });
      next();
    });
  }
}
