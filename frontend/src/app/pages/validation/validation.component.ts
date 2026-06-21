import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { ValidationService } from '../../services/validation.service';

@Component({
  selector: 'app-validation',
  standalone: true,
  templateUrl: './validation.component.html',
  styleUrl: './validation.component.css'
})
export class ValidationComponent implements OnInit {
  private validationService = inject(ValidationService);
  private cdr = inject(ChangeDetectorRef);

  summary: any = null;
  explanation: any = null;
  error: string | null = null;

  ngOnInit(): void {
    this.loadSummary();
    this.loadExplanation();
  }

  loadSummary(): void {
    this.validationService.getSummary().subscribe({
      next: (data) => {
        this.summary = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load validation summary', err);
        this.error = 'Failed to load validation summary';
        this.cdr.markForCheck();
      }
    });
  }

  loadExplanation(): void {
    this.validationService.getModelExplanation().subscribe({
      next: (data) => {
        this.explanation = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load model explanation', err);
        this.cdr.markForCheck();
      }
    });
  }

  getDirectionClass(direction: string): string {
    return direction === 'positive' ? 'positive' : 'negative';
  }

  getRiskClass(level: string): string {
    switch (level) {
      case 'LOW':    return 'risk-low';
      case 'MEDIUM': return 'risk-medium';
      case 'HIGH':   return 'risk-high';
      default:       return '';
    }
  }
}
