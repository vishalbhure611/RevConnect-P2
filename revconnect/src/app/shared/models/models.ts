// ============================================================
// Models exactly matching RevConnect Spring Boot DTOs
// ============================================================

// ── Auth ──────────────────────────────────────────────────────
export interface LoginRequest {
  identifier: string; // email or username
  password: string;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  role: UserRole;
}

export interface AuthResponse {
  token: string;
  userId: number;
  username: string;
  email: string;
  role: UserRole;
}

// ── Enums ─────────────────────────────────────────────────────
export type UserRole = 'PERSONAL' | 'CREATOR' | 'BUSINESS';
export type PostType = 'REGULAR' | 'PROMOTIONAL' | 'EVENT' | 'ANNOUNCEMENT';
export type ProfilePrivacy = 'PUBLIC' | 'PRIVATE';
export type ConnectionStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

// ── User ─────────────────────────────────────────────────────
export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: UserRole;
  profile?: Profile;
  // Enriched by frontend from profile/follow data
  followersCount?: number;
  followingCount?: number;
  connectionsCount?: number;
  isFollowing?: boolean;
  isConnected?: boolean;
  connectionStatus?: string;
}

export interface UpdateAccountRequest {
  username?: string;
  email?: string;
  password?: string;
}

// ── Profile ───────────────────────────────────────────────────
export interface Profile {
  userId: number;
  username: string;
  role: string;
  fullName: string;
  bio: string;
  location: string;
  website: string;
  profilePictureUrl: string;
  privacy: ProfilePrivacy;
  category: string;
  contactEmail: string;
  contactPhone: string;
  businessAddress: string;
  businessHours: string;
  followersCount: number;
  followingCount: number;
  connectionsCount?: number;
  isOwner: boolean;
  isFollowing?: boolean;
  isConnected?: boolean;
  connectionStatus?: string;
  externalLinks: string;
}

export interface ProfileRequest {
  fullName?: string;
  bio?: string;
  location?: string;
  website?: string;
  profilePictureUrl?: string;
  privacy?: ProfilePrivacy;
  category?: string;
  contactEmail?: string;
  contactPhone?: string;
  businessAddress?: string;
  businessHours?: string;
  externalLinks?: string;
}

// ── Post ──────────────────────────────────────────────────────
export interface PostRequest {
  content: string;
  scheduledTime?: string | null;
  postType?: PostType | null;
  pinned?: boolean;
  ctaLabel?: string | null;
  ctaUrl?: string | null;
  originalPostId?: number | null;
  productIds?: number[];
}

export interface PostResponse {
  id: number;
  content: string;
  username: string;
  createdAt: string;
  scheduledTime?: string;
  postType?: PostType;
  pinned?: boolean;
  ctaLabel?: string;
  ctaUrl?: string;
  originalPostId?: number;
  originalUsername?: string;
  // Analytics counts come as totalLikes/totalComments/totalShares from backend
  totalLikes?: number;
  totalComments?: number;
  totalShares?: number;
  // Aliased for template convenience
  likesCount: number;
  commentsCount: number;
  sharesCount: number;
  hashtags?: string[];
  productIds?: number[];
  productNames?: string[];
  // Enriched by frontend
  user?: UserResponse;
  isLiked?: boolean;
}

// ── Comment ───────────────────────────────────────────────────
export interface CommentRequest {
  content: string;
}

export interface CommentResponse {
  id: number;
  content: string;
  username: string;
  createdAt: string;
}

// ── Connection ────────────────────────────────────────────────
// Backend ConnectionResponseDTO: id, senderUsername, receiverUsername, status
export interface ConnectionResponse {
  id: number;
  senderUsername: string;
  receiverUsername: string;
  status: ConnectionStatus;
  // Extended — populated by frontend if needed
  senderId?: number;
  receiverId?: number;
  sender?: UserResponse;
  receiver?: UserResponse;
}

// ── Notification ──────────────────────────────────────────────
// Backend: id, type, triggeredBy, postId, isRead
export interface NotificationResponse {
  id: number;
  type: string;
  triggeredBy: string;      // username who triggered
  actorUsername?: string;   // alias for triggeredBy
  postId?: number;
  isRead: boolean;
  message?: string;         // frontend-generated
  createdAt?: string;
}

// Backend NotificationPreferenceDTO: { type, enabled }
// Frontend uses a flat map for convenience
export interface NotificationPreference {
  likes: boolean;
  comments: boolean;
  connections: boolean;
  mentions: boolean;
  follows: boolean;
  reposts: boolean;
}

// ── Post Analytics ────────────────────────────────────────────
export interface PostAnalytics {
  postId: number;
  totalLikes: number;
  totalComments: number;
  totalShares: number;
  reach: number;
}

// ── User Insights ─────────────────────────────────────────────
// Backend UserInsightsDTO: userId, totalPosts, totalLikes, totalComments, totalShares, followerCount, followingCount
export interface UserInsights {
  userId: number;
  totalPosts: number;
  totalLikes: number;
  totalComments: number;
  totalShares: number;
  followerCount: number;
  followingCount: number;
  // Aliases for template convenience
  totalLikesReceived?: number;
  totalCommentsReceived?: number;
  totalSharesReceived?: number;
  totalFollowers?: number;
  totalReach?: number;
}

export interface FollowerDemographics {
  userId: number;
  totalFollowers: number;
  personalFollowers: number;
  creatorFollowers: number;
  businessFollowers: number;
  newFollowersLast30Days: number;
}

// ── Hashtag ───────────────────────────────────────────────────
export interface HashtagResponse {
  id: number;
  name: string;
  usageCount?: number;
}

// ── Product ───────────────────────────────────────────────────
export interface ProductRequest {
  name: string;
  description?: string;
  url?: string;
  price?: number;
}

export interface ProductResponse {
  id: number;
  ownerId: number;
  name: string;
  description?: string;
  url?: string;
  price?: number;
}
