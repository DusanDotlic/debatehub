import { Component, OnInit } from '@angular/core';
import { ThemeService } from './theme.service';
import { AuthService } from '../core/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  constructor(private theme: ThemeService, public auth: AuthService, private router: Router) {}

  ngOnInit(): void { this.theme.init(); }
  toggleTheme() { this.theme.toggle(); }
  logout() { this.auth.logout(); this.router.navigate(['/login']); }
}
