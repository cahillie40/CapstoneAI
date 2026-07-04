import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import { PlayerService } from '../../services/player.service';
import { Player } from '../../models/player';
import {
  MlModelInfoTribuo,
  MlTribuoTrainingInfoResponse
} from '../../models/ml-prediction-tribuo';

type TrendFilter = 'ALL' | 'IMPROVING' | 'DECLINING' | 'STABLE';

@Component({
  selector: 'app-ml-tribuo-training',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './ml-tribuo-training.component.html',
  styleUrl: './ml-tribuo-training.component.css'
})
export class MlTribuoTrainingComponent implements OnInit {
  private tribuoService = inject(MlPredictionTribuoService);
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  modelInfo: MlModelInfoTribuo | null = null;
  trainingInfo: MlTribuoTrainingInfoResponse | null = null;

  allFilteredPlayers: Player[] = [];

  loadingPlayers = false;
  loadingModelInfo = false;
  loadingTrainingInfo = false;
  training = false;

  error: string | null = null;
  successMessage: string | null = null;
  trainingStatusMessage: string | null = null;
  lastTrainingDurationMs: number | null = null;

  name = '';
  position = '';
  team = '';
  trendFilter: TrendFilter = 'ALL';
  showAdvanced = false;

  page = 0;
  size = 10;

