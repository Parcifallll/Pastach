// User model
export interface User {
    id: number; // Changed from string to number
    username: string; // Main field for login
    email?: string; // Optional field
    firstName: string;
    lastName: string;
    birthday?: string; // ISO date string (LocalDate)
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
    authorId: number;
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
    authorId: number;
    text: string;
    photoUrl: string | null;
    createdAt: string; // ISO datetime string (Instant)
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
    createdAt: string; // ISO datetime string (Instant)
}

export type ReactionTargetType = 'POST' | 'COMMENT';

export type ReactionType = 'LIKE' | 'DISLIKE';

// Auth related types - matching backend JwtResponse
export interface LoginRequest {
    username: string; // Login using username (not email!)
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

// Backend returns JwtResponse with these fields
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

// HATEOAS PagedModel response (Spring HATEOAS format)
export interface PagedModel<T> {
    _embedded?: {
        postResponseDTOList?: T[];
        userResponseDTOList?: T[];
        commentResponseDTOList?: T[];
    };
    page?: {
        size: number;
        totalElements: number;
        totalPages: number;
        number: number; // current page number (0-based)
    };
    _links?: {
        self?: { href: string };
        next?: { href: string };
        prev?: { href: string };
    };
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

// Response DTOs with additional data (if needed)
export interface PostWithAuthor extends Post {
    author?: User;
}

export interface CommentWithAuthor extends Comment {
    author?: User;
}

// Error response from backend
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

// Constants for enum values
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