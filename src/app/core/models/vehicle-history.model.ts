import { Customer } from './customer.model';
import { Vehicle } from './vehicle.model';
import { WorkOrderStatus } from './work-order.model';

export interface VehicleHistoryResponse {
  vehicle: Vehicle;
  customer: Customer;
  workOrders: VehicleHistoryWorkOrder[];
}

export interface VehicleHistoryWorkOrder {
  workOrderId: number;
  workOrderCode: string;
  status: WorkOrderStatus;
  receptionDate?: string;
  estimatedDeliveryDate?: string;
  readyAt?: string;
  deliveredAt?: string;
  deliveredTo?: string;
  currentMileage: number;
  finalMileage?: number;
  fuelLevel?: string;
  customerComplaint: string;
  initialObservations?: string;
  diagnosis?: string;
  qualityControlCompleted?: boolean;
  qualityControlNotes?: string;
  inspection?: VehicleHistoryInspection | null;
  quotation?: VehicleHistoryQuotation | null;
  usedParts: VehicleHistoryPart[];
  laborItems: VehicleHistoryLaborItem[];
  totals?: VehicleHistoryOrderTotals | null;
}

export interface VehicleHistoryInspection {
  id: number;
  mileage: number;
  fuelLevel?: string;
  exteriorCondition?: string;
  visibleScratches?: string;
  visibleDents?: string;
  lightsWorking?: boolean;
  tiresCondition?: string;
  mirrorsCondition?: string;
  hasSpareTire?: boolean;
  hasJack?: boolean;
  hasTools?: boolean;
  hasDocuments?: boolean;
  personalItemsNotes?: string;
  generalNotes?: string;
  createdAt?: string;
  updatedAt?: string;
  photos: VehicleHistoryInspectionPhoto[];
}

export interface VehicleHistoryInspectionPhoto {
  id: number;
  photoUrl: string;
  description?: string;
  createdAt?: string;
}

export interface VehicleHistoryQuotation {
  id: number;
  code: string;
  status: string;
  subtotal: number;
  tax: number;
  total: number;
  sentAt?: string;
  customerDecisionAt?: string;
  items: VehicleHistoryQuotationItem[];
}

export interface VehicleHistoryQuotationItem {
  id: number;
  type: string;
  description: string;
  quantity: number;
  unitPrice: number;
  total: number;
}

export interface VehicleHistoryPart {
  id: number;
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

export interface VehicleHistoryLaborItem {
  id: number;
  description: string;
  price: number;
  createdAt?: string;
}

export interface VehicleHistoryOrderTotals {
  partsTotal?: number;
  laborTotal?: number;
  serviceTotal?: number;
  quotationTotal?: number;
}
