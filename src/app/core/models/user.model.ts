export type UserRole = 'ADMIN' | 'RECEPTIONIST' | 'MECHANIC';

export interface User {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
}
