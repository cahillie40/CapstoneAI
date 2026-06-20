import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private cdr = inject(ChangeDetectorRef);

  stats: any = null;
  error: string | null = null;

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.dashboardService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load dashboard stats', err);
        this.error = 'Failed to load dashboard stats';
        this.cdr.markForCheck();
      }
    });
  }
}
