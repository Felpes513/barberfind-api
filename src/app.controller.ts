import { Controller, Get } from '@nestjs/common';
import { Public } from './common/public.decorator';

@Controller()
export class AppController {
  @Public()
  @Get(['init', 'initi'])
  health() {
    return { ok: true, msg: 'API BarberFind a responder.' };
  }

  @Public()
  @Get('ping')
  ping() {
    return { status: 'ok' };
  }
}
