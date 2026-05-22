export interface Customer {
  id: number;
  fullName: string;
  identification?: string;
  phone: string;
  email?: string;
  address?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CustomerUpsertRequest {
  fullName: string;
  identification?: string;
  phone: string;
  email?: string;
  address?: string;
}
