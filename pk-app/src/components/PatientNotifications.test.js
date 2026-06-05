import { render, screen } from '@testing-library/react';
import cookie from 'react-cookies';
import PatientNotifications from './PatientNotifications';
import { authGetMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { notifications } from '../test-utils/fixtures';

jest.mock('react-cookies', () => ({
  load: jest.fn(),
}));

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

describe('PatientNotifications', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('requires a token before loading notifications', async () => {
    cookie.load.mockReturnValue(null);

    render(<PatientNotifications />);

    expect(await screen.findByText(/Vui lòng đăng nhập để xem thông báo/i)).toBeInTheDocument();
    expect(authGetMock).not.toHaveBeenCalled();
  });

  test('renders notifications returned by the API', async () => {
    cookie.load.mockReturnValue('jwt-token');
    authGetMock.mockResolvedValue({ data: notifications });

    render(<PatientNotifications />);

    expect(await screen.findByText(/Lịch khám đã được xác nhận/i)).toBeInTheDocument();
    expect(authGetMock).toHaveBeenCalledWith(endpointMocks['my-notifications']);
  });

  test('shows empty state when API returns no notifications', async () => {
    cookie.load.mockReturnValue('jwt-token');
    authGetMock.mockResolvedValue({ data: [] });

    render(<PatientNotifications />);

    expect(await screen.findByText(/Bạn không có thông báo nào/i)).toBeInTheDocument();
  });
});
