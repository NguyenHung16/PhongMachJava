import { fireEvent, screen, waitFor } from '@testing-library/react';
import BookingPage from './BookingPage';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { apiGetMock, authGetMock, authPostMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { doctors, patientUser, specialties } from '../test-utils/fixtures';

const mockNavigate = jest.fn();

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('BookingPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
    jest.useRealTimers();
    authGetMock.mockResolvedValue({ data: { status: 200, data: patientUser } });
    apiGetMock.mockImplementation((url) => {
      if (url === endpointMocks.specialties) {
        return Promise.resolve({ data: { status: 200, data: specialties } });
      }
      return Promise.resolve({ data: { status: 200, data: doctors } });
    });
  });

  test('loads patient info, specialties, and doctors', async () => {
    renderWithProviders(<BookingPage />, { user: patientUser });

    expect(await screen.findByText(/Bệnh nhân: Nguyen Van Patient/i)).toBeInTheDocument();
    expect(screen.getByText('Tim mạch')).toBeInTheDocument();
    expect(screen.getByText('Dr Cardio')).toBeInTheDocument();
  });

  test('submits booking payload and navigates back to profile after success', async () => {
    jest.useFakeTimers();
    authPostMock.mockResolvedValue({ data: { status: 200 } });
    const { container } = renderWithProviders(<BookingPage />, { user: patientUser });

    await screen.findByText('Dr Cardio');
    const selects = container.querySelectorAll('select');
    fireEvent.change(selects[1], { target: { value: doctors[0].id.toString() } });
    fireEvent.change(container.querySelector('input[type="date"]'), {
      target: { value: '2026-05-20' },
    });
    fireEvent.change(container.querySelector('input[type="time"]'), {
      target: { value: '08:30' },
    });
    fireEvent.change(screen.getByPlaceholderText(/Mô tả triệu chứng/i), {
      target: { value: 'Đau đầu' },
    });
    fireEvent.click(screen.getByRole('button', { name: /XÁC NHẬN ĐẶT LỊCH KHÁM/i }));

    expect(await screen.findByText(/Đặt lịch thành công/i)).toBeInTheDocument();
    expect(authPostMock).toHaveBeenCalledWith(endpointMocks.appointments, {
      doctorId: doctors[0].id,
      patientId: patientUser.id,
      appointmentDate: '2026-05-20',
      appointmentTime: '08:30',
      reason: 'Đau đầu',
    });
    jest.advanceTimersByTime(2000);
    expect(mockNavigate).toHaveBeenCalledWith('/profile');
  });

  test('filters doctors by selected specialty', async () => {
    const { container } = renderWithProviders(<BookingPage />, { user: patientUser });

    await screen.findByText('Dr Cardio');
    fireEvent.change(container.querySelectorAll('select')[0], { target: { value: '1' } });

    await waitFor(() => {
      expect(apiGetMock).toHaveBeenCalledWith(`${endpointMocks.doctors}?specialtyId=1`);
    });
  });
});
