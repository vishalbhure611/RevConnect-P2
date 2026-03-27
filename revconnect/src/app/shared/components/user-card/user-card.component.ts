import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { UserResponse } from '../../models/models';
import { ConnectionService } from '../../../core/services/connection.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-user-card',
  templateUrl: './user-card.component.html',
  styleUrls: ['./user-card.component.scss']
})
export class UserCardComponent {
  @Input() user!: UserResponse;
  @Input() showActions = true;
  @Output() actionDone = new EventEmitter<void>();

  loading = false;

  constructor(
    private connectionService: ConnectionService,
    public authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  get displayName(): string {
    return this.user.profile?.fullName || this.user.username;
  }

  get isCurrentUser(): boolean {
    return this.user.id === this.authService.userId;
  }

  get isPersonal(): boolean {
    return this.user.role === 'PERSONAL';
  }

  get isBusinessOrCreator(): boolean {
    return this.user.role === 'BUSINESS' || this.user.role === 'CREATOR';
  }

  connect(): void {
    this.loading = true;
    this.connectionService.sendRequest(this.authService.userId!, this.user.id).subscribe({
      next: () => {
        this.user = { ...this.user, connectionStatus: 'PENDING' };
        this.toastService.success('Connection request sent!');
        this.loading = false;
        this.actionDone.emit();
      },
      error: (err) => {
        this.toastService.error(err.error?.message || 'Failed to send request');
        this.loading = false;
      }
    });
  }

  follow(): void {
    this.loading = true;
    this.connectionService.follow(this.user.id).subscribe({
      next: () => {
        this.user = { ...this.user, isFollowing: true };
        this.toastService.success(`Following ${this.displayName}`);
        this.loading = false;
        this.actionDone.emit();
      },
      error: (err) => {
        this.toastService.error(err.error?.message || 'Failed to follow');
        this.loading = false;
      }
    });
  }

  unfollow(): void {
    this.loading = true;
    this.connectionService.unfollow(this.user.id).subscribe({
      next: () => {
        this.user = { ...this.user, isFollowing: false };
        this.toastService.info(`Unfollowed ${this.displayName}`);
        this.loading = false;
        this.actionDone.emit();
      },
      error: () => {
        this.toastService.error('Failed to unfollow');
        this.loading = false;
      }
    });
  }

  goToProfile(): void {
    this.router.navigate(['/profile', this.user.id]);
  }
}
