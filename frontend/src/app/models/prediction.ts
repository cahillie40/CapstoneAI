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
}

export interface PredictionResponse {
  predictedFormRating: number;
  riskLevel: string;
  summary: string;
}
