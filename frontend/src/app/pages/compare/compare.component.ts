import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PlayerService } from '../../services/player.service';

@Component({
  selector: 'app-compare',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './compare.component.html',
  styleUrl: './compare.component.css'
})
export class CompareComponent implements OnInit {
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  players: any[] = [];
  selectedPlayer1Id: number | null = null;
  selectedPlayer2Id: number | null = null;

  player1: any = null;
  player2: any = null;
  winner: string | null = null;
  error: string | null = null;

  ngOnInit(): void {
    this.loadPlayers();
  }

  loadPlayers(): void {
    this.playerService.getPlayers().subscribe({
      next: (data) => {
        this.players = data || [];
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load players', err);
        this.error = 'Failed to load players';
        this.cdr.markForCheck();
      }
    });
  }

  comparePlayers(): void {
    this.error = null;
    this.winner = null;

    if (!this.selectedPlayer1Id || !this.selectedPlayer2Id) {
      this.error = 'Please select two players';
      return;
    }

    if (this.selectedPlayer1Id === this.selectedPlayer2Id) {
      this.error = 'Please select two different players';
      return;
    }

    this.player1 = this.players.find(p => p.id === Number(this.selectedPlayer1Id));
    this.player2 = this.players.find(p => p.id === Number(this.selectedPlayer2Id));

    if (!this.player1 || !this.player2) {
      this.error = 'Could not load selected players';
      return;
    }

    const score1 = this.calculateComparisonScore(this.player1);
    const score2 = this.calculateComparisonScore(this.player2);

    if (score1 > score2) {
      this.winner = this.player1.name;
    } else if (score2 > score1) {
      this.winner = this.player2.name;
    } else {
      this.winner = 'Draw';
    }

    this.cdr.markForCheck();
  }

  calculateComparisonScore(player: any): number {
    let score = 0;

    score += (player.formRating ?? 0) * 2;
    score += (player.goals ?? 0) * 2.5;
    score += (player.assists ?? 0) * 2;
    score += (player.shotsOnTarget ?? 0) * 0.8;
    score += (player.passAccuracy ?? 0) * 0.2;

    score += (player.expectedGoals ?? 0) * 3.0;
    score += (player.expectedAssists ?? 0) * 2.5;
    score += (player.keyPasses ?? 0) * 0.4;
    score += (player.progressivePasses ?? 0) * 0.2;
    score += (player.dribblesCompleted ?? 0) * 0.3;
    score += (player.tacklesWon ?? 0) * 0.25;
    score += (player.interceptions ?? 0) * 0.25;
    score += (player.ballRecoveries ?? 0) * 0.15;

    score -= (player.yellowCards ?? 0) * 0.5;
    score -= (player.redCards ?? 0) * 2.0;
    score -= (player.matchesMissed ?? 0) * 0.6;

    if (player.injuryStatus) {
      score -= 10;
    }

    if ((player.recentMatchLoad ?? 0) > 5) {
      score -= 2;
    }

    return Math.round(score * 10) / 10;
  }
}
