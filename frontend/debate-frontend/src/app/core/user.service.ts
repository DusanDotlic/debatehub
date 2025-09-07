import { Injectable } from '@angular/core';
import { ApiClientService } from './api-client.service';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private api: ApiClientService) {}

  // Backend expects oldPassword/newPassword in body at /api/auth/change-password
  changePassword(payload: { currentPassword: string; newPassword: string; }) {
    const body = { oldPassword: payload.currentPassword, newPassword: payload.newPassword };
    return this.api.post<{ success: boolean; message: string }>('/auth/change-password', body);
  }

  // Backend expects DELETE with { password } body at /api/auth/delete-account
  deleteAccount(password: string) {
    return this.api.deleteWithBody<{ success: boolean; message: string }>('/auth/delete-account', { password });
  }
}
