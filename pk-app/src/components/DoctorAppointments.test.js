import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import DoctorAppointments from './DoctorAppointments';
import { MyUserContext } from '../config/MyContexts';
import { authApis, endpoints } from '../config/Apis';

const mockNavigate = jest.fn();

jest.mock('../config/Apis', () => ({
  authApis: jest.fn(),
  endpoints: {
    'my-appointments-doctor': '/doctors/my-appointments',
  },
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const doctorUser = { id: 10, role: 'DOCTOR', fullName: 'Dr Test' };

const renderDoctorAppointments = (user = doctorUser) => {
  return render(
    <MyUserContext.Provider value={[user, jest.fn()]}>
      <MemoryRouter>
        <DoctorAppointments />
      </MemoryRouter>
    </MyUserContext.Provider>
  );
};

describe('DoctorAppointments', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('loads appointments and hides completed ones', async () => {
    const getMock = jest.fn().mockResolvedValue({
      data: {
        status: 200,
        data: [
          {
            id: 1,
            patientName: 'Nguyen Van A',
            appointmentDate: '2026-05-14',
            appointmentTime: '08:30',
            status: 'CONFIRMED',
          },
          {
            id: 2,
            patientName: 'Tran Thi B',
            appointmentDate: '2026-05-14',
            appointmentTime: '09:00',
            status: 'COMPLETED',
          },
          {
            id: 3,
            patientName: 'Le Van C',
            appointmentDate: '2026-05-14',
            appointmentTime: '10:00',
            status: ' awaiting_deposit ',
          },
        ],
      },
    });
    authApis.mockReturnValue({ get: getMock });

    renderDoctorAppointments();

    expect(await screen.findByText('Nguyen Van A')).toBeInTheDocument();
    expect(screen.getByText('Le Van C')).toBeInTheDocument();
    expect(screen.queryByText('Tran Thi B')).not.toBeInTheDocument();
    expect(screen.getByText('Chờ đặt cọc')).toBeInTheDocument();
    expect(screen.getAllByText('Sẵn sàng khám')).toHaveLength(1);
    expect(getMock).toHaveBeenCalledWith(endpoints['my-appointments-doctor']);
  });

  test('navigates to medical record form with selected appointment', async () => {
    const appointment = {
      id: 4,
      patientName: 'Pham Van D',
      appointmentDate: '2026-05-14',
      appointmentTime: '11:00',
      status: 'CONFIRMED',
    };
    authApis.mockReturnValue({
      get: jest.fn().mockResolvedValue({
        data: {
          status: 200,
          data: [appointment],
        },
      }),
    });

    renderDoctorAppointments();

    fireEvent.click(await screen.findByRole('button', { name: /Khám bệnh/i }));

    expect(mockNavigate).toHaveBeenCalledWith('/doctor/create-medical-record', {
      state: { appointment },
    });
  });

  test('shows an error message when appointments cannot be loaded', async () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    authApis.mockReturnValue({
      get: jest.fn().mockRejectedValue(new Error('Network error')),
    });

    renderDoctorAppointments();

    await waitFor(() => {
      expect(screen.getByText('Lỗi kết nối hoặc phiên đăng nhập không hợp lệ.')).toBeInTheDocument();
    });
    expect(consoleErrorSpy).toHaveBeenCalled();
    consoleErrorSpy.mockRestore();
  });
});
