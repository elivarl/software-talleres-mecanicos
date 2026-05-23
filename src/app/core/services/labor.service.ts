import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LaborItem, RegisterLaborItemRequest } from '../models/labor.model';

@Injectable({
  providedIn: 'root'
})
export class LaborService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backendApiUrl}/work-orders`;

  listLaborItems(workOrderId: number): Observable<LaborItem[]> {
    return this.http.get<LaborItem[]>(`${this.baseUrl}/${workOrderId}/labor`);
  }

  registerLaborItem(
    workOrderId: number,
    payload: RegisterLaborItemRequest
  ): Observable<LaborItem> {
    return this.http.post<LaborItem>(`${this.baseUrl}/${workOrderId}/labor`, payload);
  }

  deleteLaborItem(workOrderId: number, laborId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${workOrderId}/labor/${laborId}`);
  }
}