  private trainingStep1Timer: ReturnType<typeof setTimeout> | null = null;
  private trainingStep2Timer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.refreshAll();
  }

  get trendFilteredPlayers(): Player[] {
    if (this.trendFilter === 'ALL') {
      return this.allFilteredPlayers;
    }

    return this.allFilteredPlayers.filter((player) => this.getTrend(player) === this.trendFilter);
  }

  get pagedPlayers(): Player[] {
    const start = this.page * this.size;
    return this.trendFilteredPlayers.slice(start, start + this.size);
  }

  get totalElements(): number {
    return this.trendFilteredPlayers.length;
  }

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.size);
  }

  get improvingCount(): number {
    return this.allFilteredPlayers.filter((player) => this.getTrend(player) === 'IMPROVING').length;
  }

  get decliningCount(): number {
    return this.allFilteredPlayers.filter((player) => this.getTrend(player) === 'DECLINING').length;
  }

  get stableCount(): number {
    return this.allFilteredPlayers.filter((player) => this.getTrend(player) === 'STABLE').length;
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  loadAllFilteredPlayers(): void {
    this.loadingPlayers = true;
    this.error = null;

    this.playerService.searchPlayers(this.name, this.position, this.team, 0, 1000)
      .pipe(finalize(() => {
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data) => {
          this.allFilteredPlayers = data.content ?? [];
          this.page = 0;
        },
        error: (err) => {
          console.error('Failed to load players', err);
          this.error = 'Failed to load players';
          this.allFilteredPlayers = [];
        }
      });
  }

  loadModelInfo(): void {
    this.loadingModelInfo = true;
    this.tribuoService.getTrainingModelInfo()
      .pipe(finalize(() => {
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlModelInfoTribuo) => {
          this.modelInfo = data;
        },
        error: (err: unknown) => {
          console.error('Failed to load training model info', err);
          this.error = 'Failed to load training model info';
        }
      });
  }

  loadTrainingInfo(): void {
    this.loadingTrainingInfo = true;
    this.tribuoService.getTrainingInfo()
      .pipe(finalize(() => {
        this.loadingTrainingInfo = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoTrainingInfoResponse) => {
          this.trainingInfo = data;
        },
        error: (err: unknown) => {
          console.error('Failed to load training info', err);
          this.error = 'Failed to load training info';
        }
      });
  }

  trainModel(): void {
    if (this.training) {
      return;
    }

    const startedAt = performance.now();

    this.clearTimers();
    this.training = true;
    this.error = null;
    this.successMessage = null;
    this.lastTrainingDurationMs = null;
    this.trainingStatusMessage = 'Preparing database-backed Tribuo training...';

    this.trainingStep1Timer = setTimeout(() => {
      if (this.training) {
        this.trainingStatusMessage = 'Building training dataset from player records...';
        this.cdr.markForCheck();
      }
    }, 900);

    this.trainingStep2Timer = setTimeout(() => {
      if (this.training) {
        this.trainingStatusMessage = 'Finalising model training and refreshing summary...';
        this.cdr.markForCheck();
      }
    }, 1800);

    this.tribuoService.trainModel()
      .pipe(finalize(() => {
        this.training = false;
        this.clearTimers();
        this.trainingStatusMessage = null;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoTrainingInfoResponse) => {
          this.trainingInfo = data;
          this.lastTrainingDurationMs = Math.round(performance.now() - startedAt);
          this.successMessage = `Tribuo model retrained successfully in ${this.lastTrainingDurationMs} ms.`;

          this.loadTrainingInfo();
          this.loadModelInfo();
          this.loadAllFilteredPlayers();
        },
        error: (err: unknown) => {
          console.error('Failed to retrain model', err);
          this.error = 'Failed to retrain Tribuo model';
        }
      });
  }

  refreshAll(): void {
    this.loadModelInfo();
    this.loadTrainingInfo();
    this.loadAllFilteredPlayers();
  }

  applyFilters(): void {
    this.page = 0;
    this.loadAllFilteredPlayers();
  }

  clearFilters(): void {
    this.name = '';
    this.position = '';
    this.team = '';
    this.trendFilter = 'ALL';
    this.page = 0;
    this.loadAllFilteredPlayers();
  }

  onTrendFilterChange(): void {
    this.page = 0;
  }

  goToPage(pageNumber: number): void {
    if (pageNumber < 0 || pageNumber >= this.totalPages || pageNumber === this.page) {
      return;
    }

    this.page = pageNumber;
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
    }
  }

  toggleAdvanced(): void {
    this.showAdvanced = !this.showAdvanced;
  }

  getTrend(player: Player): 'IMPROVING' | 'DECLINING' | 'STABLE' {
    const score = this.safe(player.formRating);
    const shift = this.deriveTrendShift(player);
    const previous = score - shift;

    if (score > previous) {
      return 'IMPROVING';
    }

    if (score < previous) {
      return 'DECLINING';
    }

    return 'STABLE';
  }

  getTrendClass(player: Player): string {
    const trend = this.getTrend(player);

    if (trend === 'IMPROVING') {
      return 'trend-up';
    }

    if (trend === 'DECLINING') {
      return 'trend-down';
    }

    return 'trend-stable';
  }

  getTrendReason(player: Player): string {
    if (player.injuryStatus) {
      return 'Injury status is negatively affecting training outlook.';
    }

    if (this.safe(player.matchesMissed) >= 5) {
      return 'Missed matches reduce continuity in the player profile.';
    }

    if (this.safe(player.goals) >= 10 || this.safe(player.assists) >= 7) {
      return 'Strong attacking contribution supports an improving training trend.';
    }

    if (this.safe(player.expectedGoals) >= 8 || this.safe(player.expectedAssists) >= 6) {
      return 'Underlying expected metrics support stronger model input quality.';
    }

    if (this.safe(player.minutesPlayed) < 1500) {
      return 'Lower minutes played weaken the current training profile.';
    }

    return 'Trend is based on the current balance of player performance indicators.';
  }

  private deriveTrendShift(player: Player): number {
    let shift = 0;

    if (this.safe(player.goals) >= 10) {
      shift += 2.0;
    }
    if (this.safe(player.assists) >= 7) {
      shift += 1.5;
    }
    if (this.safe(player.expectedGoals) >= 8) {
      shift += 1.0;
    }
    if (this.safe(player.expectedAssists) >= 6) {
      shift += 1.0;
    }
    if (this.safe(player.minutesPlayed) < 1500) {
      shift -= 1.5;
    }
    if (player.injuryStatus) {
      shift -= 2.5;
    }
    if (this.safe(player.matchesMissed) >= 5) {
      shift -= 1.5;
    }

    return shift;
  }

  private safe(value: number | null | undefined): number {
    return value ?? 0;
  }

  private clearTimers(): void {
    if (this.trainingStep1Timer) {
      clearTimeout(this.trainingStep1Timer);
      this.trainingStep1Timer = null;
    }

    if (this.trainingStep2Timer) {
      clearTimeout(this.trainingStep2Timer);
      this.trainingStep2Timer = null;
    }
  }
}
