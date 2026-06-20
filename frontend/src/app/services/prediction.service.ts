import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PredictionRequest, PredictionResponse } from '../models/prediction';

@Injectable({
  providedIn: 'root'
})
export class PredictionService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/predictions';

  predict(request: PredictionRequest): Observable<PredictionResponse> {
    return this.http.post<PredictionResponse>(`${this.apiUrl}/form-rating`, request);
  }

  savePrediction(playerId: number, request: PredictionRequest, response: PredictionResponse): Observable<any> {
    const saveRequest = {
      playerId: playerId,
      predictionRequest: request
    };
    return this.http.post(`${this.apiUrl}/save`, saveRequest);
  }

  getHistory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/history`);
  }

  getPlayerHistory(playerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/history/${playerId}`);
  }
}
