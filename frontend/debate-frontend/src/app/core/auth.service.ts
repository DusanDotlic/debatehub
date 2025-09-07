import { Injectable } from '@angular/core';
import { BehaviorSubject, map, tap } from 'rxjs';
import { ApiClientService } from './api-client.service';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { User } from '../models/user.model';

const ACCESS_KEY = 'debatehub_access_token';
const USER_KEY = 'debatehub_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private user$ = new BehaviorSubject<User | null>(this.loadUser());

  constructor(private api: ApiClientService) {}

  login(payload: LoginRequest) {
    return this.api.post<AuthResponse>('/auth/login', payload).pipe(
      tap(res => this.persist(res))
    );
  }

  register(payload: RegisterRequest) {
    return this.api.post<AuthResponse>('/auth/register', payload).pipe(
      tap(res => this.persist(res))
    );
  }

  logout() {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(USER_KEY);
    this.user$.next(null);
  }

  me() {
    return this.api.get<{ success: boolean; data: User }>('/users/me').pipe(
      tap(r => { if (r?.data) this.setUser(r.data); }),
      map(r => r.data)
    );
  }

  get token(): string | null { return localStorage.getItem(ACCESS_KEY); }
  userChanges() { return this.user$.asObservable(); }
  currentUser(): User | null { return this.user$.value; }
  isAuthenticated(): boolean { return !!this.token; }

  private persist(res: any) {
    const token =
      res?.token ??
      res?.accessToken ??
      res?.data?.token ??
      res?.data?.accessToken ??
      null;

    const user =
      res?.user ??
      res?.data?.user ??
      res?.principal ??
      null;

    if (token) localStorage.setItem(ACCESS_KEY, token);
    if (user) {
      localStorage.setItem(USER_KEY, JSON.stringify(user));
      this.user$.next(user);
    }
  }

  private loadUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) as User : null;
  }
  private setUser(u: User) {
    localStorage.setItem(USER_KEY, JSON.stringify(u));
    this.user$.next(u);
  }
}
