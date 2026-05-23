export interface RegisterUsedPartRequest {
  inventoryItemId: number;
  quantity: number;
}

export interface WorkOrderPart {
  id: number;
  workOrderId: number;
  inventoryItemId: number;
  inventoryItemName: string;
  inventoryItemSku: string;
  quantity: number;
  unitCost: number;
  salePrice: number;
  total: number;
  margin?: number;
  createdAt?: string;
}
