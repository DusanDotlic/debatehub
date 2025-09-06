import { Component } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent {
  mode: 'login' | 'register' = 'login';
  email = '';
  displayName = '';
  password = '';
  error = '';

  constructor(private api: ApiService, private router: Router) {}

  async submit() {
    this.error = '';
    try {
      if (this.mode === 'register') {
        await this.api.register(this.email, this.displayName, this.password).toPromise();
      } else {
        await this.api.login(this.email, this.password).toPromise();
      }
      localStorage.setItem('dh_email', this.email);
      if (this.displayName) localStorage.setItem('dh_displayName', this.displayName);
      this.router.navigateByUrl('/profile');
    } catch (e: any) {
      this.error = e?.error?.message || 'Request failed';
    }
  }
}
