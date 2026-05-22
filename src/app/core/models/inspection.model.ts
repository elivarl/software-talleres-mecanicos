export interface InspectionPhoto {
  id: number;
  photoUrl: string;
  description?: string;
  createdAt?: string;
}

export interface Inspection {
  id: number;
  workOrderId: number;
  workOrderCode: string;
  mileage: number;
  fuelLevel?: string;
  exteriorCondition?: string;
  visibleScratches?: string;
  visibleDents?: string;
  lightsWorking?: boolean;
  tiresCondition?: string;
  mirrorsCondition?: string;
  hasSpareTire?: boolean;
  hasJack?: boolean;
  hasTools?: boolean;
  hasDocuments?: boolean;
  personalItemsNotes?: string;
  generalNotes?: string;
  createdAt?: string;
  updatedAt?: string;
  photos: InspectionPhoto[];
}

export interface InspectionUpsertRequest {
  mileage: number;
  fuelLevel?: string;
  exteriorCondition?: string;
  visibleScratches?: string;
  visibleDents?: string;
  lightsWorking?: boolean;
  tiresCondition?: string;
  mirrorsCondition?: string;
  hasSpareTire?: boolean;
  hasJack?: boolean;
  hasTools?: boolean;
  hasDocuments?: boolean;
  personalItemsNotes?: string;
  generalNotes?: string;
}

export interface AddInspectionPhotoRequest {
  photoUrl: string;
  description?: string;
}
