import { Controller, Get } from '@nestjs/common';
import { ApiOkResponse, ApiOperation, ApiTags } from '@nestjs/swagger';
import { Public } from './common/public.decorator';

@ApiTags('Health')
@Controller()
export class AppController {
  @Public()
  @Get(['init', 'initi'])
  @ApiOperation({ summary: 'Healthcheck legado da API' })
  @ApiOkResponse({
    description: 'API online',
    schema: { example: { ok: true, msg: 'API BarberFind a responder.' } },
  })
  health() {
    return { ok: true, msg: 'API BarberFind a responder.' };
  }

  @Public()
  @Get('ping')
  @ApiOperation({ summary: 'Ping rapido da API' })
  @ApiOkResponse({
    description: 'Ping respondido',
    schema: { example: { status: 'ok' } },
  })
  ping() {
    return { status: 'ok' };
  }
}
