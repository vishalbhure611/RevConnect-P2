import { Component, Input, OnInit, OnChanges } from '@angular/core';
import { CommentService } from '../../../core/services/comment.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { CommentResponse } from '../../models/models';

@Component({
  selector: 'app-comment-section',
  templateUrl: './comment-section.component.html',
  styleUrls: ['./comment-section.component.scss']
})
export class CommentSectionComponent implements OnChanges {
  @Input() postId!: number;
  @Input() isOpen = false;
  @Input() commentCount = 0;

  comments: CommentResponse[] = [];
  newComment = '';
  loading = false;
  submitting = false;

  constructor(
    private commentService: CommentService,
    public authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnChanges(): void {
    if (this.isOpen && this.comments.length === 0) {
      this.loadComments();
    }
  }

  loadComments(): void {
    this.loading = true;
    this.commentService.getComments(this.postId).subscribe({
      next: (c) => { this.comments = Array.isArray(c) ? c : []; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  submitComment(): void {
    if (!this.newComment.trim() || this.submitting) return;
    this.submitting = true;
    this.commentService.addComment(this.postId, { content: this.newComment.trim() }).subscribe({
      next: (c) => {
        this.comments = [...this.comments, c];
        this.newComment = '';
        this.submitting = false;
      },
      error: () => {
        this.toastService.error('Failed to post comment');
        this.submitting = false;
      }
    });
  }

  deleteComment(commentId: number): void {
    this.commentService.deleteComment(commentId).subscribe({
      next: () => { this.comments = this.comments.filter(c => c.id !== commentId); },
      error: () => this.toastService.error('Failed to delete comment')
    });
  }

  getCurrentUserAvatar(): string | null {
    return this.authService.currentUser?.profilePictureUrl || null;
  }

  getCommentAvatar(commentUsername: string): string | null {
    return commentUsername === this.authService.username
      ? this.getCurrentUserAvatar()
      : null;
  }
}
