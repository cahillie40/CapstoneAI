import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { PlayerListComponent } from './pages/player-list/player-list.component';
import { PlayerFormComponent } from './pages/player-form/player-form.component';
import { PlayerDetailComponent } from './pages/player-detail/player-detail.component';
import { PlayerEditComponent } from './pages/player-edit/player-edit.component';
import { PredictionFormComponent } from './pages/prediction-form/prediction-form.component';
import { PredictionHistoryComponent } from './pages/prediction-history/prediction-history.component';
import { ChartsComponent } from './pages/charts/charts.component';
import { CompareComponent } from './pages/compare/compare.component';
import { CsvImportComponent } from './pages/csv-import/csv-import.component';
import { ValidationComponent } from './pages/validation/validation.component';
import { MlPredictorComponent } from './pages/ml-predictor/ml-predictor.component';
import { MlPredictionTribuoComponent } from './pages/ml-prediction-tribuo/ml-prediction-tribuo.component';
import { MlTribuoTrainingComponent } from './pages/ml-tribuo-training/ml-tribuo-training.component';
import { MlTribuoEvaluationComponent } from './pages/ml-tribuo-evaluation/ml-tribuo-evaluation.component';


export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'players', component: PlayerListComponent },
  { path: 'players/:id', component: PlayerDetailComponent },
  { path: 'players/:id/edit', component: PlayerEditComponent },
  { path: 'add-player', component: PlayerFormComponent },
  { path: 'predict', component: PredictionFormComponent },
  { path: 'history', component: PredictionHistoryComponent },
  { path: 'charts', component: ChartsComponent },
  { path: 'compare', component: CompareComponent },
  { path: 'import', component: CsvImportComponent },
  { path: 'validation', component: ValidationComponent },
  { path: 'ml-predictor', title: 'ML Predictor', component: MlPredictorComponent },
  { path: 'ml-prediction-tribuo', title: 'ML Prediction Tribuo', component: MlPredictionTribuoComponent },
  {
    path: 'ml-tribuo-training',
    component: MlTribuoTrainingComponent
  },
  {
    path: 'ml-tribuo-evaluation',
    component: MlTribuoEvaluationComponent
  },
];
