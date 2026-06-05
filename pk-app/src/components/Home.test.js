import { screen } from '@testing-library/react';
import Home from './Home';
import { renderWithProviders } from '../test-utils/renderWithProviders';

test('renders home page hero and service highlights', () => {
  renderWithProviders(<Home />);

  expect(screen.getByText(/Sức khỏe của bạn là sứ mệnh của chúng tôi/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /Đặt lịch ngay/i })).toBeInTheDocument();
  expect(screen.getByText(/Đặt lịch trực tuyến/i)).toBeInTheDocument();
  expect(screen.getByText(/Thanh toán linh hoạt/i)).toBeInTheDocument();
});
