import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, forkJoin } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, takeUntil } from 'rxjs/operators';
import { UserService } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { HashtagService } from '../../../core/services/hashtag.service';
import { UserResponse, PostResponse, HashtagResponse } from '../../../shared/models/models';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

type SearchTab = 'people' | 'posts' | 'hashtags';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit, OnDestroy {
  query = '';
  activeTab: SearchTab = 'people';
  users: UserResponse[] = [];
  posts: PostResponse[] = [];
  hashtags: HashtagResponse[] = [];
  trendingHashtags: HashtagResponse[] = [];
  loading = false;
  searched = false;

  private destroy$ = new Subject<void>();
  private searchInput$ = new Subject<string>();

  constructor(
    private userService: UserService,
    private postService: PostService,
    private hashtagService: HashtagService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.hashtagService.getTrendingHashtags().pipe(
      catchError(() => of([])),
      takeUntil(this.destroy$)
    ).subscribe(tags => {
      this.trendingHashtags = (Array.isArray(tags) ? tags : []).slice(0, 12);
    });

    this.route.queryParams.pipe(takeUntil(this.destroy$)).subscribe(params => {
      if (params['q']) {
        this.query = params['q'];
        this.doSearch(this.query);
      } else if (params['hashtag']) {
        this.query = '#' + params['hashtag'];
        this.activeTab = 'posts';
        this.searchByHashtag(params['hashtag']);
      }
    });

    this.searchInput$.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(q => this.doSearch(q));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onQueryChange(): void {
    if (!this.query.trim()) {
      this.users = []; this.posts = []; this.hashtags = []; this.searched = false;
      return;
    }
    this.searchInput$.next(this.query);
  }

  doSearch(q: string): void {
    if (!q.trim()) return;
    this.searched = true;
    this.loading = true;
    const clean = q.replace(/^#/, '').trim();

    this.userService.searchUsers(clean).pipe(
      catchError(() => of([]))
    ).subscribe(u => {
      const users = Array.isArray(u) ? u : [];
      this.enrichUsersWithProfiles(users).subscribe(enrichedUsers => {
        this.users = enrichedUsers;
        this.loading = false;
      });
    });

    if (q.startsWith('#')) {
      this.searchByHashtag(clean);
    } else {
      this.postService.getPostsByHashtag(clean).pipe(
        catchError(() => of([]))
      ).subscribe(p => {
        this.posts = Array.isArray(p) ? p : [];
      });
    }

    this.hashtagService.searchHashtags(clean).pipe(
      catchError(() => of([]))
    ).subscribe(h => {
      this.hashtags = Array.isArray(h) ? h : [];
    });
  }

  searchByHashtag(name: string): void {
    this.loading = true;
    this.postService.getPostsByHashtag(name).pipe(
      catchError(() => of([]))
    ).subscribe(p => {
      this.posts = Array.isArray(p) ? p : [];
      this.loading = false;
    });
  }

  setTab(tab: SearchTab): void { this.activeTab = tab; }

  clickHashtag(name: string): void {
    this.query = '#' + name;
    this.activeTab = 'posts';
    this.searched = true;
    this.searchByHashtag(name);
    this.router.navigate([], { queryParams: { hashtag: name }, replaceUrl: true });
  }

  clearSearch(): void {
    this.query = '';
    this.users = []; this.posts = []; this.hashtags = [];
    this.searched = false;
    this.router.navigate([], { queryParams: {}, replaceUrl: true });
  }

  private enrichUsersWithProfiles(users: UserResponse[]) {
    if (!users.length) {
      return of([]);
    }

    return forkJoin(
      users.map(user =>
        this.userService.getProfile(user.id).pipe(
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(profiles => users.map((user, index) => ({
        ...user,
        profile: profiles[index] || user.profile,
        followersCount: profiles[index]?.followersCount ?? user.followersCount,
        followingCount: profiles[index]?.followingCount ?? user.followingCount,
        connectionsCount: profiles[index]?.connectionsCount ?? user.connectionsCount,
        isFollowing: profiles[index]?.isFollowing ?? user.isFollowing,
        isConnected: profiles[index]?.isConnected ?? user.isConnected,
        connectionStatus: profiles[index]?.connectionStatus ?? user.connectionStatus
      })))
    );
  }
}
