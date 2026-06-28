import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MlModelInfo, MlPredictionRequest, MlPredictionResponse } from '../models/ml-prediction';

@Injectable({
  providedIn: 'root'
})
export class MlPredictionService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/ml';

  getModelInfo(): Observable<MlModelInfo> {
    return this.http.get<MlModelInfo>(`${this.apiUrl}/model-info`);
  }

  predict(request: MlPredictionRequest): Observable<MlPredictionResponse> {
    return this.http.post<MlPredictionResponse>(`${this.apiUrl}/predict`, request);
  }
}
