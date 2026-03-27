import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, catchError, map } from 'rxjs/operators';
import { of, forkJoin } from 'rxjs';
import { ConnectionService } from '../../../core/services/connection.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ConnectionResponse } from '../../../shared/models/models';
import { UserService } from '../../../core/services/user.service';

type NetworkTab = 'connections' | 'requests' | 'sent';

@Component({
  selector: 'app-network',
  templateUrl: './network.component.html',
  styleUrls: ['./network.component.scss']
})
export class NetworkComponent implements OnInit, OnDestroy {
  activeTab: NetworkTab = 'connections';
  connections: ConnectionResponse[] = [];
  pendingReceived: ConnectionResponse[] = [];
  pendingSent: ConnectionResponse[] = [];
  loading = false;

  private destroy$ = new Subject<void>();

  constructor(
    private connectionService: ConnectionService,
    private userService: UserService,
    public authService: AuthService,
    private toastService: ToastService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadAll(): void {
    const uid = this.authService.userId!;
    this.loading = true;

    this.connectionService.getConnections(uid).pipe(
      catchError(() => of([])), takeUntil(this.destroy$)
    ).subscribe(c => {
      this.enrichConnections(Array.isArray(c) ? c : []).subscribe(enriched => {
        this.connections = enriched;
      });
    });

    this.connectionService.getPendingReceived(uid).pipe(
      catchError(() => of([])), takeUntil(this.destroy$)
    ).subscribe(c => {
      this.enrichConnections(Array.isArray(c) ? c : []).subscribe(enriched => {
        this.pendingReceived = enriched;
      });
    });

    this.connectionService.getPendingSent(uid).pipe(
      catchError(() => of([])), takeUntil(this.destroy$)
    ).subscribe(c => {
      this.enrichConnections(Array.isArray(c) ? c : []).subscribe(enriched => {
        this.pendingSent = enriched;
        this.loading = false;
      });
    });
  }

  accept(connectionId: number): void {
    this.connectionService.acceptRequest(connectionId).subscribe({
      next: () => {
        const req = this.pendingReceived.find(c => c.id === connectionId);
        if (req) {
          this.pendingReceived = this.pendingReceived.filter(c => c.id !== connectionId);
          this.connections = [...this.connections, { ...req, status: 'ACCEPTED' }];
        }
        this.notificationService.getUnreadCount(this.authService.userId!).subscribe({ error: () => {} });
        this.toastService.success('Connection accepted!');
      },
      error: () => this.toastService.error('Failed to accept request')
    });
  }

  reject(connectionId: number): void {
    this.connectionService.rejectRequest(connectionId).subscribe({
      next: () => {
        this.pendingReceived = this.pendingReceived.filter(c => c.id !== connectionId);
        this.toastService.info('Request declined');
      },
      error: () => this.toastService.error('Failed to decline request')
    });
  }

  remove(connectionId: number): void {
    this.connectionService.removeConnection(connectionId).subscribe({
      next: () => {
        this.connections = this.connections.filter(c => c.id !== connectionId);
        this.pendingSent = this.pendingSent.filter(c => c.id !== connectionId);
        this.toastService.info('Connection removed');
      },
      error: () => this.toastService.error('Failed to remove connection')
    });
  }

  setTab(tab: NetworkTab): void { this.activeTab = tab; }

  get requestBadge(): number { return this.pendingReceived.length; }

  getPeerUsername(conn: ConnectionResponse): string {
    const myUsername = this.authService.username!;
    return conn.senderUsername === myUsername ? conn.receiverUsername : conn.senderUsername;
  }

  getPeerUser(conn: ConnectionResponse): any {
    return { username: this.getPeerUsername(conn), profile: null };
  }

  getPeerAvatar(conn: ConnectionResponse): string | null {
    const isSender = conn.senderUsername === this.authService.username;
    return (isSender ? conn.receiver?.profile?.profilePictureUrl : conn.sender?.profile?.profilePictureUrl) || null;
  }

  getSenderAvatar(conn: ConnectionResponse): string | null {
    return conn.sender?.profile?.profilePictureUrl || null;
  }

  getReceiverAvatar(conn: ConnectionResponse): string | null {
    return conn.receiver?.profile?.profilePictureUrl || null;
  }

  private enrichConnections(connections: ConnectionResponse[]) {
    if (!connections.length) {
      return of([]);
    }

    return forkJoin(
      connections.map(conn =>
        forkJoin({
          senderProfile: conn.senderId
            ? this.userService.getProfile(conn.senderId).pipe(catchError(() => of(null)))
            : of(null),
          receiverProfile: conn.receiverId
            ? this.userService.getProfile(conn.receiverId).pipe(catchError(() => of(null)))
            : of(null)
        }).pipe(
          map(({ senderProfile, receiverProfile }) => ({
            ...conn,
            sender: conn.senderId ? {
              id: conn.senderId,
              username: conn.senderUsername,
              email: '',
              role: 'PERSONAL' as any,
              profile: senderProfile || undefined
            } : conn.sender,
            receiver: conn.receiverId ? {
              id: conn.receiverId,
              username: conn.receiverUsername,
              email: '',
              role: 'PERSONAL' as any,
              profile: receiverProfile || undefined
            } : conn.receiver
          }))
        )
      )
    );
  }
}
