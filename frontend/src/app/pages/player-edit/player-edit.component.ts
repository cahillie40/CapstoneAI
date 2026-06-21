import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Player } from '../../models/player';
import { PlayerService } from '../../services/player.service';

@Component({
  selector: 'app-player-edit',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './player-edit.component.html',
  styleUrl: './player-edit.component.css'
})
export class PlayerEditComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  playerId!: number;
  loading = false;
  saving = false;
  deleting = false;
  error: string | null = null;

  player: Player = {
    name: '',
    age: 18,
    position: '',
    team: '',
    matchesPlayed: 0,
    goals: 0,
    assists: 0,
    minutesPlayed: 0,
    yellowCards: 0,
    redCards: 0,
    shotsOnTarget: 0,
    passAccuracy: 0,
    formRating: 0,
    injuryStatus: false,

    expectedGoals: 0,
    expectedAssists: 0,
    keyPasses: 0,
    progressivePasses: 0,
    dribblesCompleted: 0,
    tacklesWon: 0,
    interceptions: 0,
    ballRecoveries: 0,
    matchesMissed: 0,
    recentMatchLoad: 0
  };

  ngOnInit(): void {
    this.playerId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadPlayer();
  }

  loadPlayer(): void {
    this.loading = true;
    this.error = null;

    this.playerService.getPlayer(this.playerId).subscribe({
      next: (data) => {
        this.player = {
          ...data,
          expectedGoals: data.expectedGoals ?? 0,
          expectedAssists: data.expectedAssists ?? 0,
          keyPasses: data.keyPasses ?? 0,
          progressivePasses: data.progressivePasses ?? 0,
          dribblesCompleted: data.dribblesCompleted ?? 0,
          tacklesWon: data.tacklesWon ?? 0,
          interceptions: data.interceptions ?? 0,
          ballRecoveries: data.ballRecoveries ?? 0,
          matchesMissed: data.matchesMissed ?? 0,
          recentMatchLoad: data.recentMatchLoad ?? 0
        };
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load player', err);
        this.error = 'Failed to load player';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  save(): void {
    this.saving = true;
    this.error = null;

    this.playerService.updatePlayer(this.playerId, this.player).subscribe({
      next: () => {
        this.saving = false;
        this.router.navigate(['/players', this.playerId]);
      },
      error: (err) => {
        console.error('Failed to update player', err);
        this.error = 'Failed to update player';
        this.saving = false;
        this.cdr.markForCheck();
      }
    });
  }

  delete(): void {
    const confirmed = window.confirm('Are you sure you want to delete this player?');
    if (!confirmed) return;

    this.deleting = true;
    this.error = null;

    this.playerService.deletePlayer(this.playerId).subscribe({
      next: () => {
        this.deleting = false;
        this.router.navigate(['/players']);
      },
      error: (err) => {
        console.error('Failed to delete player', err);
        this.error = 'Failed to delete player';
        this.deleting = false;
        this.cdr.markForCheck();
      }
    });
  }
}
