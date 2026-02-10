export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  type: 'text' | 'pdf_upload' | 'analysis_result';
  timestamp: string;
}

export interface ChatSession {
  id: string;
  title: string;
  messages: ChatMessage[];
  propertyDetailsId: string;
  createdAt: string;
  updatedAt: string;
}

export interface ChatSessionSummary {
  id: string;
  title: string;
  createdAt: string;
  updatedAt: string;
}
