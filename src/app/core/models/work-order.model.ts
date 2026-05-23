export type WorkOrderStatus =
  | 'RECEIVED'
  | 'DIAGNOSIS'
  | 'QUOTED'
  | 'APPROVED'
  | 'REJECTED'
  | 'IN_PROGRESS'
  | 'READY'
  | 'DELIVERED'
  | 'CANCELLED';

export interface WorkOrder {
  id: number;
  code: string;
  customerId: number;
  customerFullName: string;
  vehicleId: number;
  vehiclePlate: string;
  assignedMechanicId?: number;
  assignedMechanicFullName?: string;
  status: WorkOrderStatus;
  receptionDate?: string;
  estimatedDeliveryDate?: string;
  currentMileage: number;
  fuelLevel?: string;
  customerComplaint: string;
  initialObservations?: string;
  diagnosis?: string;
  internalNotes?: string;
  qualityControlCompleted?: boolean;
  qualityControlNotes?: string;
  readyAt?: string;
  deliveredAt?: string;
  deliveredTo?: string;
  finalMileage?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface WorkOrderFilters {
  status?: WorkOrderStatus;
  plate?: string;
  customerId?: number;
  receptionDateFrom?: string;
  receptionDateTo?: string;
}

export interface WorkOrderCreateRequest {
  customerId: number;
  vehicleId: number;
  assignedMechanicId?: number;
  receptionDate?: string;
  estimatedDeliveryDate?: string;
  currentMileage: number;
  fuelLevel?: string;
  customerComplaint: string;
  initialObservations?: string;
}

export interface AssignMechanicRequest {
  assignedMechanicId: number;
}

export interface UpdateWorkOrderStatusRequest {
  status: WorkOrderStatus;
}

export interface UpdateDiagnosisRequest {
  diagnosis: string;
}

export interface UpdateInternalNotesRequest {
  internalNotes: string;
}

export interface UpdateQualityControlRequest {
  completed: boolean;
  notes?: string;
}

export interface WorkOrderStatusOption {
  label: string;
  value: WorkOrderStatus;
}

export const WORK_ORDER_STATUS_OPTIONS: WorkOrderStatusOption[] = [
  { label: 'Recibida', value: 'RECEIVED' },
  { label: 'Diagnóstico', value: 'DIAGNOSIS' },
  { label: 'Cotizada', value: 'QUOTED' },
  { label: 'Aprobada', value: 'APPROVED' },
  { label: 'Rechazada', value: 'REJECTED' },
  { label: 'En progreso', value: 'IN_PROGRESS' },
  { label: 'Lista', value: 'READY' },
  { label: 'Entregada', value: 'DELIVERED' },
  { label: 'Cancelada', value: 'CANCELLED' }
];

export function getWorkOrderStatusLabel(status: WorkOrderStatus): string {
  return WORK_ORDER_STATUS_OPTIONS.find((option) => option.value === status)?.label ?? status;
}

export function getWorkOrderStatusSeverity(
  status: WorkOrderStatus
): 'success' | 'secondary' | 'info' | 'warn' | 'danger' | 'contrast' {
  switch (status) {
    case 'RECEIVED':
      return 'info';
    case 'DIAGNOSIS':
      return 'warn';
    case 'QUOTED':
      return 'secondary';
    case 'APPROVED':
      return 'success';
    case 'REJECTED':
    case 'CANCELLED':
      return 'danger';
    case 'IN_PROGRESS':
      return 'contrast';
    case 'READY':
      return 'success';
    case 'DELIVERED':
      return 'info';
    default:
      return 'secondary';
  }
}
