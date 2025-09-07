import { Component, OnInit } from '@angular/core';
import { DebateService } from '../core/debate.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-debate-detail',
  templateUrl: './debate-detail.component.html',
  styleUrls: ['./debate.shared.css']
})
export class DebateDetailComponent implements OnInit {
  slug!: string;
  data: any;
  error = ''; loading = true;

  constructor(private ds: DebateService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.slug = String(this.route.snapshot.paramMap.get('slug'));
    this.reload();
  }

  reload() {
    this.loading = true; this.error = '';
    this.ds.getOne(this.slug).subscribe({
      next: (d) => { this.data = d; this.loading = false; },
      error: (e: any) => { this.error = (e && e.message) ? e.message : 'Failed to load'; this.loading = false; }
    });
  }

  delete() {
    if (!confirm('Delete this debate?')) return;
    this.ds.delete(this.slug).subscribe({
      next: () => this.router.navigate(['/profile']),
      error: (e: any) => this.error = (e && e.message) ? e.message : 'Delete failed'
    });
  }
}
