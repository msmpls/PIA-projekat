export interface User {
  id: number;
  username: string;
  email: string;
  first_name: string;
  last_name: string;
  phone: string;
  role: string;
  status: string;
  profile_picture_path: string;
  favorite_sports: string;
}

export interface AdminUserResponse {
  id: number;
  username: string;
  email: string;
  first_name: string;
  last_name: string;
  phone: string;
  role: string;
  status: string;
  profile_picture_path: string;
  favorite_sports: string;
}

// CAMELCASE - jedini izuzetak od konvencije!
export interface EmployeeFacilityDTO {
  id: number;
  name: string;
  city: string;
  address: string;
  description: string;
  pricePerHour: number;
  type: string;
  status: string;
  companyName: string;
  companyAddress: string;
  companyIdNumber: string;
  pib: string;
}

// CAMELCASE
export interface EmployeeProfileResponse {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: string;
  status: string;
  profilePicturePath: string;
  myFacilities: EmployeeFacilityDTO[];
}