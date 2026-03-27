import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../core/services/post.service';
import { HashtagService } from '../../../core/services/hashtag.service';
import { AuthService } from '../../../core/services/auth.service';
import { PostResponse, HashtagResponse } from '../../../shared/models/models';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

type FeedTab = 'for-you' | 'trending';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit {
  posts: PostResponse[] = [];
  trendingPosts: PostResponse[] = [];
  trendingHashtags: HashtagResponse[] = [];
  loading = false;
  activeTab: FeedTab = 'for-you';
  filterUserType = '';
  filterPostType = '';

  constructor(
    private postService: PostService,
    private hashtagService: HashtagService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadFeed();
    this.loadTrendingHashtags();
  }

  loadFeed(): void {
    const userId = this.authService.userId!;
    this.loading = true;
    this.postService.getFeed(userId, undefined, this.filterUserType || undefined).pipe(
      catchError(() => of([]))
    ).subscribe(posts => {
      this.posts = Array.isArray(posts) ? posts : [];
      this.loading = false;
    });
  }

  loadTrendingHashtags(): void {
    this.hashtagService.getTrendingHashtags().pipe(
      catchError(() => of([]))
    ).subscribe(tags => {
      this.trendingHashtags = (Array.isArray(tags) ? tags : []).slice(0, 8);
      // Load trending posts from top hashtag if available
      if (this.trendingHashtags.length > 0) {
        this.postService.getPostsByHashtag(this.trendingHashtags[0].name).pipe(
          catchError(() => of([]))
        ).subscribe(p => {
          this.trendingPosts = Array.isArray(p) ? p : [];
        });
      }
    });
  }

  onPosted(post: PostResponse): void {
    this.posts = [post, ...this.posts];
  }

  onDeleted(id: number): void {
    this.posts = this.posts.filter(p => p.id !== id);
    this.trendingPosts = this.trendingPosts.filter(p => p.id !== id);
  }

  setTab(tab: FeedTab): void {
    this.activeTab = tab;
  }

  get activePosts(): PostResponse[] {
    let list = this.activeTab === 'trending' ? this.trendingPosts : this.posts;
    if (this.filterPostType) list = list.filter(p => p.postType === this.filterPostType);
    return list;
  }

  searchHashtag(name: string): void {
    this.loading = true;
    this.postService.getPostsByHashtag(name).pipe(
      catchError(() => of([]))
    ).subscribe(posts => {
      this.posts = Array.isArray(posts) ? posts : [];
      this.loading = false;
      this.activeTab = 'for-you';
    });
  }

  clearFilters(): void {
    this.filterPostType = '';
    this.filterUserType = '';
    this.loadFeed();
  }

  onUserTypeChange(): void {
    this.loadFeed();
  }

  trackById(_: number, post: PostResponse): number {
    return post.id;
  }
}
