import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { DebateService } from '../core/debate.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-debate-form',
  templateUrl: './debate-form.component.html',
  styleUrls: ['./debate.shared.css']
})
export class DebateFormComponent implements OnInit {
  slug?: string;
  error = '';
  form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
    description: ['']
  });

  constructor(private fb: FormBuilder, private ds: DebateService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const slugParam = this.route.snapshot.paramMap.get('slug');
    if (slugParam) {
      this.slug = String(slugParam);
      this.ds.getOne(this.slug).subscribe({
        next: (d) => this.form.patchValue({ title: d.title, description: d.description || '' }),
        error: (e: any) => this.error = e?.message || 'Failed to load debate'
      });
    }
  }

  submit() {
    if (this.form.invalid) return;
    this.error = '';
    const payload = this.form.value as any;
    const obs = this.slug ? this.ds.update(this.slug, payload) : this.ds.create(payload);
    obs.subscribe({
      next: (d: any) => this.router.navigate(['/debates', d?.slug || this.slug]),
      error: (e: any) => this.error = e?.message || 'Save failed'
    });
  }
}
