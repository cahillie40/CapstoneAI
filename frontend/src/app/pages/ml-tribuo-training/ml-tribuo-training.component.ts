import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
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

  loadingModelInfo = false;
  loadingTrainingInfo = false;
  training = false;
  error: string | null = null;
  successMessage: string | null = null;

  trainingPreviewRows: MlTribuoTrainingPreviewRow[] = [];
  loadingTrainingPreview = false;
  trainingStatusMessage: string | null = null;

  ngOnInit(): void {
    this.loadModelInfo();
    this.loadTrainingInfo();
    this.loadTrainingPreview();
  }

  loadModelInfo(): void {
    this.loadingModelInfo = true;
    this.tribuoService.getTrainingModelInfo().subscribe({
      next: (data: MlModelInfoTribuo) => {
        this.modelInfo = data;
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load training model info', err);
        this.error = 'Failed to load training model info';
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadTrainingInfo(): void {
    this.loadingTrainingInfo = true;
    this.tribuoService.getTrainingInfo().subscribe({
      next: (data: MlTribuoTrainingInfoResponse) => {
        this.trainingInfo = data;
        this.loadingTrainingInfo = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load training info', err);
        this.error = 'Failed to load training info';
        this.loadingTrainingInfo = false;
        this.cdr.markForCheck();
      }
    });
  }

  trainModel(): void {
    this.training = true;
    this.error = null;
    this.successMessage = null;
    this.trainingStatusMessage = 'Preparing training dataset...';

    setTimeout(() => {
      if (this.training) {
        this.trainingStatusMessage = 'Running Tribuo regression training...';
        this.cdr.markForCheck();
      }
    }, 500);

    setTimeout(() => {
      if (this.training) {
        this.trainingStatusMessage = 'Finalising model and refreshing training metadata...';
        this.cdr.markForCheck();
      }
    }, 1200);

    this.tribuoService.trainModel().subscribe({
      next: (data: MlTribuoTrainingInfoResponse) => {
        this.trainingInfo = data;
        this.training = false;
        this.trainingStatusMessage = null;
        this.successMessage = 'Tribuo model trained successfully.';
        this.loadModelInfo();
        this.loadTrainingInfo();
        this.loadTrainingPreview();
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to train model', err);
        this.error = 'Failed to train model';
        this.training = false;
        this.trainingStatusMessage = null;
        this.cdr.markForCheck();
      }
    });
  }

  loadTrainingPreview(): void {
    this.loadingTrainingPreview = true;
    this.tribuoService.getTrainingDataPreview().subscribe({
      next: (data: MlTribuoTrainingPreviewRow[]) => {
        this.trainingPreviewRows = data;
        this.loadingTrainingPreview = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load training preview', err);
        this.error = 'Failed to load training preview';
        this.loadingTrainingPreview = false;
        this.cdr.markForCheck();
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
}
