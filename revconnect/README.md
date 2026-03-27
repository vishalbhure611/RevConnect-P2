# RevConnect Angular Frontend

A full-stack social media frontend built with **Angular 17**, designed to integrate with the RevConnect Spring Boot backend.

---

## 🚀 Quick Setup

### Prerequisites
- Node.js 18+
- npm 9+
- Angular CLI 17: `npm install -g @angular/cli@17`

### Install & Run
```bash
# Extract the ZIP, then:
cd revconnect
npm install
ng serve
# Open http://localhost:4200
```

### Connect to backend
Edit `src/environments/environment.ts`:
```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'   // ← your Spring Boot URL
};
```

---

## 📁 Project Structure

```
src/app/
├── core/
│   ├── guards/          auth.guard.ts, guest.guard.ts
│   ├── interceptors/    jwt.interceptor.ts (auto-attaches Bearer token)
│   └── services/        auth, user, post, comment, connection,
│                        notification, product, hashtag, toast
├── shared/
│   ├── components/      sidebar, topbar, layout, post-card,
│   │                    post-composer, comment-section,
│   │                    user-card, avatar, confirm-modal, toast
│   ├── models/          models.ts (all TypeScript interfaces)
│   └── pipes/           unread.pipe.ts
└── features/
    ├── auth/            login, register
    ├── feed/            personalized feed + trending
    ├── profile/         view/edit profile, posts, followers, products
    ├── network/         connections, requests, sent
    ├── notifications/   list, mark-read, preferences
    ├── analytics/       insights dashboard, post performance table
    └── search/          people, posts, hashtags
```

---

## ✅ Features Implemented

### Authentication
- [x] Login with username/email + password
- [x] Register with role selection (Personal / Business / Creator)
- [x] JWT auto-injected on all API calls
- [x] Auto-redirect logged-in users from auth pages

### Profile
- [x] View own and others' profiles
- [x] Edit profile (name, bio, picture URL, location, website, privacy)
- [x] Business-specific fields: address, hours, category, contact
- [x] Public/Private privacy toggle
- [x] Follow / Unfollow business & creator accounts
- [x] Connect / Disconnect personal users
- [x] Follower / Following tabs
- [x] Products showcase tab (business/creator)

### Posts
- [x] Create posts with hashtags
- [x] Promotional posts with CTA buttons (business/creator)
- [x] Schedule posts for future publishing
- [x] Tag products in posts
- [x] Edit & delete own posts
- [x] Pin/unpin posts to profile

### Social
- [x] Like / Unlike posts (optimistic UI)
- [x] Comment on posts, delete own comments
- [x] Share/repost with attribution

### Feed
- [x] Personalized feed (connections + following)
- [x] Trending posts tab
- [x] Filter by user type and post type
- [x] Trending hashtags sidebar
- [x] Click hashtag → filter feed

### Network
- [x] Send / Accept / Reject connection requests
- [x] View pending received and sent requests
- [x] Remove existing connections
- [x] Follow / Unfollow accounts

### Notifications
- [x] List all notifications with icons and messages
- [x] Mark individual or all as read
- [x] Unread count badge in sidebar
- [x] Notification type preferences (toggle each type on/off)

### Analytics (Business/Creator only)
- [x] Overview stats: followers, likes, comments, shares, reach, posts
- [x] Per-post analytics table
- [x] Engagement rate calculation
- [x] Visual performance bar

### Search
- [x] Search users by name/username
- [x] Search posts by hashtag
- [x] Browse hashtags
- [x] Trending hashtags on empty state

---

## 🎨 Design System

| Token | Value |
|---|---|
| `--bg-base` | `#0a0a0f` dark base |
| `--accent` | `#6c63ff` violet |
| `--electric` | `#00e5ff` cyan accent |
| `--font-display` | Syne (headings) |
| `--font-body` | DM Sans (body text) |

---

## 🔌 API Mapping

All API calls match the RevConnect Spring Boot backend exactly:

| Service | Endpoints |
|---|---|
| Auth | `POST /auth/login`, `POST /auth/register` |
| Profile | `GET/PUT /profile/{userId}` |
| Posts | `GET/POST/PUT/DELETE /posts`, `GET /posts/user/{id}` |
| Feed | `GET /feed/{userId}`, `GET /feed/trending` |
| Likes | `POST/DELETE /likes/{postId}` |
| Comments | `GET/POST /comments/post/{id}`, `DELETE /comments/{id}` |
| Shares | `POST/DELETE /shares/{postId}` |
| Connections | `POST /connections/request/{id}`, `PUT /connections/accept/{id}` |
| Follow | `POST/DELETE /follow/{targetId}` |
| Notifications | `GET/PUT /notifications/{userId}` |
| Analytics | `GET /analytics/insights/{userId}`, `GET /analytics/post/{postId}` |
| Hashtags | `GET /hashtags/trending`, `GET /hashtags/{name}/posts` |
| Products | `GET/POST/PUT/DELETE /products` |

---

## 🛠 Common Customizations

**Change backend URL:** `src/environments/environment.ts`  
**Add a new route:** `src/app/app-routing.module.ts`  
**Extend a model:** `src/app/shared/models/models.ts`  
**Theme colors:** `src/styles.scss` → `:root` CSS variables
