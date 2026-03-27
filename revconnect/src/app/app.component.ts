import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/services/auth.service';
import { NotificationService } from './core/services/notification.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn && this.authService.userId) {
      this.notificationService.getUnreadCount(this.authService.userId).subscribe();
      this.authService.refreshProfilePicture().subscribe({ error: () => {} });
    }
  }
}
