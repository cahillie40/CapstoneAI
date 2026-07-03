import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import {
  MlTribuoEvaluationPlayerRow,
  MlTribuoEvaluationResponse
} from '../../models/ml-prediction-tribuo';

@Component({
  selector: 'app-ml-tribuo-evaluation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ml-tribuo-evaluation.component.html',
  styleUrl: './ml-tribuo-evaluation.component.css'
})
export class MlTribuoEvaluationComponent implements OnInit {
  private tribuoService = inject(MlPredictionTribuoService);
  private cdr = inject(ChangeDetectorRef);

  evaluation: MlTribuoEvaluationResponse | null = null;
  evaluationPlayers: MlTribuoEvaluationPlayerRow[] = [];

  loading = false;
  loadingPlayers = false;
  evaluating = false;

  error: string | null = null;
  successMessage: string | null = null;
  evaluationStatusMessage: string | null = null;
  lastEvaluationDurationMs: number | null = null;

  currentPage = 1;
  pageSize = 10;

  private evalStep1Timer: ReturnType<typeof setTimeout> | null = null;
  private evalStep2Timer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.refreshAll();
  }

  get improvingCount(): number {
    return this.evaluationPlayers.filter((row) => row.trend === 'IMPROVING').length;
  }

  get decliningCount(): number {
    return this.evaluationPlayers.filter((row) => row.trend === 'DECLINING').length;
  }

  get stableCount(): number {
    return this.evaluationPlayers.filter((row) => row.trend === 'STABLE').length;
  }

  get paginatedEvaluationPlayers(): MlTribuoEvaluationPlayerRow[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.evaluationPlayers.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.evaluationPlayers.length / this.pageSize));
  }

  get pageStart(): number {
    if (this.evaluationPlayers.length === 0) {
      return 0;
    }
    return (this.currentPage - 1) * this.pageSize + 1;
  }

  get pageEnd(): number {
    return Math.min(this.currentPage * this.pageSize, this.evaluationPlayers.length);
  }

  getScoreDelta(row: MlTribuoEvaluationPlayerRow): number {
    return Number((row.evaluatedScore - row.previousScore).toFixed(1));
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

  loadEvaluationPlayers(): void {
    this.loadingPlayers = true;

    this.tribuoService.getEvaluationPlayers()
      .pipe(finalize(() => {
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoEvaluationPlayerRow[]) => {
          this.evaluationPlayers = data;
          this.currentPage = 1;
        },
        error: (err: unknown) => {
          console.error('Failed to load evaluation players', err);
          this.error = 'Failed to load evaluation player trends';
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
        this.evaluationStatusMessage = 'Refreshing player trends...';
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
          this.loadEvaluationPlayers();
        },
        error: (err: unknown) => {
          console.error('Failed to evaluate model', err);
          this.error = 'Failed to evaluate model. Make sure the Tribuo model has been trained first.';
        }
      });
  }

  refreshAll(): void {
    this.successMessage = null;
    this.loadEvaluation();
    this.loadEvaluationPlayers();
  }

  goToPreviousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.cdr.markForCheck();
    }
  }

  goToNextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.cdr.markForCheck();
    }
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
