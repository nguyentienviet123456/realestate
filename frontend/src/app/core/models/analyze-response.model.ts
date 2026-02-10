export interface ExtractResponse {
  sessionId: string;
  status: 'processing' | 'completed' | 'failed';
}
