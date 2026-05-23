import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AssignMechanicRequest,
  UpdateDiagnosisRequest,
  UpdateInternalNotesRequest,
  UpdateQualityControlRequest,
  UpdateWorkOrderStatusRequest,
  WorkOrder,
  WorkOrderCreateRequest,
  WorkOrderFilters
} from '../models/work-order.model';

@Injectable({
  providedIn: 'root'
})
export class WorkOrderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backendApiUrl}/work-orders`;

  listWorkOrders(filters?: WorkOrderFilters): Observable<WorkOrder[]> {
    let params = new HttpParams();

    if (filters?.status) {
      params = params.set('status', filters.status);
    }

    if (filters?.plate?.trim()) {
      params = params.set('plate', filters.plate.trim());
    }

    if (filters?.customerId) {
      params = params.set('customerId', filters.customerId);
    }

    if (filters?.receptionDateFrom) {
      params = params.set('receptionDateFrom', filters.receptionDateFrom);
    }

    if (filters?.receptionDateTo) {
      params = params.set('receptionDateTo', filters.receptionDateTo);
    }

    return this.http.get<WorkOrder[]>(this.baseUrl, { params });
  }

  getWorkOrderById(id: number): Observable<WorkOrder> {
    return this.http.get<WorkOrder>(`${this.baseUrl}/${id}`);
  }

  createWorkOrder(payload: WorkOrderCreateRequest): Observable<WorkOrder> {
    return this.http.post<WorkOrder>(this.baseUrl, payload);
  }

  assignMechanic(id: number, payload: AssignMechanicRequest): Observable<WorkOrder> {
    return this.http.patch<WorkOrder>(`${this.baseUrl}/${id}/assign-mechanic`, payload);
  }

  updateStatus(id: number, payload: UpdateWorkOrderStatusRequest): Observable<WorkOrder> {
    return this.http.patch<WorkOrder>(`${this.baseUrl}/${id}/status`, payload);
  }

  updateDiagnosis(id: number, payload: UpdateDiagnosisRequest): Observable<WorkOrder> {
    return this.http.patch<WorkOrder>(`${this.baseUrl}/${id}/diagnosis`, payload);
  }

  updateInternalNotes(id: number, payload: UpdateInternalNotesRequest): Observable<WorkOrder> {
    return this.http.patch<WorkOrder>(`${this.baseUrl}/${id}/internal-notes`, payload);
  }

  updateQualityControl(id: number, payload: UpdateQualityControlRequest): Observable<WorkOrder> {
    return this.http.patch<WorkOrder>(`${this.baseUrl}/${id}/quality-control`, payload);
  }

  markReady(id: number): Observable<WorkOrder> {
    return this.http.patch<WorkOrder>(`${this.baseUrl}/${id}/mark-ready`, {});
  }
}
