import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../services/dashboard.service';

@Component({
  selector: 'app-validation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './validation.component.html',
  styleUrl: './validation.component.css'
})
export class ValidationComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private cdr = inject(ChangeDetectorRef);

  loading = false;
  error: string | null = null;

  validation: any = null;
  explanation: any = null;

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.error = null;

    let validationLoaded = false;
    let explanationLoaded = false;

    const finishIfDone = () => {
      if (validationLoaded && explanationLoaded) {
        this.loading = false;
        this.cdr.markForCheck();
      }
    };

    this.dashboardService.getValidation().subscribe({
      next: (data) => {
        this.validation = data;
        validationLoaded = true;
        finishIfDone();
      },
      error: (err) => {
        console.error('Failed to load validation summary', err);
        this.error = 'Failed to load validation summary';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });

    this.dashboardService.getModelExplanation().subscribe({
      next: (data) => {
        this.explanation = data;
        explanationLoaded = true;
        finishIfDone();
      },
      error: (err) => {
        console.error('Failed to load model explanation', err);
        this.error = 'Failed to load model explanation';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }
}
