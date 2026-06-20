import { Routes } from '@angular/router';
import { PlayerListComponent } from './pages/player-list/player-list.component';
import { PlayerFormComponent } from './pages/player-form/player-form.component';
import { PredictionFormComponent } from './pages/prediction-form/prediction-form.component';
import { PredictionHistoryComponent } from './pages/prediction-history/prediction-history.component';


export const routes: Routes = [
  { path: '', component: PlayerListComponent },
  { path: 'add-player', component: PlayerFormComponent },
  { path: 'predict', component: PredictionFormComponent },
  { path: 'history', component: PredictionHistoryComponent }
];
