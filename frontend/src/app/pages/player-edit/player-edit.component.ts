import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PlayerService } from '../../services/player';
import { Player } from '../../models/player';

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

  playerId: number = 0;
  error: string | null = null;
  success: string | null = null;

  ngOnInit(): void {
    this.playerId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.playerId) {
      this.playerService.getPlayer(this.playerId).subscribe({
        next: (data) => {
          this.player = data;
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Failed to load player', err);
          this.error = 'Failed to load player';
          this.cdr.markForCheck();
        }
      });
    }
  }

  onSubmit(): void {
    this.playerService.updatePlayer(this.playerId, this.player).subscribe({
      next: () => {
        this.router.navigateByUrl(`/players/${this.playerId}`);
      },
      error: (err) => {
        console.error('Failed to update player', err);
        this.error = 'Failed to update player';
        this.cdr.markForCheck();
      }
    });
  }

  onDelete(): void {
    if (confirm(`Are you sure you want to delete ${this.player.name}?`)) {
      this.playerService.deletePlayer(this.playerId).subscribe({
        next: () => {
          this.router.navigateByUrl('/');
        },
        error: (err) => {
          console.error('Failed to delete player', err);
          this.error = 'Failed to delete player';
          this.cdr.markForCheck();
        }
      });
    }
  }
}
