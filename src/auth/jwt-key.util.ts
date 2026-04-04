import { createHash } from 'crypto';

/** Match JwtProvider.hmacKeyBytes in Java (jjwt 256-bit key). */
export function hmacKeyBytes(secret: string): Buffer {
  const raw = Buffer.from(secret, 'utf8');
  if (raw.length >= 32) return raw;
  return createHash('sha256').update(raw).digest();
}
