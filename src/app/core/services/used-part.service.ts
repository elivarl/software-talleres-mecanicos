import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RegisterUsedPartRequest, WorkOrderPart } from '../models/used-part.model';

@Injectable({
  providedIn: 'root'
})
export class UsedPartService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backendApiUrl}/work-orders`;

  listUsedParts(workOrderId: number): Observable<WorkOrderPart[]> {
    return this.http.get<WorkOrderPart[]>(`${this.baseUrl}/${workOrderId}/parts`);
  }

  registerUsedPart(
    workOrderId: number,
    payload: RegisterUsedPartRequest
  ): Observable<WorkOrderPart> {
    return this.http.post<WorkOrderPart>(`${this.baseUrl}/${workOrderId}/parts`, payload);
  }

  deleteUsedPart(workOrderId: number, partId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${workOrderId}/parts/${partId}`);
  }
}
