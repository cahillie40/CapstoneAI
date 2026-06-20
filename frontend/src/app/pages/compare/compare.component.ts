import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Player } from '../../models/player';
import { PlayerService } from '../../services/player';

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

  players: Player[] = [];
  player1Id: number | null = null;
  player2Id: number | null = null;
  player1: Player | null = null;
  player2: Player | null = null;
  compared = false;
  error: string | null = null;

  stats = [
    { label: 'Age',            key: 'age',            lowerIsBetter: false },
    { label: 'Matches Played', key: 'matchesPlayed',  lowerIsBetter: false },
    { label: 'Goals',          key: 'goals',          lowerIsBetter: false },
    { label: 'Assists',        key: 'assists',        lowerIsBetter: false },
    { label: 'Minutes Played', key: 'minutesPlayed',  lowerIsBetter: false },
    { label: 'Shots On Target',key: 'shotsOnTarget',  lowerIsBetter: false },
    { label: 'Pass Accuracy',  key: 'passAccuracy',   lowerIsBetter: false },
    { label: 'Form Rating',    key: 'formRating',     lowerIsBetter: false },
    { label: 'Yellow Cards',   key: 'yellowCards',    lowerIsBetter: true  },
    { label: 'Red Cards',      key: 'redCards',       lowerIsBetter: true  }
  ];

  ngOnInit(): void {
    this.playerService.getPlayers().subscribe({
      next: (response: any) => {
        this.players = Array.isArray(response) ? response : [];
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load players', err);
        this.error = 'Failed to load players';
        this.cdr.markForCheck();
      }
    });
  }

  compare(): void {
    if (!this.player1Id || !this.player2Id) {
      this.error = 'Please select two players to compare';
      return;
    }

    if (this.player1Id === this.player2Id) {
      this.error = 'Please select two different players';
      return;
    }

    this.player1 = this.players.find(p => p.id === this.player1Id) || null;
    this.player2 = this.players.find(p => p.id === this.player2Id) || null;
    this.compared = true;
    this.error = null;
    this.cdr.markForCheck();
  }

  getValue(player: Player, key: string): any {
    return (player as any)[key];
  }

  isWinner(player: Player, key: string, lowerIsBetter: boolean): boolean {
    if (!this.player1 || !this.player2) return false;
    const other = player === this.player1 ? this.player2 : this.player1;
    const val1 = (player as any)[key];
    const val2 = (other as any)[key];
    if (val1 === val2) return false;
    return lowerIsBetter ? val1 < val2 : val1 > val2;
  }

  getWinner(): string {
    if (!this.player1 || !this.player2) return '';

    let p1Score = 0;
    let p2Score = 0;

    for (const stat of this.stats) {
      const v1 = (this.player1 as any)[stat.key];
      const v2 = (this.player2 as any)[stat.key];
      if (v1 === v2) continue;

      if (stat.lowerIsBetter) {
        if (v1 < v2) p1Score++; else p2Score++;
      } else {
        if (v1 > v2) p1Score++; else p2Score++;
      }
    }

    // injury bonus
    if (!this.player1.injuryStatus && this.player2.injuryStatus) p1Score++;
    if (!this.player2.injuryStatus && this.player1.injuryStatus) p2Score++;

    if (p1Score === p2Score) return 'Draw';
    return p1Score > p2Score
      ? `${this.player1.name} wins (${p1Score} vs ${p2Score})`
      : `${this.player2.name} wins (${p2Score} vs ${p1Score})`;
  }
}
