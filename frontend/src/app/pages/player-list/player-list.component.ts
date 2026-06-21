import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PlayerService } from '../../services/player.service';
import { Player } from '../../models/player';

@Component({
  selector: 'app-player-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './player-list.component.html',
  styleUrl: './player-list.component.css'
})
export class PlayerListComponent implements OnInit {
  private playerService = inject(PlayerService);
  private cdr = inject(ChangeDetectorRef);

  players: Player[] = [];
  loading = false;
  error: string | null = null;

  name = '';
  position = '';
  team = '';

  showAdvanced = false;

  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  ngOnInit(): void {
    this.loadPlayers();
  }

  loadPlayers(): void {
    this.loading = true;
    this.error = null;

    this.playerService.searchPlayers(this.name, this.position, this.team, this.page, this.size).subscribe({
      next: (data) => {
        this.players = data.content ?? [];
        this.totalPages = data.totalPages ?? 0;
        this.totalElements = data.totalElements ?? 0;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load players', err);
        this.error = 'Failed to load players';
        this.loading = false;
        this.cdr.markForCheck();
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

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
