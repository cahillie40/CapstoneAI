import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import { PlayerService } from '../../services/player.service';
import { Player } from '../../models/player';
import { MlTribuoEvaluationResponse } from '../../models/ml-prediction-tribuo';

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
  players: Player[] = [];

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
  showAdvanced = false;

  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  private evalStep1Timer: ReturnType<typeof setTimeout> | null = null;
  private evalStep2Timer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.refreshAll();
  }

  get improvingCount(): number {
    return this.players.filter((player) => this.getTrend(player) === 'IMPROVING').length;
  }

  get decliningCount(): number {
    return this.players.filter((player) => this.getTrend(player) === 'DECLINING').length;
  }

  get stableCount(): number {
    return this.players.filter((player) => this.getTrend(player) === 'STABLE').length;
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

  loadPlayers(): void {
    this.loadingPlayers = true;
    this.error = null;

    this.playerService.searchPlayers(this.name, this.position, this.team, this.page, this.size)
      .pipe(finalize(() => {
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data) => {
          this.players = data.content ?? [];
          this.totalPages = data.totalPages ?? 0;
          this.totalElements = data.totalElements ?? 0;
        },
        error: (err) => {
          console.error('Failed to load players', err);
          this.error = 'Failed to load players';
        }
      });
  }

  evaluateModel(): void {
    if (this.evaluating) {
      return;
    }

    const startedAt = performance.now();

    this.evaluating = true;
    this.error = null;
    this.successMessage = null;
    this.evaluationStatusMessage = 'Preparing evaluation dataset...';
    this.clearEvaluationTimers();

    this.evalStep1Timer = setTimeout(() => {
      if (this.evaluating) {
        this.evaluationStatusMessage = 'Scoring Tribuo player outcomes...';
        this.cdr.markForCheck();
      }
    }, 350);

    this.evalStep2Timer = setTimeout(() => {
      if (this.evaluating) {
        this.evaluationStatusMessage = 'Refreshing evaluation summary and player table...';
        this.cdr.markForCheck();
      }
    }, 900);

    this.tribuoService.evaluateModel()
      .pipe(finalize(() => {
        this.evaluating = false;
        this.evaluationStatusMessage = null;
        this.clearEvaluationTimers();
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoEvaluationResponse) => {
          this.evaluation = data;
          this.lastEvaluationDurationMs = Math.round(performance.now() - startedAt);
          this.successMessage = `Tribuo evaluation completed successfully in ${this.lastEvaluationDurationMs} ms.`;
          this.loadEvaluation();
          this.loadPlayers();
        },
        error: (err: unknown) => {
          console.error('Failed to evaluate model', err);
          this.error = 'Failed to evaluate model. Make sure the Tribuo model has been trained first.';
        }
      });
  }

  applyFilters(): void {
    this.page = 0;
    this.loadPlayers();
  }

  clearFilters(): void {
    this.name = '';
    this.position = '';
    this.team = '';
    this.page = 0;
    this.loadPlayers();
  }

  toggleAdvanced(): void {
    this.showAdvanced = !this.showAdvanced;
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadPlayers();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadPlayers();
    }
  }

  goToPage(pageIndex: number): void {
    if (pageIndex >= 0 && pageIndex < this.totalPages) {
      this.page = pageIndex;
      this.loadPlayers();
    }
  }

  refreshAll(): void {
    this.successMessage = null;
    this.loadEvaluation();
    this.loadPlayers();
  }

  getTrend(player: Player): 'IMPROVING' | 'DECLINING' | 'STABLE' {
    const form = player.formRating ?? 0;

    if (player.injuryStatus || (player.matchesMissed ?? 0) >= 5 || form < 5) {
      return 'DECLINING';
    }

    if ((player.goals ?? 0) >= 10 || (player.assists ?? 0) >= 7 || form >= 7) {
      return 'IMPROVING';
    }

    return 'STABLE';
  }

  private clearEvaluationTimers(): void {
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
