import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

type ParamValue = string | number | boolean | ReadonlyArray<string | number | boolean>;
type ParamMap = { [param: string]: ParamValue };

@Injectable({ providedIn: 'root' })
export class ApiClientService {
  private readonly base = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  get<T>(path: string, params?: HttpParams | ParamMap): Observable<T> {
    const options = this.buildOptions(params);
    return this.http.get<T>(`${this.base}${path}`, options)
      .pipe(catchError(err => this.onErr(err)));
  }

  post<T>(path: string, body: any): Observable<T> {
    return this.http.post<T>(`${this.base}${path}`, body)
      .pipe(catchError(err => this.onErr(err)));
  }

  put<T>(path: string, body: any): Observable<T> {
    return this.http.put<T>(`${this.base}${path}`, body)
      .pipe(catchError(err => this.onErr(err)));
  }

  patch<T>(path: string, body: any): Observable<T> {
    return this.http.patch<T>(`${this.base}${path}`, body)
      .pipe(catchError(err => this.onErr(err)));
  }

  delete<T>(path: string): Observable<T> {
    return this.http.delete<T>(`${this.base}${path}`)
      .pipe(catchError(err => this.onErr(err)));
  }

  /** For endpoints that require a DELETE request with a body (e.g., delete account) */
  deleteWithBody<T>(path: string, body: any): Observable<T> {
    return this.http.request<T>('DELETE', `${this.base}${path}`, { body })
      .pipe(catchError(err => this.onErr(err)));
  }

  private buildOptions(params?: HttpParams | ParamMap): { params?: HttpParams | ParamMap } {
    if (!params) return {};
    if (params instanceof HttpParams) return { params };
    let hp = new HttpParams();
    Object.entries(params).forEach(([k, v]) => {
      if (Array.isArray(v)) {
        v.forEach(item => { hp = hp.append(k, String(item)); });
      } else if (v !== undefined && v !== null) {
        hp = hp.set(k, String(v));
      }
    });
    return { params: hp };
  }

  private onErr(err: HttpErrorResponse) {
    const message =
      (err?.error && (err.error.message || err.error.error || (typeof err.error === 'string' ? err.error : null))) ||
      err?.statusText ||
      'Request failed';
    return throwError(() => new Error(message));
  }
}
