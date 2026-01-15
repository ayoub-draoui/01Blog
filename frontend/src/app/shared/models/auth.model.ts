export interface LoginRequest {
    usernameOrEmail: String;
    password: String;
}
export interface RegisterRequest {
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  userId: number;
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  avatar: string;
  role: string;
  message: string;
}

export interface DecodedToken {
  sub: string;         
  role: string;       
  iat: number;         
  exp: number;         
}
