export function normalizeEmail(email: string | null | undefined): string | null {
  if (email == null) return null;
  return email.trim().toLowerCase();
}

/** Digits only, e.g. "(55) 11999-0000" -> "55119990000" */
export function normalizePhone(phone: string | null | undefined): string | null {
  if (phone == null) return null;
  return phone.replace(/\D/g, '');
}
