import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Player } from '../../models/player';
import { PlayerService } from '../../services/player.service';

@Component({
  selector: 'app-player-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './player-form.component.html',
  styleUrl: './player-form.component.css'
})
export class PlayerFormComponent {
  private playerService = inject(PlayerService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  error: string | null = null;
  saving = false;

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

  submit(): void {
    this.error = null;
    this.saving = true;

    this.playerService.createPlayer(this.player).subscribe({
      next: () => {
        this.saving = false;
        this.router.navigate(['/players']);
      },
      error: (err) => {
        console.error('Failed to save player', err);
        this.error = 'Failed to save player';
        this.saving = false;
        this.cdr.markForCheck();
      }
    });
  }
}
