export type UserRole = 'ADMIN' | 'RECEPTIONIST' | 'MECHANIC';

export interface User {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
