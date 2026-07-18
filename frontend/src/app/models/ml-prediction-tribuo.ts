export interface MlModelInfoTribuo {
  modelName: string;
  algorithm: string;
  status: string;
  description: string;
  featuresUsed: string[];
}

export interface MlTribuoTrainingInfoResponse {
  modelName: string;
  algorithm: string;
  trainingStatus: string;
  trainingRowCount: number | null;
  trainingSource: string | null;
  lastTrainedAt: string | null;
  totalPlayers: number | null;
  trainablePlayers: number | null;
  excludedPlayers: number | null;
  featuresUsed: string[];
}

export interface MlTribuoEvaluationResponse {
  mae: number | null;
  rmse: number | null;
  r2: number | null;
  trainingRows: number | null;
  testRows: number | null;
  splitRatio: number | null;
  evaluatedAt: string | null;
  totalPlayers: number | null;
  trainablePlayers: number | null;
  excludedPlayers: number | null;
  summary: string;
}

export interface MlPredictionTribuoRequest {
  playerId?: number | null;
  playerName: string;
  age: number | null;
  position?: string | null;
  matchesPlayed?: number | null;
  goals: number | null;
  assists: number | null;
  minutesPlayed: number | null;
  yellowCards?: number | null;
  redCards?: number | null;
  shotsOnTarget: number | null;
  passAccuracy: number | null;
  injuryStatus: boolean | null;
  expectedGoals: number | null;
  expectedAssists: number | null;
  keyPasses: number | null;
  progressivePasses: number | null;
  dribblesCompleted: number | null;
  tacklesWon: number | null;
  interceptions: number | null;
  ballRecoveries: number | null;
  matchesMissed: number | null;
  recentMatchLoad: number | null;
}

export interface MlFeatureImpactTribuo {
  feature: string;
  value: string;
  importance: number;
  effect: string;
  explanation: string;
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

export interface MlTribuoTrainingPreviewRow {
  playerName: string;
  position: string;
  age: number;
  goals: number;
  assists: number;
  minutesPlayed: number;
  expectedGoals: number;
  expectedAssists: number;
  previousScore: number;
  currentScore: number;
  trend: 'IMPROVING' | 'DECLINING' | 'STABLE';
  trendReason: string;
}

export interface MlTribuoEvaluationPlayerRow {
  playerName: string;
  position: string;
  previousScore: number;
  evaluatedScore: number;
  trend: 'IMPROVING' | 'DECLINING' | 'STABLE';
  trendReason: string;
}
