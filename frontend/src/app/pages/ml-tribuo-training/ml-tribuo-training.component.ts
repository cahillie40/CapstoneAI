import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';

import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import {
  MlModelInfoTribuo,
  MlTribuoTrainingInfoResponse,
  MlTribuoTrainingPreviewRow
} from '../../models/ml-prediction-tribuo';

type TrendFilter = 'ALL' | 'IMPROVING' | 'DECLINING' | 'STABLE';

@Component({
  selector: 'app-ml-tribuo-training',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ml-tribuo-training.component.html',
  styleUrl: './ml-tribuo-training.component.css'
})
export class MlTribuoTrainingComponent implements OnInit {
  private tribuoService = inject(MlPredictionTribuoService);
  private cdr = inject(ChangeDetectorRef);

  modelInfo: MlModelInfoTribuo | null = null;
  trainingInfo: MlTribuoTrainingInfoResponse | null = null;

  allFilteredPlayers: MlTribuoTrainingPreviewRow[] = [];

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

  get trendFilteredPlayers(): MlTribuoTrainingPreviewRow[] {
    if (this.trendFilter === 'ALL') {
      return this.allFilteredPlayers;
    }

    return this.allFilteredPlayers.filter((player) => player.trend === this.trendFilter);
  }

  get pagedPlayers(): MlTribuoTrainingPreviewRow[] {
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
    return this.allFilteredPlayers.filter((player) => player.trend === 'IMPROVING').length;
  }

  get decliningCount(): number {
    return this.allFilteredPlayers.filter((player) => player.trend === 'DECLINING').length;
  }

  get stableCount(): number {
    return this.allFilteredPlayers.filter((player) => player.trend === 'STABLE').length;
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  loadAllFilteredPlayers(): void {
    this.loadingPlayers = true;
    this.error = null;

    this.tribuoService.getTrainingDataPreview()
      .pipe(finalize(() => {
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoTrainingPreviewRow[]) => {
          let filtered = data ?? [];

          if (this.name.trim()) {
            filtered = filtered.filter((player) =>
              player.playerName?.toLowerCase().includes(this.name.toLowerCase())
            );
          }

          if (this.position.trim()) {
            filtered = filtered.filter((player) =>
              player.position?.toLowerCase().includes(this.position.toLowerCase())
            );
          }

          this.allFilteredPlayers = filtered;
          this.page = 0;
        },
        error: (err: unknown) => {
          console.error('Failed to load training preview rows', err);
          this.error = 'Failed to load training preview rows';
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
    this.error = null;

    this.name = '';
    this.position = '';
    this.team = '';
    this.trendFilter = 'ALL';
    this.page = 0;
    this.showAdvanced = false;

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

  getTrend(player: MlTribuoTrainingPreviewRow): 'IMPROVING' | 'DECLINING' | 'STABLE' {
    return player.trend;
  }

  getTrendClass(player: MlTribuoTrainingPreviewRow): string {
    if (player.trend === 'IMPROVING') {
      return 'trend-up';
    }

    if (player.trend === 'DECLINING') {
      return 'trend-down';
    }

    return 'trend-stable';
  }

  getTrendReason(player: MlTribuoTrainingPreviewRow): string {
    return player.trendReason;
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
