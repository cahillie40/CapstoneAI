import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';

import { PlayerService } from '../../services/player.service';
import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import { Player } from '../../models/player';
import {
  MlModelInfoTribuo,
  MlPredictionTribuoHistoryResponse,
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
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  players: Player[] = [];
  history: MlPredictionTribuoHistoryResponse[] = [];
  selectedPlayerId: number | null = null;

  modelInfo: MlModelInfoTribuo | null = null;
  predictionResult: MlPredictionTribuoResponse | null = null;

  loading = false;
  loadingPlayers = false;
  loadingModelInfo = false;
  loadingHistory = false;
  error: string | null = null;
  successMessage: string | null = null;

  request: MlPredictionTribuoRequest = this.createEmptyRequest();

  ngOnInit(): void {
    this.loadPlayers();
    this.loadModelInfo();
    this.loadHistory();
  }

  loadPlayers(): void {
    this.loadingPlayers = true;
    this.error = null;

    this.playerService.getPlayers()
      .pipe(finalize(() => {
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: Player[]) => {
          this.players = data ?? [];
        },
        error: (err: unknown) => {
          console.error('Failed to load players', err);
          this.error = 'Failed to load players';
          this.players = [];
        }
      });
  }

  loadModelInfo(): void {
    this.loadingModelInfo = true;
    this.error = null;

    this.tribuoService.getModelInfo()
      .pipe(finalize(() => {
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlModelInfoTribuo) => {
          this.modelInfo = data;
        },
        error: (err: unknown) => {
          console.error('Failed to load Tribuo model info', err);
          this.error = 'Failed to load Tribuo model info';
        }
      });
  }

  loadHistory(): void {
    this.loadingHistory = true;
    this.error = null;

    this.tribuoService.getHistory()
      .pipe(finalize(() => {
        this.loadingHistory = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlPredictionTribuoHistoryResponse[]) => {
          this.history = data ?? [];
        },
        error: (err: unknown) => {
          console.error('Failed to load Tribuo history', err);
          this.error = 'Failed to load Tribuo history';
          this.history = [];
        }
      });
  }

  onPlayerChange(): void {
    const player = this.players.find(p => p.id === Number(this.selectedPlayerId));
    if (!player) {
      return;
    }

    this.request = {
      playerId: player.id ?? null,
      playerName: player.name ?? '',
      age: player.age ?? 0,
      position: player.position ?? '',
      matchesPlayed: player.matchesPlayed ?? 0,
      goals: player.goals ?? 0,
      assists: player.assists ?? 0,
      minutesPlayed: player.minutesPlayed ?? 0,
      yellowCards: player.yellowCards ?? 0,
      redCards: player.redCards ?? 0,
      shotsOnTarget: player.shotsOnTarget ?? 0,
      passAccuracy: player.passAccuracy ?? 0,
      injuryStatus: player.injuryStatus ?? false,
      expectedGoals: player.expectedGoals ?? 0,
      expectedAssists: player.expectedAssists ?? 0,
      keyPasses: player.keyPasses ?? 0,
      progressivePasses: player.progressivePasses ?? 0,
      dribblesCompleted: player.dribblesCompleted ?? 0,
      tacklesWon: player.tacklesWon ?? 0,
      interceptions: player.interceptions ?? 0,
      ballRecoveries: player.ballRecoveries ?? 0,
      matchesMissed: player.matchesMissed ?? 0,
      recentMatchLoad: player.recentMatchLoad ?? 0
    };

    this.predictionResult = null;
    this.successMessage = null;
    this.error = null;
  }

  predict(): void {
    this.loading = true;
    this.error = null;
    this.successMessage = null;
    this.predictionResult = null;

    this.tribuoService.predict(this.request)
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlPredictionTribuoResponse) => {
          this.predictionResult = data;
          this.successMessage = 'Tribuo prediction generated successfully.';
          this.loadHistory();
        },
        error: (err: unknown) => {
          console.error('Failed to generate Tribuo prediction', err);
          this.error = 'Failed to generate Tribuo prediction';
        }
      });
  }

  resetForm(): void {
    this.selectedPlayerId = null;
    this.request = this.createEmptyRequest();
    this.predictionResult = null;
    this.error = null;
    this.successMessage = null;
  }

  getRiskClass(riskLevel: string): string {
    switch (riskLevel) {
      case 'LOW':
        return 'risk-low';
      case 'MEDIUM':
        return 'risk-medium';
      case 'HIGH':
        return 'risk-high';
      default:
        return '';
    }
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
