export const patientUser = {
  id: 101,
  role: 'PATIENT',
  username: 'patient01',
  fullName: 'Nguyen Van Patient',
  email: 'patient@example.com',
  phoneNumber: '0909000001',
  gender: 'MALE',
  patientCode: 'BN001',
  avatar: '/uploads/patient.png',
};

export const doctorUser = {
  id: 201,
  role: 'DOCTOR',
  username: 'doctor01',
  fullName: 'Dr Nguyen',
  email: 'doctor@example.com',
  avatar: '/uploads/doctor.png',
};

export const doctors = [
  { id: 301, fullName: 'Dr Cardio', specialtyName: 'Tim mạch' },
  { id: 302, fullName: 'Dr Skin', specialtyName: 'Da liễu' },
];

export const specialties = [
  { id: 1, name: 'Tim mạch' },
  { id: 2, name: 'Da liễu' },
];

export const patientAppointments = [
  {
    id: 401,
    doctorName: 'Dr Cardio',
    specialtyName: 'Tim mạch',
    appointmentDate: '2026-05-20',
    appointmentTime: '08:30',
    status: 'AWAITING_DEPOSIT',
  },
  {
    id: 402,
    doctorName: 'Dr Skin',
    specialtyName: 'Da liễu',
    appointmentDate: '2026-05-21',
    appointmentTime: '09:00',
    status: 'CONFIRMED',
  },
];

export const doctorAppointments = [
  {
    id: 501,
    patientId: patientUser.id,
    patientName: patientUser.fullName,
    appointmentDate: '2026-05-20',
    appointmentTime: '08:30',
    status: 'CONFIRMED',
  },
];

export const bills = [
  { id: 601, billDate: '2026-05-10T00:00:00', totalAmount: 250000, paymentStatus: 'UNPAID' },
  { id: 602, billDate: '2026-05-11T00:00:00', totalAmount: 150000, paymentStatus: 'PAID' },
];

export const medicalRecords = [
  {
    id: 701,
    patientName: patientUser.fullName,
    createdDate: '2026-05-12T08:00:00',
    diagnosis: 'Viêm họng',
    symptoms: 'Ho và sốt',
    treatmentPlan: 'Nghỉ ngơi',
    prescriptionItems: [
      { medicineName: 'Paracetamol', quantity: 2, instruction: 'Sau ăn' },
    ],
  },
];

export const medicines = [
  { id: 801, name: 'Paracetamol', unit: 'viên', price: 5000 },
  { id: 802, name: 'Vitamin C', unit: 'viên', price: 3000 },
];

export const notifications = [
  { id: 901, message: 'Lịch khám đã được xác nhận', isRead: false, createdAt: '2026-05-13T09:00:00' },
];
