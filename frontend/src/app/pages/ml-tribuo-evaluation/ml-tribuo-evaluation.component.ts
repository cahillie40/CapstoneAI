import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';

import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import {
  MlTribuoEvaluationPlayerRow,
  MlTribuoEvaluationResponse
} from '../../models/ml-prediction-tribuo';

type TrendFilter = 'ALL' | 'IMPROVING' | 'DECLINING' | 'STABLE';

@Component({
  selector: 'app-ml-tribuo-evaluation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ml-tribuo-evaluation.component.html',
  styleUrl: './ml-tribuo-evaluation.component.css'
})
export class MlTribuoEvaluationComponent implements OnInit {
  private tribuoService = inject(MlPredictionTribuoService);
  private cdr = inject(ChangeDetectorRef);

  evaluation: MlTribuoEvaluationResponse | null = null;
  allFilteredPlayers: MlTribuoEvaluationPlayerRow[] = [];

  loadingPlayers = false;
  loadingEvaluation = false;
  evaluating = false;

  error: string | null = null;
  successMessage: string | null = null;
  evaluationStatusMessage: string | null = null;

  name = '';
  position = '';
  trendFilter: TrendFilter = 'ALL';
  showAdvanced = false;

  page = 0;
  size = 10;

  private evaluationStep1Timer: ReturnType<typeof setTimeout> | null = null;
  private evaluationStep2Timer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.loadInitialData();
  }

  loadInitialData(): void {
    this.error = null;
    this.successMessage = null;
    this.loadEvaluation();
    this.loadAllFilteredPlayers();
  }

  get trendFilteredPlayers(): MlTribuoEvaluationPlayerRow[] {
    if (this.trendFilter === 'ALL') {
      return this.allFilteredPlayers;
    }

    return this.allFilteredPlayers.filter((player) => player.trend === this.trendFilter);
  }

  get pagedPlayers(): MlTribuoEvaluationPlayerRow[] {
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

  loadEvaluation(): void {
    this.loadingEvaluation = true;

    this.tribuoService.getEvaluation()
      .pipe(finalize(() => {
        this.loadingEvaluation = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoEvaluationResponse) => {
          this.evaluation = data;
        },
        error: (err: unknown) => {
          console.error('Failed to load evaluation summary', err);
          this.error = 'Failed to load evaluation summary';
        }
      });
  }

  loadAllFilteredPlayers(): void {
    this.loadingPlayers = true;
    this.error = null;

    this.tribuoService.getEvaluationPlayers()
      .pipe(finalize(() => {
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoEvaluationPlayerRow[]) => {
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
          console.error('Failed to load evaluation players', err);
          this.error = 'Failed to load evaluation players';
          this.allFilteredPlayers = [];
        }
      });
  }

  evaluateModel(): void {
    if (this.evaluating) {
      return;
    }

    this.clearTimers();
    this.evaluating = true;
    this.error = null;
    this.successMessage = null;
    this.evaluationStatusMessage = 'Preparing Tribuo evaluation...';

    this.evaluationStep1Timer = setTimeout(() => {
      if (this.evaluating) {
        this.evaluationStatusMessage = 'Building evaluation dataset from player records...';
        this.cdr.markForCheck();
      }
    }, 900);

    this.evaluationStep2Timer = setTimeout(() => {
      if (this.evaluating) {
        this.evaluationStatusMessage = 'Finalising evaluation metrics and refreshing results...';
        this.cdr.markForCheck();
      }
    }, 1800);

    this.tribuoService.evaluateModel()
      .pipe(finalize(() => {
        this.evaluating = false;
        this.clearTimers();
        this.evaluationStatusMessage = null;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoEvaluationResponse) => {
          this.evaluation = data;
          this.successMessage = 'Tribuo evaluation completed successfully.';
          this.loadEvaluation();
          this.loadAllFilteredPlayers();
        },
        error: (err: unknown) => {
          console.error('Failed to evaluate model', err);
          this.error = 'Failed to evaluate Tribuo model';
        }
      });
  }

  refreshAll(): void {
    this.error = null;

    this.name = '';
    this.position = '';
    this.trendFilter = 'ALL';
    this.page = 0;
    this.showAdvanced = false;

    this.loadEvaluation();
    this.loadAllFilteredPlayers();
  }

  applyFilters(): void {
    this.page = 0;
    this.loadAllFilteredPlayers();
  }

  clearFilters(): void {
    this.name = '';
    this.position = '';
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

  getTrend(player: MlTribuoEvaluationPlayerRow): 'IMPROVING' | 'DECLINING' | 'STABLE' {
    return player.trend;
  }

  getTrendClass(player: MlTribuoEvaluationPlayerRow): string {
    if (player.trend === 'IMPROVING') {
      return 'trend-up';
    }

    if (player.trend === 'DECLINING') {
      return 'trend-down';
    }

    return 'trend-stable';
  }

  getTrendReason(player: MlTribuoEvaluationPlayerRow): string {
    return player.trendReason;
  }

  private clearTimers(): void {
    if (this.evaluationStep1Timer) {
      clearTimeout(this.evaluationStep1Timer);
      this.evaluationStep1Timer = null;
    }

    if (this.evaluationStep2Timer) {
      clearTimeout(this.evaluationStep2Timer);
      this.evaluationStep2Timer = null;
    }
  }
}
