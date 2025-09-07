export interface LoginRequest { email: string; password: string; }
export interface RegisterRequest { email: string; displayName: string; password: string; }
export interface AuthResponse {
  success: boolean;
  message?: string;
  token?: string;        // if your backend returns JWT here
  refreshToken?: string; // if applicable
  user?: { id: number; email: string; username: string; };
}
