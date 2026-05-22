import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Vehicle, VehicleUpsertRequest } from '../models/vehicle.model';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backendApiUrl}/vehicles`;

  listVehicles(plate?: string): Observable<Vehicle[]> {
    let params = new HttpParams();

    if (plate?.trim()) {
      params = params.set('plate', plate.trim());
    }

    return this.http.get<Vehicle[]>(this.baseUrl, { params });
  }

  getVehicleById(id: number): Observable<Vehicle> {
    return this.http.get<Vehicle>(`${this.baseUrl}/${id}`);
  }

  createVehicle(payload: VehicleUpsertRequest): Observable<Vehicle> {
    return this.http.post<Vehicle>(this.baseUrl, payload);
  }

  updateVehicle(id: number, payload: VehicleUpsertRequest): Observable<Vehicle> {
    return this.http.put<Vehicle>(`${this.baseUrl}/${id}`, payload);
  }
}
