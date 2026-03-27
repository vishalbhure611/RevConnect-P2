import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-topbar',
  template: `
    <header class="topbar">
      <div class="topbar-search">
        <span class="search-icon">&#8981;</span>
        <input type="text" placeholder="Search people, posts, hashtags..."
          class="search-input" [(ngModel)]="searchQuery"
          (keyup.enter)="search()" />
      </div>
      <div class="topbar-actions">
        <button class="btn btn-ghost btn-icon-round" routerLink="/notifications" title="Notifications">
          <span style="font-size:18px">&#8984;</span>
          <span *ngIf="unreadCount > 0" class="topbar-badge">
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </span>
        </button>
        <app-avatar [src]="profilePictureUrl" [name]="displayName" size="sm"
          style="cursor:pointer" (click)="goToProfile()"></app-avatar>
      </div>
    </header>
  `,
  styles: [`
    .topbar {
      position: sticky;
      top: 0;
      height: var(--topbar-height);
      background: var(--bg-surface);
      border-bottom: 1px solid var(--border);
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 0 24px;
      z-index: 50;
      backdrop-filter: blur(8px);
    }
    .topbar-search {
      flex: 1;
      max-width: 480px;
      position: relative;
    }
    .search-icon {
      position: absolute;
      left: 14px;
      top: 50%;
      transform: translateY(-50%);
      color: var(--text-muted);
      font-size: 17px;
      pointer-events: none;
    }
    .search-input {
      width: 100%;
      padding: 9px 16px 9px 42px;
      background: var(--bg-input);
      border: 1px solid var(--border);
      border-radius: var(--radius-full);
      color: var(--text-primary);
      font-family: var(--font-body);
      font-size: 14px;
      outline: none;
      transition: border-color var(--transition), box-shadow var(--transition);
    }
    .search-input::placeholder { color: var(--text-muted); }
    .topbar-actions {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-left: auto;
    }
    .topbar-actions .btn {
      position: relative;
    }
    .topbar-badge {
      position: absolute;
      top: -4px;
      right: -4px;
      min-width: 18px;
      height: 18px;
      padding: 0 5px;
      border-radius: 9999px;
      background: var(--accent);
      color: #fff;
      font-size: 10px;
      font-weight: 700;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      box-shadow: var(--shadow-accent);
    }
    @media (max-width: 768px) {
      .topbar { padding: 0 16px; }
      .topbar-search { max-width: none; }
    }
  `]
})
export class TopbarComponent implements OnInit, OnDestroy {
  searchQuery = '';
  unreadCount = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      takeUntil(this.destroy$)
    ).subscribe(() => this.syncSearchQueryFromRoute());

    this.notificationService.unreadCount$
      .pipe(takeUntil(this.destroy$))
      .subscribe(count => this.unreadCount = count);

    this.syncSearchQueryFromRoute();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get displayName(): string {
    return this.authService.currentUser?.username || '';
  }

  get profilePictureUrl(): string | null {
    return this.authService.currentUser?.profilePictureUrl || null;
  }

  search(): void {
    const value = this.searchQuery.trim();
    if (value) {
      this.router.navigate(['/search'], { queryParams: { q: value } });
    } else if (this.router.url.startsWith('/search')) {
      this.router.navigate(['/search'], { queryParams: {} });
    }
  }

  goToProfile(): void {
    this.router.navigate(['/profile', this.authService.userId]);
  }

  private syncSearchQueryFromRoute(): void {
    let current: ActivatedRoute | null = this.route;
    while (current?.firstChild) current = current.firstChild;

    if (this.router.url.startsWith('/search')) {
      const q = current?.snapshot.queryParamMap.get('q');
      const hashtag = current?.snapshot.queryParamMap.get('hashtag');
      this.searchQuery = q ?? (hashtag ? `#${hashtag}` : '');
      return;
    }

    this.searchQuery = '';
  }
}
