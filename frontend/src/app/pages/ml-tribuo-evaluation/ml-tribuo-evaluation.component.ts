import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import { PlayerService } from '../../services/player.service';
import { Player } from '../../models/player';
import { MlTribuoEvaluationResponse } from '../../models/ml-prediction-tribuo';

type TrendFilter = 'ALL' | 'IMPROVING' | 'DECLINING' | 'STABLE';

@Component({
  selector: 'app-ml-tribuo-evaluation',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './ml-tribuo-evaluation.component.html',
  styleUrl: './ml-tribuo-evaluation.component.css'
})
export class MlTribuoEvaluationComponent implements OnInit {
  private tribuoService = inject(MlPredictionTribuoService);
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  evaluation: MlTribuoEvaluationResponse | null = null;
  allFilteredPlayers: Player[] = [];

  loading = false;
  loadingPlayers = false;
  evaluating = false;

  error: string | null = null;
  successMessage: string | null = null;
  evaluationStatusMessage: string | null = null;
  lastEvaluationDurationMs: number | null = null;

  name = '';
  position = '';
  team = '';
  trendFilter: TrendFilter = 'ALL';
  showAdvanced = false;

  page = 0;
  size = 10;

  private evalStep1Timer: ReturnType<typeof setTimeout> | null = null;
  private evalStep2Timer: ReturnType<typeof setTimeout> | null = null;

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

  loadEvaluation(): void {
    this.loading = true;
    this.error = null;

    this.tribuoService.getEvaluation()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoEvaluationResponse) => {
          this.evaluation = data;
        },
        error: (err: unknown) => {
          console.error('Failed to load evaluation', err);
          this.error = 'Failed to load evaluation';
        }
      });
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

  evaluateModel(): void {
    if (this.evaluating) {
      return;
    }

    const startedAt = performance.now();

    this.clearTimers();
    this.evaluating = true;
    this.error = null;
    this.successMessage = null;
    this.lastEvaluationDurationMs = null;
    this.evaluationStatusMessage = 'Preparing database-backed Tribuo evaluation...';

    this.evalStep1Timer = setTimeout(() => {
      if (this.evaluating) {
        this.evaluationStatusMessage = 'Building dataset and evaluating player records...';
        this.cdr.markForCheck();
      }
    }, 900);

    this.evalStep2Timer = setTimeout(() => {
      if (this.evaluating) {
        this.evaluationStatusMessage = 'Refreshing evaluation summary...';
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
          this.lastEvaluationDurationMs = Math.round(performance.now() - startedAt);
          this.successMessage = `Tribuo evaluation completed successfully in ${this.lastEvaluationDurationMs} ms.`;

          this.loadAllFilteredPlayers();
        },
        error: (err: unknown) => {
          console.error('Failed to run evaluation', err);
          this.error = 'Failed to run Tribuo evaluation';
        }
      });
  }

  refreshAll(): void {
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
      return 'Injury status is negatively affecting availability and trend outlook.';
    }

    if (this.safe(player.matchesMissed) >= 5) {
      return 'Missed matches reduce continuity and weaken the current trend profile.';
    }

    if (this.safe(player.goals) >= 10 || this.safe(player.assists) >= 7) {
      return 'Strong attacking output is supporting a positive trend signal.';
    }

    if (this.safe(player.expectedGoals) >= 8 || this.safe(player.expectedAssists) >= 6) {
      return 'Underlying expected metrics support stronger current performance.';
    }

    if (this.safe(player.minutesPlayed) < 1500) {
      return 'Lower minutes played are limiting the current performance profile.';
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
    if (this.evalStep1Timer) {
      clearTimeout(this.evalStep1Timer);
      this.evalStep1Timer = null;
    }

    if (this.evalStep2Timer) {
      clearTimeout(this.evalStep2Timer);
      this.evalStep2Timer = null;
    }
  }
}
