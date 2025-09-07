import { Component, OnInit } from '@angular/core';
import { DebateService } from '../core/debate.service';
import { DebateCard } from '../models/debate.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  pinned: DebateCard[] = [];
  created: DebateCard[] = [];
  joined: DebateCard[] = [];
  pinnedSet = new Set<string>();
  pinPlaceholders: number[] = [];
  loading = true; error = '';

  constructor(private debates: DebateService) {}

  ngOnInit(): void { this.reload(); }

  reload() {
    this.loading = true; this.error = '';
    this.debates.listMine().subscribe({
      next: (data: any) => {
        this.pinned = data.pinned || [];
        this.created = data.created || [];
        this.joined = data.joined || [];
        this.pinnedSet = new Set((this.pinned || []).map(d => d.slug));
        this.refreshPinPlaceholders();
        this.loading = false;
      },
      error: (e: any) => { this.error = e?.message || 'Failed to load debates'; this.loading = false; }
    });
  }

  private refreshPinPlaceholders() {
    const count = Math.max(0, 4 - (this.pinned?.length || 0));
    this.pinPlaceholders = Array.from({ length: count }, (_, i) => i);
  }

  isPinned(slug: string): boolean { return this.pinnedSet.has(slug); }

  togglePin(ev: Event, d: DebateCard) {
    ev.preventDefault(); ev.stopPropagation();
    const call = this.isPinned(d.slug) ? this.debates.unpin(d.slug) : this.debates.pin(d.slug);
    call.subscribe({
      next: () => this.reload(),
      error: (e: any) => this.error = e?.message || 'Action failed'
    });
  }
}
