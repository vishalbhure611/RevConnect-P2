import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, switchMap, tap } from 'rxjs';
import { Router } from '@angular/router';
import { CreateUserRequest, LoginRequest, UserResponse } from '../../shared/models/models';
import { environment } from '../../../environments/environment';
import { UserService } from './user.service';

export interface Session {
  token: string;
  userId: number;
  username: string;
  email: string;
  role: string;
  profilePictureUrl?: string;
}

function parseJwt(token: string): any {
  try {
    return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
  } catch {
    return {};
  }
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  // ✅ FINAL FIX HERE
  private base = `${environment.apiUrl}/api`;

  private sessionSubject = new BehaviorSubject<Session | null>(this.loadSession());
  currentUser$ = this.sessionSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private userService: UserService
  ) {}

  private loadSession(): Session | null {
    try {
      const s = localStorage.getItem('rc_session');
      return s ? JSON.parse(s) : null;
    } catch {
      return null;
    }
  }

  private save(session: Session): void {
    localStorage.setItem('rc_session', JSON.stringify(session));
    this.sessionSubject.next(session);
  }

  get currentUser(): Session | null { return this.sessionSubject.value; }
  get isLoggedIn(): boolean { return !!this.sessionSubject.value?.token; }
  get token(): string | null { return this.sessionSubject.value?.token ?? null; }
  get userId(): number | null { return this.sessionSubject.value?.userId ?? null; }
  get userRole(): string | null { return this.sessionSubject.value?.role ?? null; }
  get username(): string | null { return this.sessionSubject.value?.username ?? null; }

  isBusinessOrCreator(): boolean {
    return this.userRole === 'BUSINESS' || this.userRole === 'CREATOR';
  }

  // ================= LOGIN =================
  login(request: LoginRequest): Observable<UserResponse> {
    return this.http.post<string>(`${this.base}/auth/login`, request).pipe(
      switchMap((token: string) => {
        const claims = parseJwt(token);

        const partial: Session = {
          token,
          userId: Number(claims['userId'] ?? 0),
          username: claims['sub'] ?? '',
          email: '',
          role: claims['role'] ?? 'PERSONAL'
        };

        this.save(partial);

        return this.http.get<UserResponse>(`${this.base}/users/${partial.userId}`);
      }),
      tap((user: UserResponse) => {
        this.save({
          token: this.token!,
          userId: user.id,
          username: user.username,
          email: user.email,
          role: user.role,
          profilePictureUrl: this.currentUser?.profilePictureUrl
        });
      }),
      tap(() => this.refreshProfilePicture().subscribe({ error: () => {} }))
    );
  }

  // ================= REGISTER =================
  register(request: CreateUserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.base}/auth/register`, request);
  }

  // ================= LOGOUT =================
  logout(): void {
    localStorage.removeItem('rc_session');
    this.sessionSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  updateSession(updates: Partial<Session>): void {
    const cur = this.currentUser;
    if (cur) this.save({ ...cur, ...updates });
  }

  refreshProfilePicture(): Observable<any> {
    const userId = this.userId;
    if (!userId) {
      return of(null);
    }

    return this.userService.getProfile(userId).pipe(
      tap((profile) => {
        this.updateSession({
          profilePictureUrl: profile.profilePictureUrl || ''
        });
      })
    );
  }
}
