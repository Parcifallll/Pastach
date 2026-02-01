// User model
export interface User {
    id: string; // UUID string
    email: string;
    firstName: string;
    lastName: string;
    birthday: string; // ISO date string (LocalDate)
    locked: boolean;
    createdAt: string; // ISO datetime string (Instant)
    roles: Role[];
}

export interface Role {
    id: number;
    name: RoleEnum;
}

export type RoleEnum = 'USER' | 'ADMIN' | 'GUEST';

export interface Post {
    id: number;
    authorId: string;
    text: string;
    photoUrl: string | null;
    createdAt: string; // ISO datetime string (Instant)
    commentsCount: number;
    likesCount: number;
    dislikesCount: number;
}

// Comment model
export interface Comment {
    id: number;
    authorId: string;
    text: string;
    photoUrl: string | null;
    createdAt: string; // ISO datetime string (Instant)
    likesCount: number;
    dislikesCount: number;
    // post is @JsonIgnore
}

export interface Reaction {
    id: number;
    targetType: ReactionTargetType;
    targetId: number; // postId or commentId
    authorId: string;
    type: ReactionType;
    createdAt: string; // ISO datetime string (Instant)
}

export type ReactionTargetType = 'POST' | 'COMMENT';

export type ReactionType = 'LIKE' | 'DISLIKE';

// Auth related types
export interface LoginRequest {
    email: string; // username=email
    password: string;
}

export interface RegisterRequest {
    id: string; // UUID
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    birthday: string; // ISO date format: "YYYY-MM-DD"
}

export interface AuthResponse {
    token: string;
    user: User;
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
    text: string;
    photoUrl?: string | null;
}

export interface UpdatePostRequest {
    text: string;
    photoUrl?: string | null;
}

export interface CreateCommentRequest {
    text: string;
    photoUrl?: string | null;
}

export interface CreateReactionRequest {
    targetType: ReactionTargetType;
    targetId: number;
    type: ReactionType;
}

// Response DTOs with additional data (if needed)
export interface PostWithAuthor extends Post {
    author?: User;
}

export interface CommentWithAuthor extends Comment {
    author?: User;
}

// Constants for enum values (if you need them)
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