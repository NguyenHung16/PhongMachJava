import { render, screen } from '@testing-library/react';
import cookie from 'react-cookies';
import AppointmentsPage from './AppointmentsPage';
import { authGetMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { patientAppointments, patientUser } from '../test-utils/fixtures';

jest.mock('react-cookies', () => ({
  load: jest.fn(),
}));

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

describe('AppointmentsPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('blocks non-patient users', async () => {
    cookie.load.mockReturnValue(null);

    render(<AppointmentsPage />);

    expect(await screen.findByText(/Bạn không có quyền truy cập/i)).toBeInTheDocument();
    expect(authGetMock).not.toHaveBeenCalled();
  });

  test('renders appointments returned as an array', async () => {
    cookie.load.mockReturnValue(patientUser);
    authGetMock.mockResolvedValue({ data: patientAppointments });

    render(<AppointmentsPage />);

    expect(await screen.findByText(/Lịch hẹn với Bác sĩ Dr Cardio/i)).toBeInTheDocument();
    expect(screen.getByText(/08:30/i)).toBeInTheDocument();
    expect(authGetMock).toHaveBeenCalledWith(endpointMocks['my-appointments-patient']);
  });

  test('shows empty state when appointment list is empty', async () => {
    cookie.load.mockReturnValue(patientUser);
    authGetMock.mockResolvedValue({ data: [] });

    render(<AppointmentsPage />);

    expect(await screen.findByText(/Bạn chưa có lịch hẹn nào/i)).toBeInTheDocument();
  });
});
