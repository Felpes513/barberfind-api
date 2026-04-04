export function decToNumber(d: unknown): number | null {
  if (d == null) return null;
  if (typeof d === 'number') return d;
  if (
    typeof d === 'object' &&
    d !== null &&
    'toNumber' in d &&
    typeof (d as { toNumber: () => number }).toNumber === 'function'
  ) {
    return (d as { toNumber: () => number }).toNumber();
  }
  return Number(d);
}
