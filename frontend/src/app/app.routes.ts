import { Routes } from '@angular/router';
import { PlayerListComponent } from './pages/player-list/player-list.component';
import { PlayerFormComponent } from './pages/player-form/player-form.component';

export const routes: Routes = [
  { path: '', component: PlayerListComponent },
  { path: 'add-player', component: PlayerFormComponent }
];
