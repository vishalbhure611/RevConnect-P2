import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PostResponse } from '../../models/models';
import { PostService } from '../../../core/services/post.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-post-card',
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.scss']
})
export class PostCardComponent implements OnInit {
  @Input() post!: PostResponse;
  @Output() deleted = new EventEmitter<number>();
  @Output() updated = new EventEmitter<PostResponse>();

  showComments = false;
  showDropdown = false;
  isLiked = false;
  likeCount = 0;
  shareCount = 0;
  likeLoading = false;
  shareLoading = false;
  showEditModal = false;
  showDeleteConfirm = false;
  editContent = '';

  constructor(
    private postService: PostService,
    public authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isLiked = this.post.isLiked || false;
    this.likeCount = this.post.likesCount || 0;
    this.shareCount = this.post.sharesCount || 0;
  }

  get isOwner(): boolean {
    return this.post.username === this.authService.username;
  }

  get displayName(): string {
    return this.post.username;
  }

  get authorAvatar(): string | null {
    if (this.post.user?.profile?.profilePictureUrl) {
      return this.post.user.profile.profilePictureUrl;
    }

    return this.post.username === this.authService.username
      ? this.authService.currentUser?.profilePictureUrl || null
      : null;
  }

  get timeAgo(): string {
    const now = new Date();
    const created = new Date(this.post.createdAt);
    const diff = Math.floor((now.getTime() - created.getTime()) / 1000);
    if (diff < 60) return `${diff}s`;
    if (diff < 3600) return `${Math.floor(diff / 60)}m`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h`;
    if (diff < 604800) return `${Math.floor(diff / 86400)}d`;
    return created.toLocaleDateString();
  }

  toggleLike(): void {
    if (this.likeLoading) return;
    this.likeLoading = true;
    if (this.isLiked) {
      this.isLiked = false;
      this.likeCount--;
      this.postService.unlikePost(this.post.id).subscribe({
        error: () => { this.isLiked = true; this.likeCount++; },
        complete: () => { this.likeLoading = false; }
      });
    } else {
      this.isLiked = true;
      this.likeCount++;
      this.postService.likePost(this.post.id).subscribe({
        error: () => { this.isLiked = false; this.likeCount--; },
        complete: () => { this.likeLoading = false; }
      });
    }
  }

  sharePost(): void {
    if (this.shareLoading) return;
    this.shareLoading = true;
    this.postService.sharePost(this.post.id, this.authService.userId!).subscribe({
      next: () => {
        this.shareCount++;
        this.toastService.success('Post shared!');
        this.shareLoading = false;
      },
      error: (err) => {
        this.toastService.error(err.error?.message || 'Could not share post');
        this.shareLoading = false;
      }
    });
  }

  openEdit(): void {
    this.editContent = this.post.content;
    this.showEditModal = true;
    this.showDropdown = false;
  }

  saveEdit(): void {
    if (!this.editContent.trim()) return;
    this.postService.updatePost(this.post.id, { content: this.editContent }).subscribe({
      next: (updated) => {
        this.post = { ...this.post, content: updated.content };
        this.updated.emit(this.post);
        this.showEditModal = false;
        this.toastService.success('Post updated');
      },
      error: () => this.toastService.error('Failed to update post')
    });
  }

  confirmDelete(): void {
    this.showDeleteConfirm = true;
    this.showDropdown = false;
  }

  deletePost(): void {
    this.postService.deletePost(this.post.id).subscribe({
      next: () => {
        this.deleted.emit(this.post.id);
        this.toastService.success('Post deleted');
        this.showDeleteConfirm = false;
      },
      error: () => this.toastService.error('Failed to delete post')
    });
  }

  togglePin(): void {
    const obs = this.post.pinned
      ? this.postService.unpinPost(this.post.id)
      : this.postService.pinPost(this.post.id);
    obs.subscribe({
      next: () => {
        this.post = { ...this.post, pinned: !this.post.pinned };
        this.toastService.success(this.post.pinned ? 'Post pinned' : 'Post unpinned');
      },
      error: () => this.toastService.error('Failed to update pin')
    });
    this.showDropdown = false;
  }

  navigateToProfile(username: string): void {
    this.router.navigate(['/search'], { queryParams: { q: username } });
  }

  navigateToHashtag(tag: string): void {
    this.router.navigate(['/search'], { queryParams: { hashtag: tag } });
  }

  closeDropdown(): void {
    this.showDropdown = false;
  }
}
