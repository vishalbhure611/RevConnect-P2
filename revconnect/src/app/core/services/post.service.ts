import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PostRequest, PostResponse, PostAnalytics } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class PostService {
  private base = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  createPost(post: PostRequest): Observable<PostResponse> {
    return this.http.post<PostResponse>(`${this.base}/posts`, post);
  }

  updatePost(postId: number, post: Partial<PostRequest>): Observable<PostResponse> {
    return this.http.put<PostResponse>(`${this.base}/posts/${postId}`, post);
  }

  deletePost(postId: number): Observable<any> {
    return this.http.delete(`${this.base}/posts/${postId}`);
  }

  getPostById(postId: number): Observable<PostResponse> {
    return this.http.get<PostResponse>(`${this.base}/posts/${postId}`);
  }

  getUserPosts(userId: number): Observable<PostResponse[]> {
    return this.http.get<PostResponse[]>(`${this.base}/posts/user/${userId}`);
  }

  getFeed(userId: number, postType?: string, userRole?: string): Observable<PostResponse[]> {
    let params = new HttpParams();
    if (postType) params = params.set('postType', postType);
    if (userRole) params = params.set('userRole', userRole);
    return this.http.get<PostResponse[]>(`${this.base}/feed/${userId}`, { params });
  }

  // Trending posts come from hashtag trending - use feed as fallback
  getTrendingPosts(): Observable<PostResponse[]> {
    return this.http.get<PostResponse[]>(`${this.base}/feed/trending`);
  }

  // GET /api/hashtags/{name} returns hashtag object with posts
  getPostsByHashtag(hashtag: string): Observable<PostResponse[]> {
    return this.http.get<PostResponse[]>(`${this.base}/posts/hashtag/${hashtag}`);
  }

  likePost(postId: number): Observable<any> {
    return this.http.post<any>(`${this.base}/likes/${postId}`, {});
  }

  unlikePost(postId: number): Observable<any> {
    return this.http.delete<any>(`${this.base}/likes/${postId}`);
  }

  sharePost(postId: number, userId: number): Observable<any> {
    return this.http.post<any>(`${this.base}/shares/${postId}/${userId}`, {});
  }

  repostPost(postId: number): Observable<PostResponse> {
    return this.http.post<PostResponse>(`${this.base}/posts/${postId}/repost`, {});
  }

  pinPost(postId: number): Observable<any> {
    return this.http.put<any>(`${this.base}/posts/${postId}/pin?value=true`, {});
  }

  unpinPost(postId: number): Observable<any> {
    return this.http.put<any>(`${this.base}/posts/${postId}/pin?value=false`, {});
  }

  getPostAnalytics(postId: number): Observable<PostAnalytics> {
    return this.http.get<PostAnalytics>(`${this.base}/analytics/posts/${postId}`);
  }
}
