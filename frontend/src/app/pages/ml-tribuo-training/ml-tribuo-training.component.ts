import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { MlPredictionTribuoService } from '../../services/ml-prediction-tribuo.service';
import { PlayerService } from '../../services/player.service';
import { Player } from '../../models/player';
import {
  MlModelInfoTribuo,
  MlTribuoTrainingInfoResponse
} from '../../models/ml-prediction-tribuo';

@Component({
  selector: 'app-ml-tribuo-training',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './ml-tribuo-training.component.html',
  styleUrl: './ml-tribuo-training.component.css'
})
export class MlTribuoTrainingComponent implements OnInit {
  private tribuoService = inject(MlPredictionTribuoService);
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  modelInfo: MlModelInfoTribuo | null = null;
  trainingInfo: MlTribuoTrainingInfoResponse | null = null;

  players: Player[] = [];
  loadingPlayers = false;

  loadingModelInfo = false;
  loadingTrainingInfo = false;
  training = false;

  error: string | null = null;
  successMessage: string | null = null;
  trainingStatusMessage: string | null = null;
  lastTrainingDurationMs: number | null = null;

  name = '';
  position = '';
  team = '';
  showAdvanced = false;

  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  private trainingStep1Timer: ReturnType<typeof setTimeout> | null = null;
  private trainingStep2Timer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.refreshAll();
  }

  get improvingCount(): number {
    return this.players.filter((player) => this.getTrend(player) === 'IMPROVING').length;
  }

  get decliningCount(): number {
    return this.players.filter((player) => this.getTrend(player) === 'DECLINING').length;
  }

  get stableCount(): number {
    return this.players.filter((player) => this.getTrend(player) === 'STABLE').length;
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  loadPlayers(): void {
    this.loadingPlayers = true;
    this.error = null;

    this.playerService.searchPlayers(this.name, this.position, this.team, this.page, this.size)
      .pipe(finalize(() => {
        this.loadingPlayers = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data) => {
          this.players = data.content ?? [];
          this.totalPages = data.totalPages ?? 0;
          this.totalElements = data.totalElements ?? 0;
        },
        error: (err) => {
          console.error('Failed to load players', err);
          this.error = 'Failed to load players';
        }
      });
  }

  loadModelInfo(): void {
    this.loadingModelInfo = true;
    this.tribuoService.getTrainingModelInfo()
      .pipe(finalize(() => {
        this.loadingModelInfo = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlModelInfoTribuo) => {
          this.modelInfo = data;
        },
        error: (err: unknown) => {
          console.error('Failed to load training model info', err);
          this.error = 'Failed to load training model info';
        }
      });
  }

  loadTrainingInfo(): void {
    this.loadingTrainingInfo = true;
    this.tribuoService.getTrainingInfo()
      .pipe(finalize(() => {
        this.loadingTrainingInfo = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoTrainingInfoResponse) => {
          this.trainingInfo = data;
        },
        error: (err: unknown) => {
          console.error('Failed to load training info', err);
          this.error = 'Failed to load training info';
        }
      });
  }

  trainModel(): void {
    if (this.training) {
      return;
    }

    const startedAt = performance.now();

    this.training = true;
    this.error = null;
    this.successMessage = null;
    this.trainingStatusMessage = 'Preparing training dataset...';

    this.clearTrainingTimers();

    this.trainingStep1Timer = setTimeout(() => {
      if (this.training) {
        this.trainingStatusMessage = 'Running Tribuo regression training...';
        this.cdr.markForCheck();
      }
    }, 350);

    this.trainingStep2Timer = setTimeout(() => {
      if (this.training) {
        this.trainingStatusMessage = 'Refreshing model metadata and player table...';
        this.cdr.markForCheck();
      }
    }, 900);

    this.tribuoService.trainModel()
      .pipe(finalize(() => {
        this.training = false;
        this.trainingStatusMessage = null;
        this.clearTrainingTimers();
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: (data: MlTribuoTrainingInfoResponse) => {
          this.trainingInfo = data;
          this.lastTrainingDurationMs = Math.round(performance.now() - startedAt);
          this.successMessage = `Tribuo model retrained successfully in ${this.lastTrainingDurationMs} ms.`;
          this.loadModelInfo();
          this.loadTrainingInfo();
          this.loadPlayers();
        },
        error: (err: unknown) => {
          console.error('Failed to train model', err);
          this.error = 'Failed to train model.';
        }
      });
  }

  applyFilters(): void {
    this.page = 0;
    this.loadPlayers();
  }

  clearFilters(): void {
    this.name = '';
    this.position = '';
    this.team = '';
    this.page = 0;
    this.loadPlayers();
  }

  toggleAdvanced(): void {
    this.showAdvanced = !this.showAdvanced;
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadPlayers();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadPlayers();
    }
  }

  goToPage(pageIndex: number): void {
    if (pageIndex >= 0 && pageIndex < this.totalPages) {
      this.page = pageIndex;
      this.loadPlayers();
    }
  }

  refreshAll(): void {
    this.error = null;
    this.successMessage = null;
    this.loadModelInfo();
    this.loadTrainingInfo();
    this.loadPlayers();
  }

  getTrend(player: Player): 'IMPROVING' | 'DECLINING' | 'STABLE' {
    const form = player.formRating ?? 0;

    if (player.injuryStatus || (player.matchesMissed ?? 0) >= 5 || form < 5) {
      return 'DECLINING';
    }

    if ((player.goals ?? 0) >= 10 || (player.assists ?? 0) >= 7 || form >= 7) {
      return 'IMPROVING';
    }

    return 'STABLE';
  }

  private clearTrainingTimers(): void {
    if (this.trainingStep1Timer) {
      clearTimeout(this.trainingStep1Timer);
      this.trainingStep1Timer = null;
    }

    if (this.trainingStep2Timer) {
      clearTimeout(this.trainingStep2Timer);
      this.trainingStep2Timer = null;
    }
  }
}
