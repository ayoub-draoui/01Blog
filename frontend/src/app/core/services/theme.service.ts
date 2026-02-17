import { Injectable, signal, effect } from '@angular/core';

export type Theme = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  // Signal to track current theme
  private themeSignal = signal<Theme>(this.getInitialTheme());
  
  // Public readonly signal
  readonly currentTheme = this.themeSignal.asReadonly();

  constructor() {
    // Apply initial theme IMMEDIATELY
    this.applyTheme(this.themeSignal());
    
    // Effect to apply theme changes
    effect(() => {
      this.applyTheme(this.themeSignal());
    });
  }

  /**
   * Get initial theme from localStorage or system preference
   */
  private getInitialTheme(): Theme {
    // Check localStorage first
    const savedTheme = localStorage.getItem('theme') as Theme;
    if (savedTheme === 'light' || savedTheme === 'dark') {
      return savedTheme;
    }

    // Check system preference
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }

    // Default to light
    return 'light';
  }

  /**
   * Apply theme to document body and html
   */
  private applyTheme(theme: Theme): void {
    const body = document.body;
    const html = document.documentElement;
    
    body.classList.remove('light', 'dark');
    
    html.classList.remove('light', 'dark');
    
    body.classList.add(theme);
    
    html.classList.add(theme);
    
    body.setAttribute('data-theme', theme);
    html.setAttribute('data-theme', theme);
    
    localStorage.setItem('theme', theme);
    
    console.log(`✅ Theme applied: ${theme}`);
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