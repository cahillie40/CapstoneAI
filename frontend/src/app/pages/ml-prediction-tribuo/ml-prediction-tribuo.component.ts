import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';

import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import {
  MlModelInfoTribuo,
  MlPredictionTribuoRequest,
  MlPredictionTribuoResponse
} from '../../models/ml-prediction-tribuo';

@Component({
  selector: 'app-ml-prediction-tribuo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ml-prediction-tribuo.component.html',
  styleUrl: './ml-prediction-tribuo.component.css'
})
export class MlPredictionTribuoComponent implements OnInit {
  private tribuoService = inject(MlPredictionTribuoService);

  modelInfo: MlModelInfoTribuo | null = null;
  predictionResult: MlPredictionTribuoResponse | null = null;

  loading = false;
  error: string | null = null;
  successMessage: string | null = null;

  request: MlPredictionTribuoRequest = this.createEmptyRequest();

  ngOnInit(): void {
    this.loadModelInfo();
  }

  loadModelInfo(): void {
    this.error = null;

    this.tribuoService.getModelInfo().subscribe({
      next: (data: MlModelInfoTribuo) => {
        this.modelInfo = data;
      },
      error: (err: unknown) => {
        console.error('Failed to load Tribuo model info', err);
        this.error = 'Failed to load Tribuo model info';
      }
    });
  }

  predict(): void {
    this.loading = true;
    this.error = null;
    this.successMessage = null;

    this.tribuoService.predict(this.request)
      .pipe(finalize(() => {
        this.loading = false;
      }))
      .subscribe({
        next: (data: MlPredictionTribuoResponse) => {
          this.predictionResult = data;
          this.successMessage = 'Tribuo prediction generated successfully.';
        },
        error: (err: unknown) => {
          console.error('Failed to generate Tribuo prediction', err);
          this.error = 'Failed to generate Tribuo prediction';
          this.predictionResult = null;
        }
      });
  }

  resetForm(): void {
    this.request = this.createEmptyRequest();
    this.predictionResult = null;
    this.error = null;
    this.successMessage = null;
  }

  private createEmptyRequest(): MlPredictionTribuoRequest {
    return {
      playerId: null,
      playerName: '',
      age: null,
      position: '',
      matchesPlayed: null,
      goals: null,
      assists: null,
      minutesPlayed: null,
      yellowCards: null,
      redCards: null,
      shotsOnTarget: null,
      passAccuracy: null,
      injuryStatus: false,
      expectedGoals: null,
      expectedAssists: null,
      keyPasses: null,
      progressivePasses: null,
      dribblesCompleted: null,
      tacklesWon: null,
      interceptions: null,
      ballRecoveries: null,
      matchesMissed: null,
      recentMatchLoad: null
    };
  }
}
