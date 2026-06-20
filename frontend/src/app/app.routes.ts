import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { PlayerListComponent } from './pages/player-list/player-list.component';
import { PlayerFormComponent } from './pages/player-form/player-form.component';
import { PlayerDetailComponent } from './pages/player-detail/player-detail.component';
import { PlayerEditComponent } from './pages/player-edit/player-edit.component';
import { PredictionFormComponent } from './pages/prediction-form/prediction-form.component';
import { PredictionHistoryComponent } from './pages/prediction-history/prediction-history.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'players', component: PlayerListComponent },
  { path: 'players/:id', component: PlayerDetailComponent },
  { path: 'players/:id/edit', component: PlayerEditComponent },
  { path: 'add-player', component: PlayerFormComponent },
  { path: 'predict', component: PredictionFormComponent },
  { path: 'history', component: PredictionHistoryComponent }
];
