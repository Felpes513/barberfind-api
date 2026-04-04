import { Injectable, Logger } from '@nestjs/common';
import { Interval } from '@nestjs/schedule';

@Injectable()
export class KeepAliveService {
  private readonly logger = new Logger(KeepAliveService.name);

  @Interval(30000)
  async tick() {
    this.logger.log(
      `[keep-alive] BarberFind API tick at ${new Date().toISOString()}`,
    );

    if (process.env.ENABLE_KEEP_ALIVE_PING !== 'true') {
      return;
    }

    const explicit = process.env.KEEP_ALIVE_URL?.trim();
    const renderBase = process.env.RENDER_EXTERNAL_URL?.trim();
    const url =
      explicit ||
      (renderBase ? `${renderBase.replace(/\/$/, '')}/api/ping` : null);

    if (!url) {
      return;
    }

    try {
      const ac = new AbortController();
      const timeout = setTimeout(() => ac.abort(), 8000);
      const res = await fetch(url, { signal: ac.signal });
      clearTimeout(timeout);
      if (!res.ok) {
        this.logger.warn(`[keep-alive] ping ${url} -> HTTP ${res.status}`);
      }
    } catch (e) {
      this.logger.warn(
        `[keep-alive] ping failed: ${e instanceof Error ? e.message : String(e)}`,
      );
    }
  }
}
