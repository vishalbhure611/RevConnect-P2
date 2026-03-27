import { Component, Input, OnChanges } from '@angular/core';

@Component({
  selector: 'app-avatar',
  template: `
    <img *ngIf="validSrc" [src]="validSrc" [alt]="name"
      class="avatar-img" [ngClass]="sizeClass"
      (error)="onImgError()" />
    <div *ngIf="!validSrc" class="avatar-placeholder" [ngClass]="sizeClass"
      [style.background]="getColor()">
      {{ initials }}
    </div>
  `,
  styles: [`
    :host { display: inline-flex; }
    .avatar-img {
      object-fit: cover;
      border-radius: 50%;
      flex-shrink: 0;
      border: 2px solid var(--border);
      display: block;
    }
    .avatar-placeholder {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-family: var(--font-display);
      font-weight: 700;
      border-radius: 50%;
      flex-shrink: 0;
      border: 2px solid var(--border);
      color: white;
    }
    .avatar-xs  { width: 28px; height: 28px; font-size: 10px; }
    .avatar-sm  { width: 36px; height: 36px; font-size: 13px; }
    .avatar-md  { width: 44px; height: 44px; font-size: 16px; }
    .avatar-lg  { width: 56px; height: 56px; font-size: 20px; }
    .avatar-xl  { width: 80px; height: 80px; font-size: 28px; }
    .avatar-xxl { width: 112px; height: 112px; font-size: 40px; }
  `]
})
export class AvatarComponent implements OnChanges {
  @Input() src: string | null | undefined = null;
  @Input() name: string = '?';
  @Input() size: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | 'xxl' = 'md';

  validSrc: string | null = null;

  ngOnChanges(): void {
    this.validSrc = this.src || null;
  }

  onImgError(): void {
    this.validSrc = null;
  }

  get sizeClass(): string {
    return `avatar-${this.size}`;
  }

  get initials(): string {
    if (!this.name) return '?';
    const trimmed = this.name.trim();
    return trimmed ? trimmed[0].toUpperCase() : '?';
  }

  getColor(): string {
    const colors = ['#6c63ff','#0099cc','#e91e8c','#00c896','#ff6b35','#a855f7'];
    let hash = 0;
    for (let i = 0; i < this.name.length; i++) {
      hash = this.name.charCodeAt(i) + ((hash << 5) - hash);
    }
    return colors[Math.abs(hash) % colors.length];
  }
}
