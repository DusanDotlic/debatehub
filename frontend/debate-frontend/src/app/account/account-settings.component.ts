import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { UserService } from '../core/user.service';
import { AuthService } from '../core/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-account-settings',
  templateUrl: './account-settings.component.html',
  styleUrls: ['./account-settings.component.css']
})
export class AccountSettingsComponent {
  pwError = '';
  delError = '';
  msg = '';

  confirmDel = false;

  pwForm = this.fb.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(6)]]
  });

  delForm = this.fb.group({
    password: ['', Validators.required]
  });

  constructor(
    private fb: FormBuilder,
    private user: UserService,
    private auth: AuthService,
    private router: Router
  ) {}

  changePassword() {
    if (this.pwForm.invalid) return;
    this.pwError = '';
    this.msg = '';
    this.user.changePassword(this.pwForm.value as any).subscribe({
      next: (r) => (this.msg = r.message || 'Password changed'),
      error: (e: any) => (this.pwError = e?.message || 'Change failed')
    });
  }

  // First click reveals the password field; second submit actually deletes
  deleteAccount() {
    if (!this.confirmDel) {
      this.confirmDel = true;
      this.delError = '';
      this.msg = '';
      return;
    }

    if (this.delForm.invalid) {
      this.delForm.markAllAsTouched();
      return;
    }

    const pw = this.delForm.controls.password.value as string;
    this.user.deleteAccount(pw).subscribe({
      next: (r) => {
        this.msg = r.message || 'Account deleted';
        this.auth.logout();
        this.router.navigate(['/login']);
      },
      error: (e: any) => (this.delError = e?.message || 'Delete failed')
    });
  }

  cancelDelete() {
    this.confirmDel = false;
    this.delForm.reset();
    this.delError = '';
  }
}
