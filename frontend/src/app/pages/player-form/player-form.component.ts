import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Player } from '../../models/player';
import { PlayerService } from '../../services/player';

@Component({
  selector: 'app-player-form',
  imports: [FormsModule],
  templateUrl: './player-form.component.html',
  styleUrl: './player-form.component.css'
})
export class PlayerFormComponent {
  private playerService = inject(PlayerService);
  private router = inject(Router);

  player: Player = {
    name: '',
    age: 0,
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
    injuryStatus: false
  };

  onSubmit(): void {
    this.playerService.createPlayer(this.player).subscribe({
      next: () => {
        this.router.navigateByUrl('/');
      },
      error: (error) => {
        console.error('Failed to create player', error);
      }
    });
  }
}
