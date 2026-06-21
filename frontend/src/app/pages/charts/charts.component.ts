import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { PlayerService } from '../../services/player.service';
import { PredictionService } from '../../services/prediction.service';

@Component({
  selector: 'app-charts',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './charts.component.html',
  styleUrl: './charts.component.css'
})
export class ChartsComponent implements OnInit {
  private playerService = inject(PlayerService);
  private predictionService = inject(PredictionService);
  private cdr = inject(ChangeDetectorRef);

  loading = false;
  error: string | null = null;
  players: any[] = [];
  predictions: any[] = [];

  goalsVsXgScatterType: 'scatter' = 'scatter';
  assistsVsXaScatterType: 'scatter' = 'scatter';
  progressivePassesChartType: 'bar' = 'bar';
  recoveriesChartType: 'bar' = 'bar';
  riskDistributionChartType: 'pie' = 'pie';

  goalsVsXgScatterData: ChartConfiguration<'scatter'>['data'] = {
    datasets: []
  };

  assistsVsXaScatterData: ChartConfiguration<'scatter'>['data'] = {
    datasets: []
  };

  progressivePassesChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: []
  };

  recoveriesChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: []
  };

  riskDistributionChartData: ChartConfiguration<'pie'>['data'] = {
    labels: ['LOW', 'MEDIUM', 'HIGH'],
    datasets: [
      {
        data: [0, 0, 0],
        backgroundColor: ['#43a047', '#fbc02d', '#e53935']
      }
    ]
  };

  scatterOptions: ChartConfiguration<'scatter'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false
      },
      tooltip: {
        callbacks: {
          label: (context) => {
            const label = (context.raw as any)?.label ?? 'Player';
            const x = (context.raw as any)?.x ?? 0;
            const y = (context.raw as any)?.y ?? 0;
            return `${label}: (${x}, ${y})`;
          }
        }
      }
    },
    scales: {
      x: {
        type: 'linear',
        position: 'bottom',
        title: {
          display: true,
          text: 'Expected Value'
        }
      },
      y: {
        beginAtZero: true,
        title: {
          display: true,
          text: 'Actual Value'
        }
      }
    }
  };

  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true
      }
    },
    scales: {
      x: {
        ticks: {
          autoSkip: false
        }
      },
      y: {
        beginAtZero: true
      }
    }
  };

  pieChartOptions: ChartConfiguration<'pie'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'bottom'
      }
    }
  };

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.error = null;

    let playersLoaded = false;
    let predictionsLoaded = false;

    const finishIfDone = () => {
      if (playersLoaded && predictionsLoaded) {
        this.buildCharts();
        this.loading = false;
        this.cdr.markForCheck();
      }
    };

    this.playerService.getPlayers().subscribe({
      next: (data) => {
        this.players = Array.isArray(data) ? data : [];
        playersLoaded = true;
        finishIfDone();
      },
      error: (err) => {
        console.error('Failed to load players for charts', err);
        this.error = 'Failed to load player chart data';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });

    this.predictionService.getHistory().subscribe({
      next: (data) => {
        this.predictions = Array.isArray(data) ? data : [];
        predictionsLoaded = true;
        finishIfDone();
      },
      error: (err) => {
        console.error('Failed to load predictions for charts', err);
        this.error = 'Failed to load prediction chart data';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  buildCharts(): void {
    const labels = this.players.map(player => player.name);

    this.goalsVsXgScatterData = {
      datasets: [
        {
          label: 'Goals vs xG',
          data: this.players.map(player => ({
            x: player.expectedGoals ?? 0,
            y: player.goals ?? 0,
            label: player.name
          })),
          backgroundColor: '#1976d2'
        }
      ]
    };

    this.assistsVsXaScatterData = {
      datasets: [
        {
          label: 'Assists vs xA',
          data: this.players.map(player => ({
            x: player.expectedAssists ?? 0,
            y: player.assists ?? 0,
            label: player.name
          })),
          backgroundColor: '#43a047'
        }
      ]
    };

    this.progressivePassesChartData = {
      labels,
      datasets: [
        {
          label: 'Progressive Passes',
          data: this.players.map(player => player.progressivePasses ?? 0),
          backgroundColor: '#fb8c00'
        }
      ]
    };

    this.recoveriesChartData = {
      labels,
      datasets: [
        {
          label: 'Ball Recoveries',
          data: this.players.map(player => player.ballRecoveries ?? 0),
          backgroundColor: '#8e24aa'
        }
      ]
    };

    const lowCount = this.predictions.filter(p => p.riskLevel === 'LOW').length;
    const mediumCount = this.predictions.filter(p => p.riskLevel === 'MEDIUM').length;
    const highCount = this.predictions.filter(p => p.riskLevel === 'HIGH').length;

    this.riskDistributionChartData = {
      labels: ['LOW', 'MEDIUM', 'HIGH'],
      datasets: [
        {
          data: [lowCount, mediumCount, highCount],
          backgroundColor: ['#43a047', '#fbc02d', '#e53935']
        }
      ]
    };
  }
}
