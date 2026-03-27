import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UnreadPipe } from './pipes/unread.pipe';

import { ToastComponent } from './components/toast/toast.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { TopbarComponent } from './components/topbar/topbar.component';
import { PostCardComponent } from './components/post-card/post-card.component';
import { UserCardComponent } from './components/user-card/user-card.component';
import { AvatarComponent } from './components/avatar/avatar.component';
import { PostComposerComponent } from './components/post-composer/post-composer.component';
import { CommentSectionComponent } from './components/comment-section/comment-section.component';
import { LayoutComponent } from './components/layout/layout.component';
import { ConfirmModalComponent } from './components/confirm-modal/confirm-modal.component';

const PIPES = [UnreadPipe];

const COMPONENTS = [
  ToastComponent,
  SidebarComponent,
  TopbarComponent,
  PostCardComponent,
  UserCardComponent,
  AvatarComponent,
  PostComposerComponent,
  CommentSectionComponent,
  LayoutComponent,
  ConfirmModalComponent
];

@NgModule({
  declarations: [...COMPONENTS, ...PIPES],
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  exports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    ...COMPONENTS,
    ...PIPES
  ]
})
export class SharedModule {}
