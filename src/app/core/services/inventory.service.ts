import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreateInventoryItemRequest,
  InventoryItem,
  UpdateInventoryItemRequest
} from '../models/inventory.model';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backendApiUrl}/inventory`;

  listInventory(search?: string): Observable<InventoryItem[]> {
    let params = new HttpParams();

    if (search?.trim()) {
      params = params.set('search', search.trim());
    }

    return this.http.get<InventoryItem[]>(this.baseUrl, { params });
  }

  listLowStockItems(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.baseUrl}/low-stock`);
  }

  getInventoryItemById(id: number): Observable<InventoryItem> {
    return this.http.get<InventoryItem>(`${this.baseUrl}/${id}`);
  }

  createInventoryItem(payload: CreateInventoryItemRequest): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(this.baseUrl, payload);
  }

  updateInventoryItem(id: number, payload: UpdateInventoryItemRequest): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.baseUrl}/${id}`, payload);
  }

  deactivateInventoryItem(id: number): Observable<InventoryItem> {
    return this.http.patch<InventoryItem>(`${this.baseUrl}/${id}/deactivate`, {});
  }
}
