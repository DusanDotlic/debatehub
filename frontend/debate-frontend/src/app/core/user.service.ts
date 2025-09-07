import { Injectable } from '@angular/core';
import { ApiClientService } from './api-client.service';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private api: ApiClientService) {}

  changePassword(payload: { currentPassword: string; newPassword: string; }) {
    const body = { oldPassword: payload.currentPassword, newPassword: payload.newPassword };
    return this.api.post<{ success: boolean; message: string }>('/auth/change-password', body);
  }

  deleteAccount(password: string) {
    return this.api.deleteWithBody<{ success: boolean; message: string }>('/auth/delete-account', { password });
  }
}
