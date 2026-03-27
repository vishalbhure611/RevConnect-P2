import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CommentRequest, CommentResponse } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private base = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  getComments(postId: number): Observable<CommentResponse[]> {
    return this.http.get<CommentResponse[]>(`${this.base}/comments/post/${postId}`);
  }

  addComment(postId: number, request: CommentRequest): Observable<CommentResponse> {
    return this.http.post<CommentResponse>(`${this.base}/comments/${postId}`, request);
  }

  deleteComment(commentId: number): Observable<any> {
    return this.http.delete(`${this.base}/comments/${commentId}`);
  }
}
