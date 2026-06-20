import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PredictionRequest, PredictionResponse } from '../models/prediction';

@Injectable({
  providedIn: 'root'
})
export class PredictionService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/predictions';
  private exportUrl = 'http://localhost:8080/export';

  predict(request: PredictionRequest): Observable<PredictionResponse> {
    return this.http.post<PredictionResponse>(`${this.apiUrl}/form-rating`, request);
  }

  savePrediction(playerId: number, request: PredictionRequest, response: PredictionResponse): Observable<any> {
    return this.http.post(`${this.apiUrl}/save`, { playerId, predictionRequest: request });
  }

  getHistory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/history`);
  }

  getHistoryPaged(page: number, size: number): Observable<any> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<any>(`${this.apiUrl}/history/paged`, { params });
  }

  getPlayerHistory(playerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/history/${playerId}`);
  }

  exportCsv(): void {
    this.http.get(`${this.exportUrl}/predictions/csv`, { responseType: 'text' }).subscribe({
      next: (csvData) => {
        const blob = new Blob([csvData], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'prediction-history.csv';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Failed to export CSV', err)
    });
  }
}
