export interface PropertyField {
  fieldName: string;
  value: string;
  status: 'done' | 'pending';
  category: string;
}

export interface PropertyDetails {
  id: string;
  sessionId: string;
  originalFileName: string;
  fields: PropertyField[];
  createdAt: string;
  updatedAt: string;
}

export interface CategoryGroup {
  category: string;
  fields: PropertyField[];
  doneCount: number;
  pendingCount: number;
}
