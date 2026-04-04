import { randomInt } from 'crypto';

const ALPHABET = '0123456789abcdefghijklmnopqrstuvwxyz';

/** Same shape as Java Cuid.generate(): 'c' + 25 random base36 chars */
export function generateCuid(): string {
  let s = 'c';
  for (let i = 0; i < 25; i++) {
    s += ALPHABET[randomInt(ALPHABET.length)];
  }
  return s;
}
