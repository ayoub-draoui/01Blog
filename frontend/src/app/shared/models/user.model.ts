export interface User {
  id: number;
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  avatar: string;
  bio?: string;
  location?: string;
  website?: string;
  role: string;
  followersCount?: number;
  followingCount?: number;
  postsCount?: number;
  isFollowing?: boolean | null;
}


export interface UpdateProfileRequest {
  firstname?: string;
  lastname?: string;
  bio?: string;
  location?: string;
  website?: string;
}