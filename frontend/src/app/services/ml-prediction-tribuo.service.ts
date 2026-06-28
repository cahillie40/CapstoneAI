import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  MlModelInfoTribuo,
  MlPredictionTribuoHistoryResponse,
  MlPredictionTribuoRequest,
  MlPredictionTribuoResponse
} from '../models/ml-prediction-tribuo';

@Injectable({
  providedIn: 'root'
})
export class MlPredictionTribuoService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/ml/tribuo';

  getModelInfo(): Observable<MlModelInfoTribuo> {
    return this.http.get<MlModelInfoTribuo>(`${this.apiUrl}/model-info`);
  }

  predict(request: MlPredictionTribuoRequest): Observable<MlPredictionTribuoResponse> {
    return this.http.post<MlPredictionTribuoResponse>(`${this.apiUrl}/predict`, request);
  }

  getHistory(): Observable<MlPredictionTribuoHistoryResponse[]> {
    return this.http.get<MlPredictionTribuoHistoryResponse[]>(`${this.apiUrl}/history`);
  }
}
