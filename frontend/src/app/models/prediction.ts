export interface PredictionRequest {
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

export interface FactorContribution {
  feature: string;
  value: number;
  contribution: number;
  direction: string;
  explanation: string;
}

export interface PredictionResponse {
  playerName?: string | null;
  baselineScore: number;
  predictedFormRating: number;
  riskLevel: string;
  summary: string;
  positiveFactors: FactorContribution[];
  negativeFactors: FactorContribution[];
  allFactors: FactorContribution[];
}
