import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PredictionService } from '../../services/prediction.service';

@Component({
  selector: 'app-prediction-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './prediction-history.component.html',
  styleUrl: './prediction-history.component.css'
})
export class PredictionHistoryComponent implements OnInit {
  private predictionService = inject(PredictionService);
  private cdr = inject(ChangeDetectorRef);

  predictions: any[] = [];
  loading = false;
  error: string | null = null;

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.loading = true;
    this.predictionService.getHistory().subscribe({
      next: (data) => {
        this.predictions = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load history', err);
        this.error = 'Failed to load prediction history';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  getRiskClass(riskLevel: string): string {
    switch (riskLevel) {
      case 'LOW':    return 'risk-low';
      case 'MEDIUM': return 'risk-medium';
      case 'HIGH':   return 'risk-high';
      default:       return '';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }
}
