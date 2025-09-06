import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  email = '';
  pinned: any[] = [];
  started: any[] = [];
  joined: any[] = [];
  loading = true;
  error = '';  
  // Begin a debate form
  newTitle = '';
  newDescription = '';
  inviteOnly = true;
  busy = false;

  // Join by code form
  joinCode = '';


  constructor(private api: ApiService, private router: Router) {}

  ngOnInit(): void {
    const email = localStorage.getItem('dh_email');
    if (!email) { this.router.navigateByUrl('/auth'); return; }
    this.email = email;

    Promise.all([
      this.api.pinned(email).toPromise(),
      this.api.mineStarted(email).toPromise(),
      this.api.mineJoined(email).toPromise(),
    ]).then(([pin, st, jo]) => {
      this.pinned = pin ?? [];
      this.started = st ?? [];
      this.joined = jo ?? [];
    }).catch(e => {
      this.error = e?.error?.message || 'Failed to load';
    }).finally(() => this.loading = false);
  }

  onPin(slug: string) {
    this.api.pin(this.email, slug).subscribe(() => {
      // refresh pinned
      this.api.pinned(this.email).subscribe(p => this.pinned = p);
    });
  }

  onUnpin(slug: string) {
    this.api.unpin(this.email, slug).subscribe(() => {
      this.api.pinned(this.email).subscribe(p => this.pinned = p);
    });
  }

  isPinned(slug: string) {
    return this.pinned?.some(d => d.slug === slug);
  }

    async beginDebate() {
    if (!this.newTitle?.trim()) return;
    this.busy = true; this.error = '';
    try {
      await this.api.createDebate(this.email, this.newTitle, this.newDescription || '', this.inviteOnly).toPromise();
      // refresh lists
      this.started = await this.api.mineStarted(this.email).toPromise() ?? [];
      this.newTitle = ''; this.newDescription = '';
    } catch (e: any) {
      this.error = e?.error?.message || 'Failed to create debate';
    } finally {
      this.busy = false;
    }
  }

  async joinByCode() {
    if (!this.joinCode?.trim()) return;
    this.busy = true; this.error = '';
    try {
      await this.api.acceptInvitation(this.joinCode.trim(), this.email).toPromise();
      // refresh lists
      this.joined = await this.api.mineJoined(this.email).toPromise() ?? [];
      this.joinCode = '';
    } catch (e: any) {
      this.error = e?.error?.message || 'Failed to join';
    } finally {
      this.busy = false;
    }
  }

}
