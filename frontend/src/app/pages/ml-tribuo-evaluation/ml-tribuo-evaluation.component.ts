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

  get qualityLabel(): string {
    if (!this.evaluation || this.evaluation.r2 == null) {
      return 'Not Evaluated';
    }
    if (this.evaluation.r2 >= 0.8) {
      return 'Strong';
    }
    if (this.evaluation.r2 >= 0.6) {
      return 'Moderate';
    }
    return 'Weak';
  }

  get qualityClass(): string {
    if (!this.evaluation || this.evaluation.r2 == null) {
      return 'quality-neutral';
    }
    if (this.evaluation.r2 >= 0.8) {
      return 'quality-strong';
    }
    if (this.evaluation.r2 >= 0.6) {
      return 'quality-moderate';
    }
    return 'quality-weak';
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
        this.evaluationStatusMessage = 'Refreshing player trends and metrics...';
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
