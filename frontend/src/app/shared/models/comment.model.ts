export interface Comment {
  id: number;
  content: string;
  userId: number;
  postId: number;
  createdAt: string;
  updatedAt: string;
}

export interface CommentWithUser {
  id: number;
  content: string;
  userId: number;
  username?: string;
  userFirstname?: string;
  userLastname?: string;
  userAvatar?: string;
  postId: number;
  createdAt: string;
  updatedAt: string;
}


export interface CreateCommentRequest {
  content: string;
}
