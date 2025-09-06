import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private base = '/api'; // dev proxy will route this to backend

  constructor(private http: HttpClient) {}

  // Auth
  register(email: string, displayName: string, password: string) {
    return this.http.post<any>(`${this.base}/auth/register`, { email, displayName, password });
  }
  login(email: string, password: string) {
    return this.http.post<any>(`${this.base}/auth/login`, { email, password });
  }

  // Debates
  createDebate(hostEmail: string, title: string, description: string, inviteOnly: boolean) {
    return this.http.post<any>(`${this.base}/debates`, { hostEmail, title, description, inviteOnly });
  }
  getDebate(slug: string) {
    return this.http.get<any>(`${this.base}/debates/${slug}`);
  }
  mineStarted(email: string) {
    const params = new HttpParams().set('email', email);
    return this.http.get<any[]>(`${this.base}/debates/mine/started`, { params });
  }
  mineJoined(email: string) {
    const params = new HttpParams().set('email', email);
    return this.http.get<any[]>(`${this.base}/debates/mine/joined`, { params });
  }
  pinned(email: string) {
    const params = new HttpParams().set('email', email);
    return this.http.get<any[]>(`${this.base}/debates/pinned`, { params });
  }
  pin(email: string, slug: string) {
    const params = new HttpParams().set('email', email);
    return this.http.post<any>(`${this.base}/debates/${slug}/pin`, null, { params });
  }
  unpin(email: string, slug: string) {
    const params = new HttpParams().set('email', email);
    return this.http.delete<any>(`${this.base}/debates/${slug}/pin`, { params });
  }

  // Invitations
  createInvitation(hostEmail: string, debateSlug: string, roleGranted: 'debater'|'moderator', maxUses = 3, ttlDays = 7) {
    return this.http.post<any>(`${this.base}/invitations`, { hostEmail, debateSlug, roleGranted, maxUses, ttlDays });
  }
  acceptInvitation(code: string, userEmail: string) {
    return this.http.post<any>(`${this.base}/invitations/accept`, { code, userEmail });
  }
}
