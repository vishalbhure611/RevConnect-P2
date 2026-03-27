import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { NotificationResponse, NotificationPreference } from '../../../shared/models/models';

type NotiTab = 'all' | 'unread' | 'preferences';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications: NotificationResponse[] = [];
  preferences: NotificationPreference = {
    likes: true,
    comments: true,
    connections: true,
    mentions: true,
    follows: true,
    reposts: true
  };
  activeTab: NotiTab = 'all';
  loading = false;
  private destroy$ = new Subject<void>();
  preferenceOptions = [
    { key: 'likes', label: 'Likes', description: 'Notify me when someone likes my post', icon: '❤' },
    { key: 'comments', label: 'Comments', description: 'Notify me when someone comments on my post', icon: '💬' },
    { key: 'connections', label: 'Connections', description: 'Notify me about connection requests and accepts', icon: '🤝' },
    { key: 'follows', label: 'Followers', description: 'Notify me when someone follows me', icon: '👥' },
    { key: 'reposts', label: 'Reposts', description: 'Notify me when someone reposts my post', icon: '🔁' }
  ] as const;

  constructor(
    private notificationService: NotificationService,
    public authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    this.loadPreferences();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadNotifications(): void {
    const uid = this.authService.userId!;
    this.loading = true;
    this.notificationService.getNotifications(uid).pipe(
      catchError(() => of([])),
      takeUntil(this.destroy$)
    ).subscribe(n => {
      this.notifications = Array.isArray(n) ? n : [];
      this.loading = false;
    });
  }

  loadPreferences(): void {
    const uid = this.authService.userId!;
    this.notificationService.getPreferences(uid).pipe(
      catchError(() => of(null)),
      takeUntil(this.destroy$)
    ).subscribe(p => {
      if (p) this.preferences = { ...this.preferences, ...p };
    });
  }

  markRead(n: NotificationResponse): void {
    if (n.isRead) return;
    this.notificationService.markAsRead(n.id).subscribe({
      next: () => {
        n.isRead = true;
        this.notificationService.decrementUnreadCount();
      }
    });
  }

  markAllRead(): void {
    const uid = this.authService.userId!;
    this.notificationService.markAllAsRead(uid).subscribe({
      next: () => {
        this.notifications.forEach(n => n.isRead = true);
        this.notificationService.setUnreadCount(0);
        this.toastService.success('All marked as read');
      },
      error: () => this.toastService.error('Failed to mark all read')
    });
  }

  savePreferences(): void {
    const uid = this.authService.userId!;
    this.notificationService.updatePreferences(uid, this.preferences).subscribe({
      next: () => {
        this.toastService.success('Preferences saved!');
        this.loadPreferences();
      },
      error: () => this.toastService.error('Failed to save preferences')
    });
  }

  get filteredNotifications(): NotificationResponse[] {
    if (this.activeTab === 'unread') return this.notifications.filter(n => !n.isRead);
    return this.notifications;
  }

  get unreadLocalCount(): number {
    return this.notifications.filter(n => !n.isRead).length;
  }

  setTab(tab: NotiTab): void { this.activeTab = tab; }

  notificationIcon(type: string): string {
    const icons: Record<string, string> = {
      LIKE: '❤️', COMMENT: '💬', CONNECTION_REQUEST: '🤝',
      CONNECTION_ACCEPTED: '✅', FOLLOW: '👥', MENTION: '@',
      REPOST: '🔁', DEFAULT: '🔔'
    };
    return icons[type] || icons['DEFAULT'];
  }

  notificationMessage(n: NotificationResponse): string {
    const actor = n.actorUsername || 'Someone';
    const messages: Record<string, string> = {
      LIKE: `${actor} liked your post`,
      COMMENT: `${actor} commented on your post`,
      CONNECTION_REQUEST: `${actor} sent you a connection request`,
      CONNECTION_ACCEPTED: `${actor} accepted your connection request`,
      FOLLOW: `${actor} started following you`,
      MENTION: `${actor} mentioned you in a post`,
      REPOST: `${actor} reposted your post`
    };
    return messages[n.type] || n.message || `${actor} interacted with you`;
  }

  getPref(key: string): boolean {
    return (this.preferences as any)[key] ?? true;
  }

  setPref(key: string, value: boolean): void {
    (this.preferences as any)[key] = value;
  }
}
