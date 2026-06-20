import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PlayerService } from '../../services/player';
import { PredictionService } from '../../services/prediction.service';
import { Player } from '../../models/player';

@Component({
  selector: 'app-player-detail',
  standalone: true,
  templateUrl: './player-detail.component.html',
  styleUrl: './player-detail.component.css'
})
export class PlayerDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private playerService = inject(PlayerService);
  private predictionService = inject(PredictionService);
  private cdr = inject(ChangeDetectorRef);

  player: Player | null = null;
  predictions: any[] = [];
  error: string | null = null;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadPlayer(id);
      this.loadPredictions(id);
    }
  }

  loadPlayer(id: number): void {
    this.playerService.getPlayer(id).subscribe({
      next: (data) => {
        this.player = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load player', err);
        this.error = 'Failed to load player';
        this.cdr.markForCheck();
      }
    });
  }

  loadPredictions(id: number): void {
    this.predictionService.getPlayerHistory(id).subscribe({
      next: (data: any) => {
        this.predictions = Array.isArray(data) ? data : [];
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load predictions', err);
        this.cdr.markForCheck();
      }
    });
  }

  goToPredict(): void {
    this.router.navigateByUrl('/predict');
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }

  getRiskClass(riskLevel: string): string {
    switch (riskLevel) {
      case 'LOW':    return 'risk-low';
      case 'MEDIUM': return 'risk-medium';
      case 'HIGH':   return 'risk-high';
      default:       return '';
    }
  }
}
