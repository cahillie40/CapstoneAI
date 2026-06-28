import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlayerService } from '../../services/player.service';
import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
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
  private playerService = inject(PlayerService);
  private mlPredictionTribuoService = inject(MlPredictionTribuoService);
  private cdr = inject(ChangeDetectorRef);

  players: any[] = [];
  history: MlPredictionTribuoHistoryResponse[] = [];
  selectedPlayerId: number | null = null;

  loadingPlayers = false;
  loadingModelInfo = false;
  loadingHistory = false;
  predicting = false;
  error: string | null = null;

  modelInfo: MlModelInfoTribuo | null = null;
  predictionResult: MlPredictionTribuoResponse | null = null;

  request: MlPredictionTribuoRequest = {
    playerId: null,
    playerName: '',
    age: 0,
    position: '',
    matchesPlayed: 0,
    goals: 0,
    assists: 0,
    minutesPlayed: 0,
    yellowCards: 0,
    redCards: 0,
    shotsOnTarget: 0,
    passAccuracy: 0,
    injuryStatus: false,
    expectedGoals: 0,
    expectedAssists: 0,
    keyPasses: 0,
    progressivePasses: 0,
    dribblesCompleted: 0,
    tacklesWon: 0,
    interceptions: 0,
    ballRecoveries: 0,
    matchesMissed: 0,
    recentMatchLoad: 0
  };

  ngOnInit(): void {
    this.loadPlayers();
    this.loadModelInfo();
    this.loadHistory();
  }

  loadPlayers(): void {
    this.loadingPlayers = true;
    this.playerService.getPlayers().subscribe({
      next: (data: any[]) => {
        this.players = data || [];
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load players', err);
        this.error = 'Failed to load players';
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadModelInfo(): void {
    this.loadingModelInfo = true;
    this.mlPredictionTribuoService.getModelInfo().subscribe({
      next: (data: MlModelInfoTribuo) => {
        this.modelInfo = data;
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load Tribuo model info', err);
        this.error = 'Failed to load Tribuo model info';
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadHistory(): void {
    this.loadingHistory = true;
    this.mlPredictionTribuoService.getHistory().subscribe({
      next: (data: MlPredictionTribuoHistoryResponse[]) => {
        this.history = data || [];
        this.loadingHistory = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load Tribuo history', err);
        this.loadingHistory = false;
        this.cdr.markForCheck();
      }
    });
  }

  onPlayerChange(): void {
    const player = this.players.find((p: any) => p.id === Number(this.selectedPlayerId));
    if (!player) return;

    this.request = {
      playerId: player.id,
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
  }

  runPrediction(): void {
    this.predicting = true;
    this.error = null;
    this.predictionResult = null;

    this.mlPredictionTribuoService.predict(this.request).subscribe({
      next: (result: MlPredictionTribuoResponse) => {
        this.predictionResult = result;
        this.predicting = false;
        this.loadHistory();
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Tribuo prediction failed', err);
        this.error = 'Tribuo prediction failed';
        this.predicting = false;
        this.cdr.markForCheck();
      }
    });
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
}
