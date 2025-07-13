export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface Toast {
  id: string;
  type: ToastType;
  title?: string;
  message: string;
  duration: number; // Now required since we always provide it
  dismissible?: boolean;
}

// Toast state using Svelte 5 runes
export const toastState = $state({
  toasts: [] as Toast[],
});

let toastIdCounter = 0;

function generateId(): string {
  return `toast-${++toastIdCounter}-${Date.now()}`;
}

// Toast actions
export function addToast(toast: Omit<Toast, 'id'>): string {
  const id = generateId();

  // Ensure we always have a duration for all toasts
  const defaultDuration = 5000; // 5 seconds fallback default

  const newToast: Toast = {
    id,
    dismissible: true,
    ...toast,
    // If toast.duration is explicitly provided, use it; otherwise use default
    duration: toast.duration ?? defaultDuration,
  };

  toastState.toasts.push(newToast);

  // Note: Auto-dismiss is now handled internally by the Toast component
  // to ensure progress bar animation completes properly

  return id;
}

export function removeToast(id: string): void {
  const index = toastState.toasts.findIndex((toast) => toast.id === id);
  if (index !== -1) {
    toastState.toasts.splice(index, 1);
  }
}

export function clearAllToasts(): void {
  toastState.toasts.splice(0);
}

// Convenience functions for different toast types
export function showSuccessToast(message: string, title?: string, duration?: number): string {
  return addToast({ type: 'success', message, title, duration: duration ?? 4000 }); // 4 seconds for success
}

export function showErrorToast(message: string, title?: string, duration?: number): string {
  return addToast({ type: 'error', message, title, duration: duration ?? 8000 }); // 8 seconds for errors
}

export function showWarningToast(message: string, title?: string, duration?: number): string {
  return addToast({ type: 'warning', message, title, duration: duration ?? 6000 }); // 6 seconds for warnings
}

export function showInfoToast(message: string, title?: string, duration?: number): string {
  return addToast({ type: 'info', message, title, duration: duration ?? 5000 }); // 5 seconds for info
}

// Helper function to show toast for API errors
export function showApiErrorToast(
  error: unknown,
  fallbackMessage: string = 'An error occurred'
): string {
  let message = fallbackMessage;
  let title = 'Error';

  // Handle different error formats with proper type checking
  if (error && typeof error === 'object') {
    const errorObj = error as Record<string, unknown>;

    // Helper function to safely get nested properties
    const getNestedValue = (obj: unknown, path: string[]): unknown => {
      let current = obj;
      for (const key of path) {
        if (current && typeof current === 'object' && key in current) {
          current = (current as Record<string, unknown>)[key];
        } else {
          return undefined;
        }
      }
      return current;
    };

    // Priority 1: Backend ErrorResponse schema (response.data.{errorCode, message})
    const responseData = getNestedValue(errorObj, ['response', 'data']);
    const responseErrorCode = getNestedValue(responseData, ['errorCode']);
    const responseMessage = getNestedValue(responseData, ['message']);

    // Check if error is directly an ErrorResponse object (from openapi-fetch response.error)
    const directErrorCode = getNestedValue(errorObj, ['errorCode']);
    const directMessage = getNestedValue(errorObj, ['message']);

    if (typeof directMessage === 'string') {
      message = directMessage;
    } else if (typeof responseMessage === 'string') {
      message = responseMessage;
    }

    if (typeof directErrorCode === 'string') {
      title = directErrorCode
        .replace(/_/g, ' ')
        .toLowerCase()
        .replace(/\b\w/g, (l) => l.toUpperCase());
    } else if (typeof responseErrorCode === 'string') {
      title = responseErrorCode
        .replace(/_/g, ' ')
        .toLowerCase()
        .replace(/\b\w/g, (l) => l.toUpperCase());
    }

    // Priority 2: Data wrapper format (for non-axios errors)
    if (message === fallbackMessage) {
      const dataMessage = getNestedValue(errorObj, ['data', 'message']);
      if (typeof dataMessage === 'string') {
        message = dataMessage;
      }
    }

    if (title === 'Error') {
      const dataErrorCode = getNestedValue(errorObj, ['data', 'errorCode']);
      if (typeof dataErrorCode === 'string') {
        title = dataErrorCode
          .replace(/_/g, ' ')
          .toLowerCase()
          .replace(/\b\w/g, (l) => l.toUpperCase());
      }
    }

    // Priority 3: Fallback to generic message and HTTP status
    if (message === fallbackMessage) {
      const directMessage = getNestedValue(errorObj, ['message']);
      if (typeof directMessage === 'string') {
        message = directMessage;
      }
    }

    // If no errorCode found, use HTTP status as fallback for title
    if (title === 'Error') {
      const responseStatus = getNestedValue(errorObj, ['response', 'status']);
      const directStatus = getNestedValue(errorObj, ['status']);

      if (typeof responseStatus === 'number') {
        title = `Error ${responseStatus}`;
      } else if (typeof directStatus === 'number') {
        title = `Error ${directStatus}`;
      }
    }
  } else if (typeof error === 'string') {
    message = error;
  }

  return showErrorToast(message, title);
}
