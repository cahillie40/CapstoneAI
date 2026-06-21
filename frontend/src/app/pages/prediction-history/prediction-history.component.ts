import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
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

  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  pageSize = 10;

  expandedPredictionId: number | null = null;

  ngOnInit(): void {
    this.loadPredictions();
  }

  loadPredictions(page: number = 0): void {
    this.loading = true;
    this.error = null;

    this.predictionService.getHistoryPaged(page, this.pageSize).subscribe({
      next: (data) => {
        this.predictions = (data.content || []).map((pred: any) => ({
          ...pred,
          parsedInputData: this.parseInputData(pred.inputData)
        }));

        this.currentPage = data.number ?? 0;
        this.totalPages = data.totalPages ?? 0;
        this.totalElements = data.totalElements ?? 0;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load prediction history', err);
        this.error = 'Failed to load prediction history';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  toggleBreakdown(predictionId: number): void {
    this.expandedPredictionId = this.expandedPredictionId === predictionId ? null : predictionId;
  }

  isExpanded(predictionId: number): boolean {
    return this.expandedPredictionId === predictionId;
  }

  parseInputData(inputData: string | null | undefined): any | null {
    if (!inputData) return null;

    try {
      return JSON.parse(inputData);
    } catch {
      return null;
    }
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.loadPredictions(page);
  }

  getPages(): number[] {
    const pages: number[] = [];
    for (let i = 0; i < this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  exportCsv(): void {
    this.predictionService.exportCsv();
  }

  getRiskClass(riskLevel: string): string {
    switch (riskLevel) {
      case 'LOW':
        return 'risk-low';
      case 'MEDIUM':
        return 'risk-medium';
      case 'HIGH':
        return 'risk-high';
      default:
        return '';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }
}
