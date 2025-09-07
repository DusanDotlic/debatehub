export interface DebateCard {
  slug: string;
  title: string;
  description?: string;
  inviteOnly?: boolean;
  status?: string;
  hostDisplayName?: string;
  hostEmail?: string;
  participantCount?: number;
  createdAt?: string;
  startedAt?: string;
}

export interface DebateDetails {
  slug: string;
  title: string;
  description?: string;
  status?: string;
  inviteOnly: boolean;
  hostDisplayName?: string;
  hostEmail?: string;
  createdAt?: string;
  startedAt?: string;
  endedAt?: string;
  participants?: any[];
}

export interface CreateDebateRequest { title: string; description?: string; inviteOnly?: boolean; }
export interface UpdateDebateRequest { title?: string; description?: string; inviteOnly?: boolean; }
