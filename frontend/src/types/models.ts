// User model
export interface User {
    id: number;
    username: string;
    email?: string;
    firstName: string;
    lastName: string;
    birthday?: string; // ISO (LocalDate)
    locked: boolean;
    createdAt: string; // ISO datetime (Instant)
    roles: Role[];
}

export interface Role {
    id: number;
    name: RoleEnum;
}

export type RoleEnum = 'USER' | 'ADMIN' | 'GUEST';

export interface Post {
    id: number;
    authorId: number;
    text: string;
    photoUrl: string | null;
    createdAt: string;
    commentsCount: number;
    likesCount: number;
    dislikesCount: number;
}

export interface Comment {
    id: number;
    authorId: number;
    text: string;
    photoUrl: string | null;
    createdAt: string;
    likesCount: number;
    dislikesCount: number;
    parentCommentId?: number | null;
    deletedAt?: string | null;
}

export interface Reaction {
    id: number;
    targetType: ReactionTargetType;
    targetId: number; // postId or commentId
    authorId: number;
    type: ReactionType;
    createdAt: string;
}

export type ReactionTargetType = 'POST' | 'COMMENT';

export type ReactionType = 'LIKE' | 'DISLIKE';

export interface LoginRequest {
    username: string;
    password: string;
}

export interface SignupRequest {
    username: string;
    email?: string;
    password: string;
    firstName: string;
    lastName: string;
    birthday?: string; // ISO date format: "YYYY-MM-DD"
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string; // "Bearer"
}

export interface RefreshTokenRequest {
    refreshToken: string;
}

export interface SessionInfo {
    id: string;
    deviceInfo: string;
    ipAddress: string;
    createdAt: string;
    lastUsedAt: string;
}

// API response wrappers
export interface ApiResponse<T> {
    data: T;
    message?: string;
    success: boolean;
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number; // current page number
}

// Request DTOs for creating/updating
export interface CreatePostRequest {
    text?: string;
    photoUrl?: string | null;
}

export interface UpdatePostRequest {
    text?: string;
    photoUrl?: string | null;
}

export interface CreateCommentRequest {
    text?: string;
    photoUrl?: string | null;
    parentCommentId?: number | null;
}

export interface UpdateCommentRequest {
    text?: string;
    photoUrl?: string | null;
}

export interface CreateReactionRequest {
    type: ReactionType;
}

export interface UserUpdateRequest {
    username?: string;
    email?: string;
    firstName?: string;
    lastName?: string;
    birthday?: string;
    bio?: string;
    photoUrl?: string;
}

export interface PasswordChangeRequest {
    currentPassword: string;
    newPassword: string;
}

export interface PostWithAuthor extends Post {
    author?: User;
}

export interface CommentWithAuthor extends Comment {
    author?: User;
}

export interface ErrorResponse {
    type: string;
    message: string;
}

export interface ValidationError {
    field: string;
    message: string;
}

export interface ValidationResponse {
    type: string;
    details: ValidationError[];
}

export const RoleEnumValues = {
    USER: 'USER' as const,
    ADMIN: 'ADMIN' as const,
    GUEST: 'GUEST' as const,
};

export const ReactionTypeValues = {
    LIKE: 'LIKE' as const,
    DISLIKE: 'DISLIKE' as const,
};

export const ReactionTargetTypeValues = {
    POST: 'POST' as const,
    COMMENT: 'COMMENT' as const,
};