import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  MlModelInfoTribuo,
  MlPredictionTribuoHistoryResponse,
  MlPredictionTribuoRequest,
  MlPredictionTribuoResponse,
  MlTribuoEvaluationResponse,
  MlTribuoTrainingInfoResponse
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

  getTrainingModelInfo(): Observable<MlModelInfoTribuo> {
    return this.http.get<MlModelInfoTribuo>(`${this.apiUrl}/training-model-info`);
  }

  getTrainingInfo(): Observable<MlTribuoTrainingInfoResponse> {
    return this.http.get<MlTribuoTrainingInfoResponse>(`${this.apiUrl}/training-info`);
  }

  trainModel(): Observable<MlTribuoTrainingInfoResponse> {
    return this.http.post<MlTribuoTrainingInfoResponse>(`${this.apiUrl}/train`, {});
  }

  getEvaluation(): Observable<MlTribuoEvaluationResponse> {
    return this.http.get<MlTribuoEvaluationResponse>(`${this.apiUrl}/evaluation`);
  }

  evaluateModel(): Observable<MlTribuoEvaluationResponse> {
    return this.http.post<MlTribuoEvaluationResponse>(`${this.apiUrl}/evaluate`, {});
  }

  predict(request: MlPredictionTribuoRequest): Observable<MlPredictionTribuoResponse> {
    return this.http.post<MlPredictionTribuoResponse>(`${this.apiUrl}/predict`, request);
  }

  getHistory(): Observable<MlPredictionTribuoHistoryResponse[]> {
    return this.http.get<MlPredictionTribuoHistoryResponse[]>(`${this.apiUrl}/history`);
  }
}
