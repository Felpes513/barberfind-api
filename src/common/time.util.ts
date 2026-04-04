/** Parse "HH:mm" or "HH:mm:ss" to a Date at UTC epoch date (for Prisma @db.Time). */
export function parseTimeToDate(s: string | null | undefined): Date | null {
  if (s == null || !String(s).trim()) return null;
  const parts = String(s).trim().split(':');
  const h = Number(parts[0]);
  const m = Number(parts[1] ?? 0);
  const sec = Number(parts[2] ?? 0);
  if (Number.isNaN(h) || Number.isNaN(m)) return null;
  return new Date(Date.UTC(1970, 0, 1, h, m, sec, 0));
}

export function formatTimeFromDate(d: Date | null | undefined): string | null {
  if (!d) return null;
  const h = d.getUTCHours();
  const m = d.getUTCMinutes();
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
}
