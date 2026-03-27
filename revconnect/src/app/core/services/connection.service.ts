import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ConnectionResponse, UserResponse } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class ConnectionService {
  private base = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  // Connections — backend: /api/connections/request/{senderId}/{receiverId}
  sendRequest(senderId: number, receiverId: number): Observable<any> {
    return this.http.post(`${this.base}/connections/request/${senderId}/${receiverId}`, {});
  }

  acceptRequest(connectionId: number): Observable<any> {
    return this.http.put(`${this.base}/connections/accept/${connectionId}`, {});
  }

  rejectRequest(connectionId: number): Observable<any> {
    return this.http.put(`${this.base}/connections/reject/${connectionId}`, {});
  }

  removeConnection(connectionId: number): Observable<any> {
    return this.http.delete(`${this.base}/connections/${connectionId}`);
  }

  getConnections(userId: number): Observable<ConnectionResponse[]> {
    return this.http.get<ConnectionResponse[]>(`${this.base}/connections/list/${userId}`);
  }

  getPendingReceived(userId: number): Observable<ConnectionResponse[]> {
    return this.http.get<ConnectionResponse[]>(`${this.base}/connections/pending/received/${userId}`);
  }

  getPendingSent(userId: number): Observable<ConnectionResponse[]> {
    return this.http.get<ConnectionResponse[]>(`${this.base}/connections/pending/sent/${userId}`);
  }

  // Follow — backend: /api/follows/{followingId}
  follow(targetUserId: number): Observable<any> {
    return this.http.post(`${this.base}/follows/${targetUserId}`, {});
  }

  unfollow(targetUserId: number): Observable<any> {
    return this.http.delete(`${this.base}/follows/${targetUserId}`);
  }
}
