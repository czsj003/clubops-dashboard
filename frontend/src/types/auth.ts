export type Country = "ENGLAND" | "SPAIN" | "ITALY" | "GERMANY" | "FRANCE";

export interface User {
  id: number;
  name: string;
  email: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  clubName: string;
  country: Country;
}

export interface LoginRequest {
  email: string;
  password: string;
}