export interface RegisterLaborItemRequest {
  description: string;
  price: number;
}

export interface LaborItem {
  id: number;
  workOrderId: number;
  description: string;
  price: number;
  createdAt?: string;
}
