export interface MlFeatureImpactTribuo {
  feature: string;
  value: string;
  importance: number;
  effect: 'positive' | 'negative' | 'neutral';
  explanation: string;
}

export interface MlModelInfoTribuo {
  modelName: string;
  modelType: string;
  trainingStatus: string;
  description: string;
  supportedFeatures: string[];
}

export interface MlPredictionTribuoRequest {
  playerId: number | null;
  playerName: string;
  age: number;
  position: string;
  matchesPlayed: number;
  goals: number;
  assists: number;
  minutesPlayed: number;
  yellowCards: number;
  redCards: number;
  shotsOnTarget: number;
  passAccuracy: number;
  injuryStatus: boolean;
  expectedGoals: number;
  expectedAssists: number;
  keyPasses: number;
  progressivePasses: number;
  dribblesCompleted: number;
  tacklesWon: number;
  interceptions: number;
  ballRecoveries: number;
  matchesMissed: number;
  recentMatchLoad: number;
}

export interface MlPredictionTribuoResponse {
  playerName: string;
  modelName: string;
  modelType: string;
  predictedScore: number;
  riskLevel: string;
  confidence: number;
  summary: string;
  topFeatures: MlFeatureImpactTribuo[];
}

export interface MlPredictionTribuoHistoryResponse {
  id: number;
  playerId: number | null;
  playerName: string;
  modelName: string;
  modelType: string;
  predictedScore: number;
  riskLevel: string;
  confidence: number;
  summary: string;
  predictedAt: string;
}

export interface MlTribuoTrainingInfoResponse {
  modelName: string;
  modelType: string;
  trainingStatus: string;
  trainingRowCount: number;
  trainingSource: string;
  lastTrainedAt: string | null;
  supportedFeatures: string[];
}

export interface MlTribuoEvaluationResponse {
  mae: number | null;
  rmse: number | null;
  r2: number | null;
  trainingRows: number | null;
  testRows: number | null;
  splitRatio: number | null;
  evaluatedAt: string | null;
  summary: string;
}


