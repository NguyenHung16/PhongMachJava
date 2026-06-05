import { fireEvent, screen } from '@testing-library/react';
import cookie from 'react-cookies';
import Header from './Header';
import { renderWithProviders } from '../../test-utils/renderWithProviders';
import { doctorUser, patientUser } from '../../test-utils/fixtures';

const mockNavigate = jest.fn();

jest.mock('react-cookies', () => ({
  remove: jest.fn(),
}));

jest.mock('../../config/Apis', () => require('../../test-utils/apiMocks').createApisMock());

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('Header', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('shows login and register links for guests', () => {
    renderWithProviders(<Header />);

    expect(screen.getByRole('button', { name: /Đăng nhập/i })).toHaveAttribute('href', '/login');
    expect(screen.getByRole('button', { name: /Đăng ký/i })).toHaveAttribute('href', '/register');
  });

  test('shows patient navigation links', () => {
    renderWithProviders(<Header />, { user: patientUser });

    expect(screen.getByText(/Đặt lịch khám/i)).toBeInTheDocument();
    expect(screen.getByText(/Hồ sơ bệnh án/i)).toBeInTheDocument();
    expect(screen.getByText(/Hóa đơn/i)).toBeInTheDocument();
  });

  test('shows doctor navigation links', () => {
    renderWithProviders(<Header />, { user: doctorUser });

    expect(screen.getByText(/Lịch làm việc/i)).toBeInTheDocument();
    expect(screen.getByText(/Bệnh nhân/i)).toBeInTheDocument();
    expect(screen.getByText(/Nghiệp vụ/i)).toBeInTheDocument();
  });

  test('logs out by clearing cookies and dispatching logout', () => {
    const dispatch = jest.fn();
    renderWithProviders(<Header />, { user: patientUser, dispatch });

    fireEvent.click(screen.getByText(patientUser.fullName));
    fireEvent.click(screen.getByText(/Đăng xuất/i));

    expect(cookie.remove).toHaveBeenCalledWith('token');
    expect(cookie.remove).toHaveBeenCalledWith('user');
    expect(dispatch).toHaveBeenCalledWith({ type: 'logout' });
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });
});
