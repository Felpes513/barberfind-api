import { NestFactory } from '@nestjs/core';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { AppModule } from './app.module';
import { configureApp } from './configure-app';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  configureApp(app);

  const swaggerConfig = new DocumentBuilder()
    .setTitle('BarberFind API')
    .setDescription('Documentacao oficial da API do BarberFind')
    .setVersion('1.0.0')
    .addBearerAuth(
      {
        type: 'http',
        scheme: 'bearer',
        bearerFormat: 'JWT',
        description: 'Cole apenas o token JWT no campo abaixo.',
      },
      'access-token',
    )
    .build();
  const swaggerDocument = SwaggerModule.createDocument(app, swaggerConfig);
  SwaggerModule.setup('api/docs', app, swaggerDocument, {
    swaggerOptions: {
      persistAuthorization: true,
    },
  });

  const port = Number(process.env.PORT ?? process.env.SERVER_PORT ?? 8080);
  await app.listen(port);
  if (process.env.NODE_ENV !== 'production') {
    const url = `http://localhost:${port}`;
    console.log(`API a correr em ${url} (porta ${port})`);
  }
}
void bootstrap();
