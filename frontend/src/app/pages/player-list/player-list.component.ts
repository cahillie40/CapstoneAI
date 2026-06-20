import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Player } from '../../models/player';
import { PlayerService } from '../../services/player';

@Component({
  selector: 'app-player-list',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './player-list.component.html',
  styleUrl: './player-list.component.css'
})
export class PlayerListComponent implements OnInit {
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  players: Player[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  searchName = '';
  searchPosition = '';
  searchTeam = '';

  positions = ['', 'Goalkeeper', 'Defender', 'Midfielder', 'Winger', 'Forward', 'Striker'];

  ngOnInit(): void {
    this.loadPlayers();
  }

  loadPlayers(): void {
    this.playerService.searchPlayers(
      this.searchName,
      this.searchPosition,
      this.searchTeam,
      this.currentPage,
      this.pageSize
    ).subscribe({
      next: (response: any) => {
        this.players = response.content || [];
        this.totalElements = response.totalElements || 0;
        this.totalPages = response.totalPages || 0;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load players', err);
        this.cdr.markForCheck();
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadPlayers();
  }

  onClear(): void {
    this.searchName = '';
    this.searchPosition = '';
    this.searchTeam = '';
    this.currentPage = 0;
    this.loadPlayers();
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadPlayers();
  }

  getPages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
