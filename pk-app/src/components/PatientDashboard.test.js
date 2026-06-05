import { fireEvent, screen, waitFor } from '@testing-library/react';
import PatientDashboard from './PatientDashboard';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { authGetMock, authPostMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { patientAppointments, patientUser } from '../test-utils/fixtures';

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

describe('PatientDashboard', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('renders upcoming appointments for the patient', async () => {
    authGetMock.mockResolvedValue({ data: { status: 200, data: patientAppointments } });

    renderWithProviders(<PatientDashboard />, { user: patientUser });

    expect(await screen.findByText('Dr Cardio')).toBeInTheDocument();
    expect(screen.getByText('Dr Skin')).toBeInTheDocument();
    expect(screen.getByText(/Chờ đặt cọc/i)).toBeInTheDocument();
  });

  test('pays appointment deposit and updates local status', async () => {
    authGetMock.mockResolvedValue({ data: { status: 200, data: patientAppointments } });
    authPostMock.mockResolvedValue({ data: { status: 200 } });

    renderWithProviders(<PatientDashboard />, { user: patientUser });

    fireEvent.click(await screen.findByRole('button', { name: /ĐẶT CỌC/i }));

    await waitFor(() => {
      expect(authPostMock).toHaveBeenCalledWith(endpointMocks['pay-appointment-deposit'](patientAppointments[0].id));
    });
    expect(window.alert).toHaveBeenCalledWith('Đặt cọc thành công!');
    expect(screen.getAllByText(/Đã đặt cọc/i).length).toBeGreaterThan(0);
  });

  test('shows empty state when there are no appointments', async () => {
    authGetMock.mockResolvedValue({ data: { status: 200, data: [] } });

    renderWithProviders(<PatientDashboard />, { user: patientUser });

    expect(await screen.findByText(/Bạn chưa có lịch hẹn nào sắp tới/i)).toBeInTheDocument();
  });
});
