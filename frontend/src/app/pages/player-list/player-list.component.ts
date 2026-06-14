import { Component, OnInit, inject, signal } from '@angular/core';
import { Player } from '../../models/player';
import { PlayerService } from '../../services/player';

@Component({
  selector: 'app-player-list',
  standalone: true,
  templateUrl: './player-list.component.html',
  styleUrl: './player-list.component.css'
})
export class PlayerListComponent implements OnInit {
  private playerService = inject(PlayerService);

  players = signal<Player[]>([]);

  ngOnInit(): void {
    this.loadPlayers();
  }

  loadPlayers(): void {
    this.playerService.getPlayers().subscribe({
      next: (response: any) => {
        this.players.set(Array.isArray(response) ? response : []);
      },
      error: (error) => {
        console.error('Failed to load players:', error);
        this.players.set([]);
      }
    });
  }
}
