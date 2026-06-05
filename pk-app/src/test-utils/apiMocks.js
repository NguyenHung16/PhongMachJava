export const endpointMocks = {
  login: '/login',
  register: '/users',
  profile: '/profile',
  'update-profile': (userId) => `/users/${userId}`,
  'current-patient': '/patients/me',
  medicines: '/medicines',
  doctors: '/doctors',
  specialties: '/doctors/specialties',
  appointments: '/appointments',
  'my-appointments-patient': '/patients/my-appointments',
  'my-appointments-doctor': '/doctors/my-appointments',
  'doctor-patients': '/doctors/patients',
  'create-medical-record': '/medical-records',
  'medical-records-patient': (patientId) => `/patients/${patientId}/medical-records`,
  'my-bills': '/patients/my-bills',
  'patient-bills': (patientId) => `/patients/${patientId}/bills`,
  'my-notifications': '/notifications/my-notifications',
  'create-prescription': '/prescriptions/create',
  'pay-appointment-deposit': (appointmentId) => `/payments/appointments/${appointmentId}/deposit`,
  'pay-bill': (billId) => `/payments/bills/${billId}/pay`,
};

export const authGetMock = jest.fn();
export const authPostMock = jest.fn();
export const apiGetMock = jest.fn();
export const apiPostMock = jest.fn();

export const resetApiMocks = () => {
  authGetMock.mockReset();
  authPostMock.mockReset();
  apiGetMock.mockReset();
  apiPostMock.mockReset();
};

export const createApisMock = () => ({
  __esModule: true,
  default: {
    get: apiGetMock,
    post: apiPostMock,
  },
  authApis: () => ({
    get: authGetMock,
    post: authPostMock,
  }),
  endpoints: endpointMocks,
  getImageUrl: (path) => {
    if (!path || path === 'null') {
      return 'https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y';
    }
    return path.startsWith('http') ? path : `http://localhost:8080${path}`;
  },
});
