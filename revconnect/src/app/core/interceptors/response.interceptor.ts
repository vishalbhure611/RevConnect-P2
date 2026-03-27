import { Injectable } from '@angular/core';
import {
  HttpInterceptor, HttpRequest, HttpHandler,
  HttpEvent, HttpResponse
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

function normalizePost(post: any): any {
  if (!post || typeof post !== 'object') return post;
  return {
    ...post,
    likesCount: post.likesCount ?? post.totalLikes ?? 0,
    commentsCount: post.commentsCount ?? post.totalComments ?? 0,
    sharesCount: post.sharesCount ?? post.totalShares ?? 0,
    actorUsername: post.actorUsername ?? post.triggeredBy ?? null,
  };
}

function normalizeNotification(n: any): any {
  if (!n || typeof n !== 'object') return n;
  return {
    ...n,
    isRead: n.isRead ?? n.read ?? false,
    actorUsername: n.actorUsername ?? n.triggeredBy ?? null,
  };
}

function deepNormalize(body: any, url: string): any {
  if (Array.isArray(body)) {
    if (url.includes('/posts') || url.includes('/feed')) return body.map(normalizePost);
    if (url.includes('/notifications')) return body.map(normalizeNotification);
    return body;
  }
  if (body && typeof body === 'object') {
    if (url.includes('/posts') || url.includes('/feed')) return normalizePost(body);
    if (url.includes('/notifications')) return normalizeNotification(body);
  }
  return body;
}

@Injectable()
export class ResponseInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      map(event => {
        if (
          event instanceof HttpResponse &&
          event.body !== null &&
          typeof event.body === 'object' &&
          'success' in event.body &&
          'data' in event.body
        ) {
          const unwrapped = event.body.data !== undefined ? event.body.data : event.body;
          const normalized = deepNormalize(unwrapped, req.url);
          return event.clone({ body: normalized });
        }
        return event;
      })
    );
  }
}
