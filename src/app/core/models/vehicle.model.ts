export interface Vehicle {
  id: number;
  customerId: number;
  customerFullName: string;
  plate: string;
  brand: string;
  model: string;
  year?: number;
  color?: string;
  vin?: string;
  mileage: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface VehicleUpsertRequest {
  customerId: number;
  plate: string;
  brand: string;
  model: string;
  year?: number;
  color?: string;
  vin?: string;
  mileage: number;
}
