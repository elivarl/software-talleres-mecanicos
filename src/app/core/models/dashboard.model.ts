import { WorkOrderStatus } from './work-order.model';

export interface DashboardLowStockItem {
  id: number;
  name: string;
  sku: string;
  currentStock: number;
  minStock: number;
  active: boolean;
}

export interface DashboardData {
  totalWorkOrdersThisMonth: number;
  workOrdersByStatus: Partial<Record<WorkOrderStatus, number>>;
  estimatedRevenueThisMonth: number;
  pendingWorkOrders: number;
  readyToDeliverWorkOrders: number;
  lowStockItems: DashboardLowStockItem[];
  deliveredWorkOrdersThisMonth: number;
}
