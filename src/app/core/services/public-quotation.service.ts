import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Quotation } from '../models/quotation.model';

@Injectable({
  providedIn: 'root'
})
export class PublicQuotationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backendApiUrl}/public/quotations`;

  getQuotationByToken(token: string): Observable<Quotation> {
    return this.http.get<Quotation>(`${this.baseUrl}/${token}`);
  }

  approveQuotation(token: string): Observable<Quotation> {
    return this.http.post<Quotation>(`${this.baseUrl}/${token}/approve`, {});
  }

  rejectQuotation(token: string): Observable<Quotation> {
    return this.http.post<Quotation>(`${this.baseUrl}/${token}/reject`, {});
  }
}
