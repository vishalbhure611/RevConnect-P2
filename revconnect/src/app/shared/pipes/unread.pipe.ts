import { Pipe, PipeTransform } from '@angular/core';
import { NotificationResponse } from '../models/models';

@Pipe({ name: 'unread' })
export class UnreadPipe implements PipeTransform {
  transform(notifications: NotificationResponse[]): number {
    return notifications.filter(n => !n.isRead).length;
  }
}
