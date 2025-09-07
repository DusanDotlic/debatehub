export interface LoginRequest { email: string; password: string; }
export interface RegisterRequest { email: string; displayName: string; password: string; }
export interface AuthResponse {
  success: boolean;
  message?: string;
  token?: string;
  refreshToken?: string;
  user?: { id: number; email: string; username: string; };
}
