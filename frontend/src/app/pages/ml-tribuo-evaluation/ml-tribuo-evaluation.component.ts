import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
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

  ngOnInit(): void {
    this.loadEvaluation();
  }

  loadEvaluation(): void {
    this.loading = true;
    this.error = null;

    this.tribuoService.getEvaluation().subscribe({
      next: (data: MlTribuoEvaluationResponse) => {
        this.evaluation = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load evaluation', err);
        this.error = 'Failed to load evaluation';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  evaluateModel(): void {
    this.evaluating = true;
    this.error = null;

    this.tribuoService.evaluateModel().subscribe({
      next: (data: MlTribuoEvaluationResponse) => {
        this.evaluation = data;
        this.evaluating = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to evaluate model', err);
        this.error = 'Failed to evaluate model';
        this.evaluating = false;
        this.cdr.markForCheck();
      }
    });
  }
}
