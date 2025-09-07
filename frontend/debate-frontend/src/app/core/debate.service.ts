import { Injectable } from '@angular/core';
import { ApiClientService } from './api-client.service';
import { CreateDebateRequest, DebateCard, DebateDetails, UpdateDebateRequest } from '../models/debate.model';
import { forkJoin, map, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DebateService {
  constructor(private api: ApiClientService) {}

  listMine(): Observable<{ created: DebateCard[]; joined: DebateCard[]; pinned: DebateCard[] }> {
    return forkJoin({
      created: this.api.get<DebateCard[]>('/debates/mine/started'),
      joined: this.api.get<DebateCard[]>('/debates/mine/joined'),
      pinned: this.api.get<DebateCard[]>('/debates/pinned'),
    });
  }

  getOne(slug: string): Observable<DebateDetails> {
    return this.api.get<DebateDetails>(`/debates/${slug}`);
  }

  create(payload: CreateDebateRequest): Observable<{ slug: string }> {
    return this.api.post<any>('/debates', payload).pipe(
      map(res => ({ slug: res?.slug || res?.data?.slug }))
    );
  }

  update(slug: string, payload: UpdateDebateRequest): Observable<any> {
    return this.api.put(`/debates/${slug}`, payload);
  }

  delete(slug: string) {
    return this.api.delete(`/debates/${slug}`);
  }

  pin(slug: string) {
    return this.api.post(`/debates/${slug}/pin`, {}); // POST, empty body
  }
  unpin(slug: string) {
    return this.api.delete(`/debates/${slug}/pin`);
  }
}

