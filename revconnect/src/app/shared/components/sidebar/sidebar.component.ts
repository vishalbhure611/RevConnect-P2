import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  badge?: number;
  roles?: string[];
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  currentRoute = '';
  unreadCount = 0;
  mobileOpen = false;

  navItems: NavItem[] = [
    { label: 'Feed', icon: '\u229e', route: '/feed' },
    { label: 'Network', icon: '\u2b21', route: '/network' },
    { label: 'Notifications', icon: '\u2318', route: '/notifications' },
    { label: 'Analytics', icon: '\u25c8', route: '/analytics', roles: ['BUSINESS', 'CREATOR'] },
  ];

  constructor(
    public authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd)
    ).subscribe((e: any) => {
      this.currentRoute = e.urlAfterRedirects;
      this.mobileOpen = false;
    });
    this.currentRoute = this.router.url;

    this.notificationService.unreadCount$.subscribe(count => {
      this.unreadCount = count;
    });

    if (this.authService.userId) {
      this.notificationService.getUnreadCount(this.authService.userId).subscribe({ error: () => {} });
    }
  }

  get visibleNavItems(): NavItem[] {
    const role = this.authService.userRole || '';
    return this.navItems.filter(item => !item.roles || item.roles.includes(role));
  }

  isActive(route: string): boolean {
    return this.currentRoute.startsWith(route);
  }

  goToProfile(): void {
    const userId = this.authService.userId;
    this.router.navigate(['/profile', userId]);
  }

  logout(): void {
    this.authService.logout();
  }

  toggleMobile(): void {
    this.mobileOpen = !this.mobileOpen;
  }

  get displayName(): string {
    return this.authService.currentUser?.username || 'User';
  }

  get profilePictureUrl(): string | null {
    return this.authService.currentUser?.profilePictureUrl || null;
  }

  get userRole(): string {
    return this.authService.userRole || 'PERSONAL';
  }

  get userId(): number | null {
    return this.authService.userId;
  }
}
