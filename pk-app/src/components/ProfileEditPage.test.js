import { fireEvent, screen, waitFor } from '@testing-library/react';
import cookie from 'react-cookies';
import ProfileEditPage from './ProfileEditPage';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { authPostMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { patientUser } from '../test-utils/fixtures';

jest.mock('react-cookies', () => ({
  save: jest.fn(),
}));

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

describe('ProfileEditPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('requires a logged-in user', () => {
    renderWithProviders(<ProfileEditPage />);

    expect(screen.getByText(/Vui lòng đăng nhập/i)).toBeInTheDocument();
  });

  test('updates profile data and stores returned user', async () => {
    const dispatch = jest.fn();
    authPostMock.mockResolvedValue({
      data: {
        status: 200,
        data: { fullName: 'Nguyen Van Updated', phoneNumber: '0988000000' },
      },
    });
    const { container } = renderWithProviders(<ProfileEditPage />, { user: patientUser, dispatch });

    fireEvent.change(container.querySelector('input[name="fullName"]'), {
      target: { value: 'Nguyen Van Updated' },
    });
    fireEvent.change(container.querySelector('input[name="phoneNumber"]'), {
      target: { value: '0988000000' },
    });
    const file = new File(['avatar'], 'new-avatar.png', { type: 'image/png' });
    fireEvent.change(container.querySelector('#avatar-upload'), {
      target: { files: [file] },
    });
    fireEvent.click(screen.getByRole('button', { name: /LƯU THAY ĐỔI/i }));

    expect(await screen.findByText(/Cập nhật hồ sơ thành công/i)).toBeInTheDocument();
    const [url, formData] = authPostMock.mock.calls[0];
    expect(url).toBe(endpointMocks['update-profile'](patientUser.id));
    expect(formData.get('fullName')).toBe('Nguyen Van Updated');
    expect(formData.get('avatar')).toBe(file);
    expect(cookie.save).toHaveBeenCalledWith('user', expect.objectContaining({ fullName: 'Nguyen Van Updated' }));
    expect(dispatch).toHaveBeenCalledWith({ type: 'login', payload: expect.objectContaining({ phoneNumber: '0988000000' }) });
  });

  test('shows an error when the user id is missing', async () => {
    renderWithProviders(<ProfileEditPage />, { user: { ...patientUser, id: undefined } });

    fireEvent.click(screen.getByRole('button', { name: /LƯU THAY ĐỔI/i }));

    await waitFor(() => {
      expect(screen.getByText(/Không tìm thấy ID người dùng/i)).toBeInTheDocument();
    });
    expect(authPostMock).not.toHaveBeenCalled();
  });
});
