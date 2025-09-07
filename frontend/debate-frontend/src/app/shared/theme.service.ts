import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private key = 'debatehub_theme'; // 'light' | 'dark'

  init() {
    const raw = localStorage.getItem(this.key);
    const t: 'light' | 'dark' = raw === 'dark' ? 'dark' : 'light';
    this.apply(t);
  }


  toggle() {
    const current = document.documentElement.classList.contains('dark') ? 'dark' : 'light';
    this.apply(current === 'dark' ? 'light' : 'dark');
  }

  private apply(mode: 'light'|'dark') {
    document.documentElement.classList.toggle('dark', mode === 'dark');
    localStorage.setItem(this.key, mode);
  }
}
