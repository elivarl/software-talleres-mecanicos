export type QuotationStatus = 'DRAFT' | 'SENT' | 'APPROVED' | 'REJECTED' | 'EXPIRED';

export type QuotationItemType = 'PART' | 'LABOR';

export interface QuotationItemRequest {
  type: QuotationItemType;
  description: string;
  quantity: number;
  unitPrice: number;
}

export interface QuotationItem {
  id: number;
  type: QuotationItemType;
  description: string;
  quantity: number;
  unitPrice: number;
  total: number;
}

export interface UpdateQuotationRequest {
  items: QuotationItemRequest[];
}

export interface Quotation {
  id: number;
  code: string;
  workOrderId: number;
  workOrderCode: string;
  vehiclePlate: string;
  customerFullName: string;
  status: QuotationStatus;
  subtotal: number;
  tax: number;
  total: number;
  publicToken?: string;
  sentAt?: string;
  customerDecisionAt?: string;
  createdAt?: string;
  updatedAt?: string;
  items: QuotationItem[];
}

export const QUOTATION_ITEM_TYPE_OPTIONS: Array<{ label: string; value: QuotationItemType }> = [
  { label: 'Repuesto', value: 'PART' },
  { label: 'Mano de obra', value: 'LABOR' }
];

export function getQuotationStatusLabel(status: QuotationStatus): string {
  switch (status) {
    case 'DRAFT':
      return 'Borrador';
    case 'SENT':
      return 'Enviada';
    case 'APPROVED':
      return 'Aprobada';
    case 'REJECTED':
      return 'Rechazada';
    case 'EXPIRED':
      return 'Expirada';
    default:
      return status;
  }
}

export function getQuotationStatusSeverity(
  status: QuotationStatus
): 'success' | 'secondary' | 'info' | 'warn' | 'danger' | 'contrast' {
  switch (status) {
    case 'DRAFT':
      return 'secondary';
    case 'SENT':
      return 'info';
    case 'APPROVED':
      return 'success';
    case 'REJECTED':
      return 'danger';
    case 'EXPIRED':
      return 'warn';
    default:
      return 'secondary';
  }
}

export function getQuotationItemTypeLabel(type: QuotationItemType): string {
  return QUOTATION_ITEM_TYPE_OPTIONS.find((option) => option.value === type)?.label ?? type;
}
