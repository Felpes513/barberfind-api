import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { configureApp } from './configure-app';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  configureApp(app);
  const port = Number(process.env.PORT ?? process.env.SERVER_PORT ?? 8080);
  await app.listen(port);
  if (process.env.NODE_ENV !== 'production') {
    const url = `http://localhost:${port}`;
    console.log(`API a correr em ${url} (porta ${port})`);
  }
}
bootstrap();
