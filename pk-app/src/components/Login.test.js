import { fireEvent, screen, waitFor } from '@testing-library/react';
import cookie from 'react-cookies';
import Login from './Login';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { authGetMock, apiPostMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { patientUser } from '../test-utils/fixtures';

const mockNavigate = jest.fn();

jest.mock('react-cookies', () => ({
  save: jest.fn(),
}));

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const fillLoginForm = () => {
  fireEvent.change(screen.getByPlaceholderText(/Nhập tên đăng nhập/i), {
    target: { value: 'patient01' },
  });
  fireEvent.change(screen.getByPlaceholderText(/Nhập mật khẩu/i), {
    target: { value: 'secret' },
  });
};

describe('Login', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('logs in a patient, enriches profile, saves cookies and follows next query', async () => {
    const dispatch = jest.fn();
    apiPostMock.mockResolvedValue({
      data: {
        status: 200,
        data: {
          token: 'jwt-token',
          user: { id: 1, role: 'PATIENT', username: 'patient01' },
        },
      },
    });
    authGetMock.mockResolvedValue({
      data: {
        status: 200,
        data: patientUser,
      },
    });

    renderWithProviders(<Login />, { dispatch, route: '/login?next=/profile' });
    fillLoginForm();
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/profile');
    });
    expect(apiPostMock).toHaveBeenCalledWith(endpointMocks.login, {
      username: 'patient01',
      password: 'secret',
    });
    expect(authGetMock).toHaveBeenCalledWith(endpointMocks['current-patient']);
    expect(cookie.save).toHaveBeenCalledWith('token', 'jwt-token');
    expect(cookie.save).toHaveBeenCalledWith('user', expect.objectContaining({ patientCode: patientUser.patientCode }));
    expect(dispatch).toHaveBeenCalledWith({ type: 'login', payload: expect.objectContaining({ id: patientUser.id }) });
  });

  test('shows error message when login fails', async () => {
    jest.spyOn(console, 'error').mockImplementation(() => {});
    apiPostMock.mockRejectedValue(new Error('Invalid credentials'));

    renderWithProviders(<Login />);
    fillLoginForm();
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    expect(await screen.findByText(/Tên đăng nhập hoặc mật khẩu không chính xác/i)).toBeInTheDocument();
    expect(mockNavigate).not.toHaveBeenCalled();
    console.error.mockRestore();
  });
});
