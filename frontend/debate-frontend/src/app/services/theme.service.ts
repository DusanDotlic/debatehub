import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

const STORAGE_KEY = 'debatehub_theme'; // 'light' | 'dark'

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private theme$ = new BehaviorSubject<'light' | 'dark'>(this.readInitial());

  constructor() {
    this.apply(this.theme$.value);
  }

  isDark$ = this.theme$.asObservable();

  toggle() {
    const next = this.theme$.value === 'dark' ? 'light' : 'dark';
    this.set(next);
  }

  set(theme: 'light' | 'dark') {
    this.theme$.next(theme);
    localStorage.setItem(STORAGE_KEY, theme);
    this.apply(theme);
  }

  private apply(theme: 'light' | 'dark') {
    document.body.classList.remove('theme-light', 'theme-dark');
    document.body.classList.add(theme === 'dark' ? 'theme-dark' : 'theme-light');
  }

  private readInitial(): 'light' | 'dark' {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved === 'dark' || saved === 'light') return saved;
    return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }
}
