import { PropertyField } from './property-details.model';

export interface AnalyzeResponse {
  sessionId: string;
  propertyDetailsId: string;
  fields: PropertyField[];
  summary: string;
}
