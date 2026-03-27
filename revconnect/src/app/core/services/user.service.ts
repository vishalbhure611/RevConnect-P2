import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserResponse, ProfileRequest, Profile, UserInsights, UpdateAccountRequest, FollowerDemographics } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class UserService {
  private base = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  // GET /api/users/{id}
  getUserById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.base}/users/${id}`);
  }

  updateAccount(id: number, request: UpdateAccountRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.base}/users/${id}`, request);
  }

  // GET /api/profile/username/{username}
  getUserByUsername(username: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.base}/profile/username/${username}`);
  }

  // GET /api/users/search?query=xxx
  searchUsers(query: string): Observable<UserResponse[]> {
    const params = new HttpParams().set('keyword', query);
    return this.http.get<UserResponse[]>(`${this.base}/users/search`, { params });
  }

  // GET /api/profile/{userId}
  getProfile(userId: number): Observable<Profile> {
    return this.http.get<Profile>(`${this.base}/profile/${userId}`);
  }

  // PUT /api/profile/{userId}
  updateProfile(userId: number, profile: ProfileRequest): Observable<Profile> {
    return this.http.put<Profile>(`${this.base}/profile/${userId}`, profile);
  }

  // PUT /api/users/{id}/privacy
  setPrivacy(userId: number, privacy: 'PUBLIC' | 'PRIVATE'): Observable<any> {
    return this.http.put<any>(`${this.base}/users/${userId}/privacy`, { privacy });
  }

  // GET /api/analytics/users/{userId}/insights
  getUserInsights(userId: number): Observable<UserInsights> {
    return this.http.get<UserInsights>(`${this.base}/analytics/users/${userId}/insights`);
  }

  getFollowerDemographics(userId: number): Observable<FollowerDemographics> {
    return this.http.get<FollowerDemographics>(`${this.base}/analytics/users/${userId}/followers/demographics`);
  }

  // GET /api/follows/followers/{userId}
  getFollowers(userId: number): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.base}/follows/followers/${userId}`);
  }

  // GET /api/follows/following/{userId}
  getFollowing(userId: number): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.base}/follows/following/${userId}`);
  }
}
