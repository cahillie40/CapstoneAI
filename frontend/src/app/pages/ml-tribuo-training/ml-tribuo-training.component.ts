import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import {
  MlModelInfoTribuo,
  MlTribuoTrainingInfoResponse,
  MlTribuoTrainingPreviewRow
} from '../../models/ml-prediction-tribuo';

@Component({
  selector: 'app-ml-tribuo-training',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ml-tribuo-training.component.html',
  styleUrl: './ml-tribuo-training.component.css'
})
export class MlTribuoTrainingComponent implements OnInit {
  private tribuoService = inject(MlPredictionTribuoService);
  private cdr = inject(ChangeDetectorRef);

  modelInfo: MlModelInfoTribuo | null = null;
  trainingInfo: MlTribuoTrainingInfoResponse | null = null;
  trainingPreviewRows: MlTribuoTrainingPreviewRow[] = [];

  loadingModelInfo = false;
  loadingTrainingInfo = false;
  loadingTrainingPreview = false;
  training = false;

  error: string | null = null;
  successMessage: string | null = null;
  trainingStatusMessage: string | null = null;
  lastTrainingDurationMs: number | null = null;

  private trainingStep1Timer: ReturnType<typeof setTimeout> | null = null;
  private trainingStep2Timer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.refreshAll();
  }

  get improvingCount(): number {
    return this.trainingPreviewRows.filter((row) => row.trend === 'IMPROVING').length;
  }

  get decliningCount(): number {
    return this.trainingPreviewRows.filter((row) => row.trend === 'DECLINING').length;
  }

  get stableCount(): number {
    return this.trainingPreviewRows.filter((row) => row.trend === 'STABLE').length;
  }

  get previewPlayerCount(): number {
    return this.trainingPreviewRows.length;
  }

  getScoreDelta(row: MlTribuoTrainingPreviewRow): number {
    return Number((row.currentTargetScore - row.previousScore).toFixed(1));
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

  loadTrainingPreview(): void {
    this.loadingTrainingPreview = true;
    this.tribuoService.getTrainingDataPreview()
      .pipe(finalize(() => {
        this.loadingTrainingPreview = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoTrainingPreviewRow[]) => {
          this.trainingPreviewRows = data;
        },
        error: (err: unknown) => {
          console.error('Failed to load training preview', err);
          this.error = 'Failed to load training preview';
        }
      });
  }

  trainModel(): void {
    if (this.training) {
      return;
    }

    const startedAt = performance.now();

    this.training = true;
    this.error = null;
    this.successMessage = null;
    this.trainingStatusMessage = 'Preparing training dataset...';

    this.clearTrainingTimers();

    this.trainingStep1Timer = setTimeout(() => {
      if (this.training) {
        this.trainingStatusMessage = 'Running Tribuo regression training...';
        this.cdr.markForCheck();
      }
    }, 350);

    this.trainingStep2Timer = setTimeout(() => {
      if (this.training) {
        this.trainingStatusMessage = 'Refreshing model metadata and preview...';
        this.cdr.markForCheck();
      }
    }, 900);

    this.tribuoService.trainModel()
      .pipe(finalize(() => {
        this.training = false;
        this.trainingStatusMessage = null;
        this.clearTrainingTimers();
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoTrainingInfoResponse) => {
          this.trainingInfo = data;
          this.lastTrainingDurationMs = Math.round(performance.now() - startedAt);
          this.successMessage = `Tribuo model retrained successfully in ${this.lastTrainingDurationMs} ms.`;
          this.loadModelInfo();
          this.loadTrainingInfo();
          this.loadTrainingPreview();
        },
        error: (err: unknown) => {
          console.error('Failed to train model', err);
          this.error = 'Failed to train model. Check backend /ml/tribuo/train response in browser network tab.';
        }
      });
  }

  refreshAll(): void {
    this.error = null;
    this.successMessage = null;
    this.loadModelInfo();
    this.loadTrainingInfo();
    this.loadTrainingPreview();
  }

  private clearTrainingTimers(): void {
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
