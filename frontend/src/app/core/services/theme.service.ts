import { Injectable, signal, effect } from '@angular/core';

export type Theme = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private themeSignal = signal<Theme>(this.getInitialTheme());
  
  readonly currentTheme = this.themeSignal.asReadonly();

  constructor() {
    effect(() => {
      this.applyTheme(this.themeSignal());
    });
  }

   
  private getInitialTheme(): Theme {
    const savedTheme = localStorage.getItem('theme') as Theme;
    if (savedTheme === 'light' || savedTheme === 'dark') {
      return savedTheme;
    }

    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }

    return 'light';
  }

  
  private applyTheme(theme: Theme): void {
    const body = document.body;
    
    body.classList.remove('light', 'dark');
    
    body.classList.add(theme);
    
    localStorage.setItem('theme', theme);
    
    console.log(`Theme changed to: ${theme}`);
  }
 
  setTheme(theme: Theme): void {
    this.themeSignal.set(theme);
  }

  
  toggleTheme(): void {
    const newTheme = this.themeSignal() === 'light' ? 'dark' : 'light';
    this.setTheme(newTheme);
  }

  
  isDark(): boolean {
    return this.themeSignal() === 'dark';
  }

  
  isLight(): boolean {
    return this.themeSignal() === 'light';
  }
}