import { Component } from '@angular/core';

@Component({
  selector: 'app-layout',
  template: `
    <div class="app-layout">
      <app-sidebar></app-sidebar>
      <div class="app-main">
        <app-topbar></app-topbar>
        <main class="app-content">
          <ng-content></ng-content>
        </main>
      </div>
    </div>
  `,
  styles: [`
    .app-layout {
      display: flex;
      min-height: 100vh;
    }
    .app-main {
      flex: 1;
      display: flex;
      flex-direction: column;
      min-width: 0;
      margin-left: var(--sidebar-width);
    }
    .app-content {
      flex: 1;
      padding: 24px;
      max-width: 1100px;
      width: 100%;
      margin: 0 auto;
    }
    @media (max-width: 768px) {
      .app-main { margin-left: 0; padding-bottom: 72px; }
      .app-content { padding: 16px; }
    }
  `]
})
export class LayoutComponent {}
