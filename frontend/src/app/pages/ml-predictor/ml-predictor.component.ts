import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlayerService } from '../../services/player.service';
import { MlPredictionService } from '../../services/ml-prediction.service';
import { MlModelInfo, MlPredictionRequest, MlPredictionResponse } from '../../models/ml-prediction';

@Component({
  selector: 'app-ml-predictor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ml-predictor.component.html',
  styleUrl: './ml-predictor.component.css'
})
export class MlPredictorComponent implements OnInit {
  private playerService = inject(PlayerService);
  private mlPredictionService = inject(MlPredictionService);
  private cdr = inject(ChangeDetectorRef);

  players: any[] = [];
  selectedPlayerId: number | null = null;

  loadingPlayers = false;
  loadingModelInfo = false;
  predicting = false;
  error: string | null = null;

  modelInfo: MlModelInfo | null = null;
  predictionResult: MlPredictionResponse | null = null;

  request: MlPredictionRequest = {
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
  }

  loadPlayers(): void {
    this.loadingPlayers = true;
    this.playerService.getPlayers().subscribe({
      next: (data) => {
        this.players = data || [];
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load players', err);
        this.error = 'Failed to load players';
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadModelInfo(): void {
    this.loadingModelInfo = true;
    this.mlPredictionService.getModelInfo().subscribe({
      next: (data) => {
        this.modelInfo = data;
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load model info', err);
        this.error = 'Failed to load ML model info';
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      }
    });
  }

  onPlayerChange(): void {
    const player = this.players.find(p => p.id === Number(this.selectedPlayerId));
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

    this.mlPredictionService.predict(this.request).subscribe({
      next: (result) => {
        this.predictionResult = result;
        this.predicting = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('ML prediction failed', err);
        this.error = 'ML prediction failed';
        this.predicting = false;
        this.cdr.markForCheck();
      }
    });
  }

  getRiskClass(riskLevel: string): string {
    switch (riskLevel) {
      case 'LOW': return 'risk-low';
      case 'MEDIUM': return 'risk-medium';
      case 'HIGH': return 'risk-high';
      default: return '';
    }
  }
}
