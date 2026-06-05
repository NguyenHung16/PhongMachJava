import { fireEvent, screen, waitFor } from '@testing-library/react';
import Register from './Register';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { apiPostMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';

const mockNavigate = jest.fn();

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const fillRegisterForm = (container, confirmPassword = 'secret123') => {
  fireEvent.change(container.querySelector('input[name="fullName"]'), {
    target: { value: 'Nguyen Van A' },
  });
  fireEvent.change(container.querySelector('input[name="username"]'), {
    target: { value: 'patient02' },
  });
  fireEvent.change(container.querySelector('input[name="email"]'), {
    target: { value: 'patient02@example.com' },
  });
  fireEvent.change(container.querySelector('input[name="password"]'), {
    target: { value: 'secret123' },
  });
  fireEvent.change(container.querySelector('input[name="confirmPassword"]'), {
    target: { value: confirmPassword },
  });
};

describe('Register', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('validates password confirmation before submitting', () => {
    const { container } = renderWithProviders(<Register />);

    fillRegisterForm(container, 'different');
    fireEvent.click(screen.getByRole('button', { name: /TẠO TÀI KHOẢN/i }));

    expect(screen.getByText(/Mật khẩu xác nhận không khớp/i)).toBeInTheDocument();
    expect(apiPostMock).not.toHaveBeenCalled();
  });

  test('submits multipart registration data and navigates to login', async () => {
    apiPostMock.mockResolvedValue({ data: { status: 200 } });
    const { container } = renderWithProviders(<Register />);

    fillRegisterForm(container);
    const file = new File(['avatar'], 'avatar.png', { type: 'image/png' });
    fireEvent.change(container.querySelector('input[type="file"]'), {
      target: { files: [file] },
    });
    fireEvent.click(screen.getByRole('button', { name: /TẠO TÀI KHOẢN/i }));

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/login');
    });
    const [, formData, config] = apiPostMock.mock.calls[0];
    expect(apiPostMock.mock.calls[0][0]).toBe(endpointMocks.register);
    expect(formData.get('fullName')).toBe('Nguyen Van A');
    expect(formData.get('avatar')).toBe(file);
    expect(config.headers['Content-Type']).toBe('multipart/form-data');
    expect(window.alert).toHaveBeenCalledWith('Đăng ký thành công!');
  });
});
