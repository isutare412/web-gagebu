import createClient from 'openapi-fetch';
import type { components, paths } from './schema';

// Create a singleton API client
export const apiClient = createClient<paths>({
  baseUrl: '', // Uses same origin
});

// Type definitions for better type safety
type UserUpdateRequest = components['schemas']['UserUpdateRequest'];
type AccountBookCreateRequest = components['schemas']['AccountBookCreateRequest'];
type AccountBookUpdateRequest = components['schemas']['AccountBookUpdateRequest'];
type RecordCreateRequest = components['schemas']['RecordCreateRequest'];
type RecordUpdateRequest = components['schemas']['RecordUpdateRequest'];

type RecordListParams = {
  page: number;
  pageSize: number;
  categories?: string[];
  recordType?: 'INCOME' | 'EXPENSE';
  startDate?: string;
  endDate?: string;
  direction: 'ASCENDING' | 'DESCENDING';
};

// Helper functions for common API calls
export const api = {
  // User APIs
  getCurrentUser: () => apiClient.GET('/api/v1/users/me'),
  updateUser: (userId: string, data: UserUpdateRequest) =>
    apiClient.PUT('/api/v1/users/{userId}', {
      params: { path: { userId } },
      body: data,
    }),

  // Account Book APIs
  listAccountBooks: () => apiClient.GET('/api/v1/account-books'),
  getAccountBook: (accountBookId: string) =>
    apiClient.GET('/api/v1/account-books/{accountBookId}', {
      params: { path: { accountBookId } },
    }),
  createAccountBook: (data: AccountBookCreateRequest) =>
    apiClient.POST('/api/v1/account-books', { body: data }),
  updateAccountBook: (accountBookId: string, data: AccountBookUpdateRequest) =>
    apiClient.PUT('/api/v1/account-books/{accountBookId}', {
      params: { path: { accountBookId } },
      body: data,
    }),
  deleteAccountBook: (accountBookId: string) =>
    apiClient.DELETE('/api/v1/account-books/{accountBookId}', {
      params: { path: { accountBookId } },
    }),

  // Record APIs
  listRecords: (accountBookId: string, params: RecordListParams) =>
    apiClient.GET('/api/v1/account-books/{accountBookId}/records', {
      params: {
        path: { accountBookId },
        query: params,
      },
    }),
  getRecord: (accountBookId: string, recordId: string) =>
    apiClient.GET('/api/v1/account-books/{accountBookId}/records/{recordId}', {
      params: { path: { accountBookId, recordId } },
    }),
  createRecord: (accountBookId: string, data: RecordCreateRequest) =>
    apiClient.POST('/api/v1/account-books/{accountBookId}/records', {
      params: { path: { accountBookId } },
      body: data,
    }),
  updateRecord: (accountBookId: string, recordId: string, data: RecordUpdateRequest) =>
    apiClient.PUT('/api/v1/account-books/{accountBookId}/records/{recordId}', {
      params: { path: { accountBookId, recordId } },
      body: data,
    }),
  deleteRecord: (accountBookId: string, recordId: string) =>
    apiClient.DELETE('/api/v1/account-books/{accountBookId}/records/{recordId}', {
      params: { path: { accountBookId, recordId } },
    }),

  // Invitation APIs
  listInvitations: (accountBookId: string) =>
    apiClient.GET('/api/v1/account-books/{accountBookId}/invitations', {
      params: { path: { accountBookId } },
    }),
  createInvitation: (accountBookId: string) =>
    apiClient.POST('/api/v1/account-books/{accountBookId}/invitations', {
      params: { path: { accountBookId } },
    }),
  joinInvitation: (invitationId: string) =>
    apiClient.POST('/api/v1/invitations/{invitationId}/join', {
      params: { path: { invitationId } },
    }),

  // Auth APIs
  logout: () => apiClient.GET('/api/v1/logout'),
  loginWithGoogle: () => apiClient.GET('/api/v1/oauth2/authorization/google'),
};

// Export types for use in components
export type {
  AccountBookCreateRequest,
  AccountBookUpdateRequest,
  RecordCreateRequest,
  RecordListParams,
  RecordUpdateRequest,
  UserUpdateRequest,
};
