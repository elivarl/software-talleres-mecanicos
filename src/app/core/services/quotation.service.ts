import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Quotation,
  QuotationItemRequest,
  UpdateQuotationRequest
} from '../models/quotation.model';

@Injectable({
  providedIn: 'root'
})
export class QuotationService {
  private readonly http = inject(HttpClient);
  private readonly workOrdersBaseUrl = `${environment.backendApiUrl}/work-orders`;
  private readonly quotationsBaseUrl = `${environment.backendApiUrl}/quotations`;

  createQuotation(workOrderId: number): Observable<Quotation> {
    return this.http.post<Quotation>(`${this.workOrdersBaseUrl}/${workOrderId}/quotation`, {});
  }

  getQuotationById(id: number): Observable<Quotation> {
    return this.http.get<Quotation>(`${this.quotationsBaseUrl}/${id}`);
  }

  updateQuotation(id: number, payload: UpdateQuotationRequest): Observable<Quotation> {
    return this.http.put<Quotation>(`${this.quotationsBaseUrl}/${id}`, payload);
  }

  addQuotationItem(id: number, payload: QuotationItemRequest): Observable<Quotation> {
    return this.http.post<Quotation>(`${this.quotationsBaseUrl}/${id}/items`, payload);
  }

  updateQuotationItem(
    id: number,
    itemId: number,
    payload: QuotationItemRequest
  ): Observable<Quotation> {
    return this.http.put<Quotation>(`${this.quotationsBaseUrl}/${id}/items/${itemId}`, payload);
  }

  deleteQuotationItem(id: number, itemId: number): Observable<void> {
    return this.http.delete<void>(`${this.quotationsBaseUrl}/${id}/items/${itemId}`);
  }

  sendQuotation(id: number): Observable<Quotation> {
    return this.http.post<Quotation>(`${this.quotationsBaseUrl}/${id}/send`, {});
  }
}
