import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PredictionRequest, PredictionResponse } from '../../models/prediction';
import { PredictionService } from '../../services/prediction.service';
import { PlayerService } from '../../services/player';
import { Player } from '../../models/player';

@Component({
  selector: 'app-prediction-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './prediction-form.component.html',
  styleUrl: './prediction-form.component.css'
})
export class PredictionFormComponent implements OnInit {
  private predictionService = inject(PredictionService);
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  request: PredictionRequest = {
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
    injuryStatus: false
  };

  result: PredictionResponse | null = null;
  loading = false;
  error: string | null = null;
  players: Player[] = [];
  selectedPlayerId: number | null = null;
  saveMessage: string | null = null;

  ngOnInit(): void {
    this.loadPlayers();
  }

  loadPlayers(): void {
    this.playerService.getPlayers().subscribe({
      next: (response: any) => {
        this.players = Array.isArray(response) ? response : [];
        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Failed to load players:', error);
      }
    });
  }

  fillFromPlayer(): void {
    if (!this.selectedPlayerId) {
      this.error = 'Please select a player';
      return;
    }

    const player = this.players.find(p => p.id === this.selectedPlayerId);
    if (player) {
      this.request = {
        age: player.age || 0,
        position: player.position || '',
        matchesPlayed: player.matchesPlayed || 0,
        goals: player.goals || 0,
        assists: player.assists || 0,
        minutesPlayed: player.minutesPlayed || 0,
        yellowCards: player.yellowCards || 0,
        redCards: player.redCards || 0,
        shotsOnTarget: player.shotsOnTarget || 0,
        passAccuracy: player.passAccuracy || 0,
        injuryStatus: player.injuryStatus || false
      };
      this.error = null;
      this.cdr.markForCheck();
    }
  }

  onSubmit(): void {
    this.loading = true;
    this.error = null;
    this.result = null;
    this.saveMessage = null;

    this.predictionService.predict(this.request).subscribe({
      next: (response) => {
        this.result = response;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Prediction failed', err);
        this.error = 'Prediction failed. Please try again.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  savePrediction(): void {
    if (!this.selectedPlayerId || !this.result) {
      this.error = 'Please select a player and generate a prediction first';
      return;
    }

    this.predictionService.savePrediction(this.selectedPlayerId, this.request, this.result).subscribe({
      next: () => {
        this.saveMessage = 'Prediction saved successfully!';
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to save prediction', err);
        this.error = 'Failed to save prediction';
        this.cdr.markForCheck();
      }
    });
  }

  getRiskClass(): string {
    if (!this.result) return '';
    switch (this.result.riskLevel) {
      case 'LOW':    return 'risk-low';
      case 'MEDIUM': return 'risk-medium';
      case 'HIGH':   return 'risk-high';
      default:       return '';
    }
  }
}
