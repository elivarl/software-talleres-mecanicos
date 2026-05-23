export interface InventoryItem {
  id: number;
  name: string;
  sku: string;
  description?: string;
  currentStock: number;
  minStock: number;
  unitCost: number;
  salePrice: number;
  active: boolean;
  lowStock?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateInventoryItemRequest {
  name: string;
  sku: string;
  description?: string;
  currentStock: number;
  minStock: number;
  unitCost: number;
  salePrice: number;
  active?: boolean;
}

export interface UpdateInventoryItemRequest {
  name: string;
  sku: string;
  description?: string;
  currentStock: number;
  minStock: number;
  unitCost: number;
  salePrice: number;
  active: boolean;
}

export function isInventoryLowStock(item: InventoryItem): boolean {
  if (typeof item.lowStock === 'boolean') {
    return item.lowStock;
  }

  return item.currentStock <= item.minStock;
}

export function getInventoryStockSeverity(
  item: InventoryItem
): 'success' | 'warn' | 'danger' | 'secondary' {
  if (!item.active) {
    return 'secondary';
  }

  if (item.currentStock <= 0) {
    return 'danger';
  }

  if (isInventoryLowStock(item)) {
    return 'warn';
  }

  return 'success';
}
