import { render, screen } from '@testing-library/react';
import cookie from 'react-cookies';
import App from './App';
import { doctorUser, patientUser } from './test-utils/fixtures';

jest.mock('react-cookies', () => ({
  load: jest.fn(() => null),
  remove: jest.fn(),
}));

jest.mock('./config/Apis', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
  },
  authApis: jest.fn(),
  endpoints: {},
  getImageUrl: jest.fn(() => 'https://www.gravatar.com/avatar/default?d=mp&f=y'),
}));

test('renders public home page for guests', () => {
  cookie.load.mockReturnValue(null);

  render(<App />);

  expect(screen.getAllByText(/PHÒNG KHÁM QH/i).length).toBeGreaterThan(0);
  expect(screen.getByText(/Sức khỏe của bạn là sứ mệnh của chúng tôi/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /Đăng nhập/i })).toBeInTheDocument();
});

test('renders patient navigation when a patient is logged in', () => {
  cookie.load.mockImplementation((key) => (key === 'user' ? patientUser : null));

  render(<App />);

  expect(screen.getByText(/Đặt lịch khám/i)).toBeInTheDocument();
  expect(screen.getAllByText(/Hồ sơ bệnh án/i).length).toBeGreaterThan(0);
  expect(screen.getByText(patientUser.fullName)).toBeInTheDocument();
});

test('renders doctor navigation when a doctor is logged in', () => {
  cookie.load.mockImplementation((key) => (key === 'user' ? doctorUser : null));

  render(<App />);

  expect(screen.getByText(/Lịch làm việc/i)).toBeInTheDocument();
  expect(screen.getByText(/Bệnh nhân/i)).toBeInTheDocument();
  expect(screen.getByText(doctorUser.fullName)).toBeInTheDocument();
});
