import { Component } from '@angular/core';
import { ToastService, Toast } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss']
})
export class ToastComponent {
  toasts$ = this.toastService.toasts$;

  constructor(private toastService: ToastService) {}

  remove(id: number): void {
    this.toastService.remove(id);
  }

  icon(type: string): string {
    const icons: Record<string, string> = {
      success: '✓', error: '✕', info: 'ℹ', warning: '⚠'
    };
    return icons[type] || 'ℹ';
  }
}
