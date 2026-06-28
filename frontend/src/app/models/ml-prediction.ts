export interface MlPredictionRequest {
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

export interface MlFeatureImpact {
  feature: string;
  value: number | string;
  importance: number;
  effect: 'positive' | 'negative' | 'neutral';
  explanation: string;
}

export interface MlPredictionResponse {
  playerName: string;
  modelType: string;
  predictedScore: number;
  riskLevel: string;
  confidence: number;
  summary: string;
  topFeatures: MlFeatureImpact[];
}

export interface MlModelInfo {
  modelName: string;
  modelType: string;
  trainingStatus: string;
  description: string;
  supportedFeatures: string[];
}
