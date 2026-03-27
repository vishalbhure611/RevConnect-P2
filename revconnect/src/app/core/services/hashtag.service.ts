import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { HashtagResponse } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class HashtagService {
  private base = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  getTrendingHashtags(): Observable<HashtagResponse[]> {
    return this.http.get<HashtagResponse[]>(`${this.base}/hashtags/trending`);
  }

  // GET /api/hashtags/{name} — returns posts for that hashtag
  getPostsByHashtag(name: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/hashtags/${name}`);
  }

  searchHashtags(query: string): Observable<HashtagResponse[]> {
    return this.http.get<HashtagResponse[]>(`${this.base}/hashtags/trending`);
  }
}
