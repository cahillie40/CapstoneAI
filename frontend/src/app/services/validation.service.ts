import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ValidationService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/dashboard';

  getSummary(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/validation`);
  }

  getModelExplanation(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/model-explanation`);
  }
}
