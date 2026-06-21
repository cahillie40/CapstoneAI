import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PlayerService } from '../../services/player.service';
import { PredictionService } from '../../services/prediction.service';
import { Player } from '../../models/player';

@Component({
  selector: 'app-player-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './player-detail.component.html',
  styleUrl: './player-detail.component.css'
})
export class PlayerDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private playerService = inject(PlayerService);
  private predictionService = inject(PredictionService);
  private cdr = inject(ChangeDetectorRef);

  playerId!: number;
  player: Player | null = null;
  predictionHistory: any[] = [];
  loading = false;
  error: string | null = null;

  ngOnInit(): void {
    this.playerId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadPlayer();
    this.loadPredictionHistory();
  }

  loadPlayer(): void {
    this.loading = true;
    this.playerService.getPlayer(this.playerId).subscribe({
      next: (data) => {
        this.player = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load player', err);
        this.error = 'Failed to load player details';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadPredictionHistory(): void {
    this.predictionService.getPlayerHistory(this.playerId).subscribe({
      next: (data) => {
        this.predictionHistory = Array.isArray(data) ? data : [];
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load prediction history', err);
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

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }
}
