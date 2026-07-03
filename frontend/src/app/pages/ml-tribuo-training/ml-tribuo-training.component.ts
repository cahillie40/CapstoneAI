import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import {
  MlModelInfoTribuo,
  MlTribuoTrainingInfoResponse
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

  ngOnInit(): void {
    this.loadModelInfo();
    this.loadTrainingInfo();
  }

  loadModelInfo(): void {
    this.loadingModelInfo = true;
    this.tribuoService.getModelInfo().subscribe({
      next: (data: MlModelInfoTribuo) => {
        this.modelInfo = data;
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load model info', err);
        this.error = 'Failed to load model info';
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

    this.tribuoService.trainModel().subscribe({
      next: (data: MlTribuoTrainingInfoResponse) => {
        this.trainingInfo = data;
        this.training = false;
        this.loadModelInfo();
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to train model', err);
        this.error = 'Failed to train model';
        this.training = false;
        this.cdr.markForCheck();
      }
    });
  }

  refreshAll(): void {
    this.loadModelInfo();
    this.loadTrainingInfo();
  }
}
