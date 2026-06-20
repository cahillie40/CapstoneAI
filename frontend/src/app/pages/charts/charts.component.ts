import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import { ChartData, ChartOptions } from 'chart.js';
import { PlayerService } from '../../services/player';
import { PredictionService } from '../../services/prediction.service';

@Component({
  selector: 'app-charts',
  standalone: true,
  imports: [BaseChartDirective],
  templateUrl: './charts.component.html',
  styleUrl: './charts.component.css'
})
export class ChartsComponent implements OnInit {
  private playerService = inject(PlayerService);
  private predictionService = inject(PredictionService);
  private cdr = inject(ChangeDetectorRef);

  loaded = false;
  error: string | null = null;

  // Bar chart — form ratings
  formRatingChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        label: 'Form Rating',
        data: [],
        backgroundColor: '#1976d2'
      }
    ]
  };

  formRatingChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    plugins: {
      legend: { display: true },
      title: { display: true, text: 'Player Form Ratings' }
    },
    scales: {
      y: { beginAtZero: true, max: 100 }
    }
  };

  // Bar chart — goals per player
  goalsChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        label: 'Goals',
        data: [],
        backgroundColor: '#43a047'
      }
    ]
  };

  goalsChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    plugins: {
      legend: { display: true },
      title: { display: true, text: 'Goals Per Player' }
    },
    scales: {
      y: { beginAtZero: true }
    }
  };

  // Pie chart — risk level breakdown
  riskChartData: ChartData<'pie'> = {
    labels: ['Low Risk', 'Medium Risk', 'High Risk'],
    datasets: [
      {
        data: [0, 0, 0],
        backgroundColor: ['#43a047', '#fb8c00', '#e53935']
      }
    ]
  };

  riskChartOptions: ChartOptions<'pie'> = {
    responsive: true,
    plugins: {
      legend: { display: true, position: 'bottom' },
      title: { display: true, text: 'Risk Level Breakdown' }
    }
  };

  ngOnInit(): void {
    this.loadPlayers();
    this.loadPredictions();
  }

  loadPlayers(): void {
    this.playerService.getPlayers().subscribe({
      next: (response: any) => {
        const players = Array.isArray(response) ? response : [];

        this.formRatingChartData = {
          labels: players.map((p: any) => p.name),
          datasets: [
            {
              label: 'Form Rating',
              data: players.map((p: any) => p.formRating || 0),
              backgroundColor: '#1976d2'
            }
          ]
        };

        this.goalsChartData = {
          labels: players.map((p: any) => p.name),
          datasets: [
            {
              label: 'Goals',
              data: players.map((p: any) => p.goals || 0),
              backgroundColor: '#43a047'
            }
          ]
        };

        this.loaded = true;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load players', err);
        this.error = 'Failed to load player data';
        this.cdr.markForCheck();
      }
    });
  }

  loadPredictions(): void {
    this.predictionService.getHistory().subscribe({
      next: (data: any) => {
        const predictions = Array.isArray(data) ? data : [];

        const low    = predictions.filter((p: any) => p.riskLevel === 'LOW').length;
        const medium = predictions.filter((p: any) => p.riskLevel === 'MEDIUM').length;
        const high   = predictions.filter((p: any) => p.riskLevel === 'HIGH').length;

        this.riskChartData = {
          labels: ['Low Risk', 'Medium Risk', 'High Risk'],
          datasets: [
            {
              data: [low, medium, high],
              backgroundColor: ['#43a047', '#fb8c00', '#e53935']
            }
          ]
        };

        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load predictions', err);
        this.cdr.markForCheck();
      }
    });
  }
}
