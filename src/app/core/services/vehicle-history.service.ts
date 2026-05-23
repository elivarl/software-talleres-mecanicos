import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { VehicleHistoryResponse } from '../models/vehicle-history.model';

@Injectable({
  providedIn: 'root'
})
export class VehicleHistoryService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backendApiUrl}/vehicles`;

  getVehicleHistoryById(vehicleId: number): Observable<VehicleHistoryResponse> {
    return this.http.get<VehicleHistoryResponse>(`${this.baseUrl}/${vehicleId}/history`);
  }

  getVehicleHistoryByPlate(plate: string): Observable<VehicleHistoryResponse> {
    return this.http.get<VehicleHistoryResponse>(
      `${this.baseUrl}/by-plate/${encodeURIComponent(plate.trim())}/history`
    );
  }
}
