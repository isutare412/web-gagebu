/**
 * Formats a date string or Date object to ISO format (YYYY-MM-DD)
 * @param date - Date string or Date object
 * @returns Formatted date string in ISO format
 */
export function formatDateISO(date: string | Date): string {
  if (!date) return '';

  const dateObj = typeof date === 'string' ? new Date(date) : date;
  if (isNaN(dateObj.getTime())) return '';

  return dateObj.toISOString().split('T')[0]!;
}

/**
 * Formats a date string or Date object to ISO datetime format (YYYY-MM-DD HH:MM:SS)
 * @param date - Date string or Date object
 * @returns Formatted datetime string in ISO format
 */
export function formatDateTimeISO(date: string | Date): string {
  if (!date) return '';

  const dateObj = typeof date === 'string' ? new Date(date) : date;
  if (isNaN(dateObj.getTime())) return '';

  return dateObj
    .toISOString()
    .replace('T', ' ')
    .replace(/\.\d{3}Z$/, '');
}

/**
 * Formats a date string or Date object to a readable format with full date info
 * @param date - Date string or Date object
 * @returns Formatted date string like "2025-07-13 Sunday"
 */
export function formatDateWithDay(date: string | Date): string {
  if (!date) return '';

  const dateObj = typeof date === 'string' ? new Date(date) : date;
  if (isNaN(dateObj.getTime())) return '';

  const isoDate = dateObj.toISOString().split('T')[0]!;
  const dayName = dateObj.toLocaleDateString('en-US', { weekday: 'long' });

  return `${isoDate} ${dayName}`;
}
