import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Player } from '../../models/player';
import { PlayerService } from '../../services/player';

@Component({
  selector: 'app-player-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './player-list.component.html',
  styleUrl: './player-list.component.css'
})
export class PlayerListComponent implements OnInit {
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  players: Player[] = [];

  ngOnInit(): void {
    this.loadPlayers();
  }

  loadPlayers(): void {
    this.playerService.getPlayers().subscribe({
      next: (response: any) => {
        this.players = Array.isArray(response) ? response : [];
        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Failed to load players:', error);
        this.cdr.markForCheck();
      }
    });
  }
}
