import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AddInspectionPhotoRequest,
  Inspection,
  InspectionUpsertRequest
} from '../models/inspection.model';

@Injectable({
  providedIn: 'root'
})
export class InspectionService {
  private readonly http = inject(HttpClient);
  private readonly workOrdersBaseUrl = `${environment.backendApiUrl}/work-orders`;
  private readonly inspectionsBaseUrl = `${environment.backendApiUrl}/inspections`;

  getInspectionByWorkOrderId(workOrderId: number): Observable<Inspection> {
    return this.http.get<Inspection>(`${this.workOrdersBaseUrl}/${workOrderId}/inspection`);
  }

  createInspection(workOrderId: number, payload: InspectionUpsertRequest): Observable<Inspection> {
    return this.http.post<Inspection>(`${this.workOrdersBaseUrl}/${workOrderId}/inspection`, payload);
  }

  updateInspection(inspectionId: number, payload: InspectionUpsertRequest): Observable<Inspection> {
    return this.http.put<Inspection>(`${this.inspectionsBaseUrl}/${inspectionId}`, payload);
  }

  addPhoto(inspectionId: number, payload: AddInspectionPhotoRequest): Observable<Inspection> {
    return this.http.post<Inspection>(`${this.inspectionsBaseUrl}/${inspectionId}/photos`, payload);
  }
}
