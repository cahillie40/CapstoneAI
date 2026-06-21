export interface Player {
  id?: number;
  name: string;
  age: number;
  position: string;
  team: string;
  matchesPlayed: number;
  goals: number;
  assists: number;
  minutesPlayed: number;
  yellowCards: number;
  redCards: number;
  shotsOnTarget: number;
  passAccuracy: number;
  formRating: number;
  injuryStatus: boolean;

  expectedGoals?: number;
  expectedAssists?: number;
  keyPasses?: number;
  progressivePasses?: number;
  dribblesCompleted?: number;
  tacklesWon?: number;
  interceptions?: number;
  ballRecoveries?: number;
  matchesMissed?: number;
  recentMatchLoad?: number;
}
