export interface Post {
  id: number;
  title: string;
  content: string;
  authorId: number;
  authorUsername?: string;
  authorFirstname?: string;
  authorLastname?: string;
  authorAvatar?: string;
  mediaUrl?: string;
  mediaType?: 'IMAGE' | 'VIDEO' | null;
  likesCount?: number;
  commentsCount?: number;
  isLikedByCurrentUser?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePostRequest {
  title: string;
  content: string;
  media?: File;
}

export interface Page<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
}