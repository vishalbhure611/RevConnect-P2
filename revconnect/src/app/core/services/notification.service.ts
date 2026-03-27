import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, forkJoin, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationResponse, NotificationPreference } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private base = `${environment.apiUrl}/api`;
  private unreadCountSubject = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  // GET /api/notifications — uses JWT, returns list for current user
  getNotifications(_userId: number): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(`${this.base}/notifications`);
  }

  // GET /api/notifications/unread-count
  getUnreadCount(_userId: number): Observable<number> {
    return this.http.get<number>(`${this.base}/notifications/unread-count`).pipe(
      tap(count => this.unreadCountSubject.next(typeof count === 'number' ? count : 0))
    );
  }

  // PUT /api/notifications/read/{notificationId}
  markAsRead(notificationId: number): Observable<any> {
    return this.http.put<any>(`${this.base}/notifications/read/${notificationId}`, {});
  }

  markAllAsRead(_userId: number): Observable<any> {
    return this.http.put<any>(`${this.base}/notifications/read/all`, {});
  }

  // GET /api/notifications/preferences
  getPreferences(_userId: number): Observable<NotificationPreference> {
    return this.http.get<Array<{ type: string; enabled: boolean }>>(`${this.base}/notifications/preferences`).pipe(
      map((items) => {
        const prefs: NotificationPreference = {
          likes: true,
          comments: true,
          connections: true,
          mentions: true,
          follows: true,
          reposts: true
        };

        for (const item of items || []) {
          switch (item.type) {
            case 'LIKE':
              prefs.likes = item.enabled;
              break;
            case 'COMMENT':
              prefs.comments = item.enabled;
              break;
            case 'CONNECTION_REQUEST':
            case 'CONNECTION_ACCEPTED':
              prefs.connections = item.enabled;
              break;
            case 'FOLLOW':
              prefs.follows = item.enabled;
              break;
            case 'SHARE':
              prefs.reposts = item.enabled;
              break;
          }
        }

        return prefs;
      })
    );
  }

  // PUT /api/notifications/preferences/{type}
  updatePreference(type: string, enabled: boolean): Observable<any> {
    return this.http.put<any>(`${this.base}/notifications/preferences/${type}`, { enabled });
  }

  updatePreferences(_userId: number, prefs: NotificationPreference): Observable<any> {
    return forkJoin([
      this.updatePreference('LIKE', prefs.likes),
      this.updatePreference('COMMENT', prefs.comments),
      this.updatePreference('CONNECTION_REQUEST', prefs.connections),
      this.updatePreference('CONNECTION_ACCEPTED', prefs.connections),
      this.updatePreference('FOLLOW', prefs.follows),
      this.updatePreference('SHARE', prefs.reposts)
    ]);
  }

  setUnreadCount(count: number): void { this.unreadCountSubject.next(count); }

  decrementUnreadCount(): void {
    const c = this.unreadCountSubject.value;
    if (c > 0) this.unreadCountSubject.next(c - 1);
  }
}
