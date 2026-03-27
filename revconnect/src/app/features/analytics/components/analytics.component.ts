import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../core/services/post.service';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { PostResponse, PostAnalytics, UserInsights, FollowerDemographics } from '../../../shared/models/models';
import { forkJoin, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

interface PostWithAnalytics {
  post: PostResponse;
  analytics: PostAnalytics | null;
}

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit {
  insights: UserInsights | null = null;
  followerDemographics: FollowerDemographics | null = null;
  postsWithAnalytics: PostWithAnalytics[] = [];
  loading = true;

  constructor(
    private postService: PostService,
    private userService: UserService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const uid = this.authService.userId!;
    this.loading = true;

    this.userService.getUserInsights(uid).pipe(
      catchError(() => of(null))
    ).subscribe(insights => {
      this.insights = insights;
    });

    if (this.authService.isBusinessOrCreator()) {
      this.userService.getFollowerDemographics(uid).pipe(
        catchError(() => of(null))
      ).subscribe(data => {
        this.followerDemographics = data;
      });
    }

    this.postService.getUserPosts(uid).pipe(
      catchError(() => of([])),
      switchMap((posts: PostResponse[]) => {
        if (!posts || posts.length === 0) return of([]);
        return forkJoin(
          posts.map(post =>
            this.postService.getPostAnalytics(post.id).pipe(
              catchError(() => of(null)),
              switchMap(analytics => of({ post, analytics: analytics as PostAnalytics | null }))
            )
          )
        );
      })
    ).subscribe({
      next: (items) => {
        this.postsWithAnalytics = items as PostWithAnalytics[];
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  get totalLikes(): number {
    return this.insights?.totalLikes
      ?? this.postsWithAnalytics.reduce((s, p) => s + (p.analytics?.totalLikes || 0), 0);
  }

  get totalComments(): number {
    return this.insights?.totalComments
      ?? this.postsWithAnalytics.reduce((s, p) => s + (p.analytics?.totalComments || 0), 0);
  }

  get totalShares(): number {
    return this.insights?.totalShares
      ?? this.postsWithAnalytics.reduce((s, p) => s + (p.analytics?.totalShares || 0), 0);
  }

  get totalReach(): number {
    return 0
      ?? this.postsWithAnalytics.reduce((s, p) => s + (p.analytics?.reach || 0), 0);
  }

  get topPosts(): PostWithAnalytics[] {
    return [...this.postsWithAnalytics]
      .sort((a, b) => (b.analytics?.totalLikes || b.post.likesCount || 0)
                    - (a.analytics?.totalLikes || a.post.likesCount || 0))
      .slice(0, 10);
  }

  engagementRate(pa: PostWithAnalytics): string {
    const followers = this.insights?.followerCount || 1;
    const eng = (pa.analytics?.totalLikes || pa.post.likesCount || 0)
              + (pa.analytics?.totalComments || pa.post.commentsCount || 0)
              + (pa.analytics?.totalShares || pa.post.sharesCount || 0);
    return ((eng / followers) * 100).toFixed(1);
  }

  barWidth(value: number, max: number): string {
    if (!max) return '2%';
    return `${Math.min(Math.max((value / max) * 100, 2), 100)}%`;
  }

  get maxLikes(): number {
    return Math.max(
      ...this.postsWithAnalytics.map(p => p.analytics?.totalLikes || p.post.likesCount || 0),
      1
    );
  }

  get followerMix(): { label: string; value: number }[] {
    if (!this.followerDemographics) {
      return [];
    }

    return [
      { label: 'Personal', value: this.followerDemographics.personalFollowers || 0 },
      { label: 'Creator', value: this.followerDemographics.creatorFollowers || 0 },
      { label: 'Business', value: this.followerDemographics.businessFollowers || 0 }
    ];
  }

  followerMixWidth(value: number): string {
    const total = this.followerDemographics?.totalFollowers || 0;
    if (!total) return '0%';
    return `${Math.max((value / total) * 100, value > 0 ? 8 : 0)}%`;
  }
}
