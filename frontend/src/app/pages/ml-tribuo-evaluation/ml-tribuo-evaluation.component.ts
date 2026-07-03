import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import { MlTribuoEvaluationResponse } from '../../models/ml-prediction-tribuo';

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
  loading = false;
  evaluating = false;
  error: string | null = null;
  successMessage: string | null = null;
  evaluationStatusMessage: string | null = null;
  lastEvaluationDurationMs: number | null = null;

  private evalStep1Timer: ReturnType<typeof setTimeout> | null = null;
  private evalStep2Timer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.loadEvaluation();
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

  get maeMeaning(): string {
    if (!this.evaluation || this.evaluation.mae == null) {
      return 'No MAE available yet.';
    }

    if (this.evaluation.mae <= 5) {
      return 'Low average error across predictions.';
    }

    if (this.evaluation.mae <= 10) {
      return 'Moderate average prediction error.';
    }

    return 'Higher average prediction error.';
  }

  get rmseMeaning(): string {
    if (!this.evaluation || this.evaluation.rmse == null) {
      return 'No RMSE available yet.';
    }

    if (this.evaluation.rmse <= 6) {
      return 'Prediction variance is fairly controlled.';
    }

    if (this.evaluation.rmse <= 12) {
      return 'Prediction spread is moderate.';
    }

    return 'Large prediction misses are occurring more often.';
  }

  get r2Meaning(): string {
    if (!this.evaluation || this.evaluation.r2 == null) {
      return 'No R² available yet.';
    }

    if (this.evaluation.r2 >= 0.8) {
      return 'The model explains most of the score variation well.';
    }

    if (this.evaluation.r2 >= 0.6) {
      return 'The model explains a reasonable amount of score variation.';
    }

    return 'The model explains only a limited amount of score variation.';
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
        this.evaluationStatusMessage = 'Scoring Tribuo regression model...';
        this.cdr.markForCheck();
      }
    }, 350);

    this.evalStep2Timer = setTimeout(() => {
      if (this.evaluating) {
        this.evaluationStatusMessage = 'Calculating quality metrics and refreshing results...';
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
