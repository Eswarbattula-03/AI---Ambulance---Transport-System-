export type Role = 'PATIENT' | 'AMBULANCE_DRIVER' | 'HOSPITAL_ADMIN' | 'SYSTEM_ADMIN';

export type TriagePriority = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';

export type EmergencyStatus =
  | 'REQUESTED'
  | 'ACCEPTED'
  | 'ARRIVED'
  | 'IN_TRANSIT'
  | 'COMPLETED'
  | 'TRIAGED'
  | 'DISPATCHED'
  | 'EN_ROUTE_TO_PICKUP'
  | 'PATIENT_ONBOARD'
  | 'EN_ROUTE_TO_HOSPITAL'
  | 'DELIVERED'
  | 'CANCELLED'
  | 'FAILED';

export interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: Role;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  id: number;
  name: string;
  email: string;
  role: Role;
}

export interface EmergencyResponse {
  id: number;
  patientId: number;
  patientName: string;
  patientPhone: string;
  ambulanceId?: number;
  vehicleNumber?: string;
  driverName?: string;
  driverPhone?: string;
  ambulanceLat?: number;
  ambulanceLng?: number;
  hospitalId?: number;
  hospitalName?: string;
  hospitalAddress?: string;
  hospitalContact?: string;
  destinationLat?: number;
  destinationLng?: number;
  triagePriority: TriagePriority;
  triageScore: number;
  triageExplanation?: string;
  status: EmergencyStatus;
  symptoms: string;
  pickupLat: number;
  pickupLng: number;
  pickupAddress?: string;
  createdAt: string;
  acceptedAt?: string;
  pickupAt?: string;
  deliveredAt?: string;
}

export interface Hospital {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  totalBeds: number;
  availableBeds: number;
  icuBeds: number;
  availableIcuBeds: number;
  contact: string;
  address?: string;
}
