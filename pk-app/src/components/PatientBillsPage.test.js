import { fireEvent, screen, waitFor } from '@testing-library/react';
import PatientBillsPage from './PatientBillsPage';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { authGetMock, authPostMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { bills, doctorUser, patientUser } from '../test-utils/fixtures';

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

describe('PatientBillsPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('guards access for non-patient users', async () => {
    renderWithProviders(<PatientBillsPage />, { user: doctorUser });

    expect(await screen.findByText(/Vui lòng đăng nhập với tài khoản bệnh nhân/i)).toBeInTheDocument();
    expect(authGetMock).not.toHaveBeenCalled();
  });

  test('renders paid and unpaid bills', async () => {
    authGetMock.mockResolvedValue({ data: { status: 200, data: bills } });

    renderWithProviders(<PatientBillsPage />, { user: patientUser });

    expect(await screen.findByText('#601')).toBeInTheDocument();
    expect(screen.getByText(/CHỜ THANH TOÁN/i)).toBeInTheDocument();
    expect(screen.getByText(/ĐÃ THANH TOÁN/i)).toBeInTheDocument();
  });

  test('pays an unpaid bill and updates status', async () => {
    authGetMock.mockResolvedValue({ data: { status: 200, data: bills } });
    authPostMock.mockResolvedValue({ data: { status: 200 } });

    renderWithProviders(<PatientBillsPage />, { user: patientUser });

    fireEvent.click(await screen.findByRole('button', { name: /Thanh toán/i }));

    await waitFor(() => {
      expect(authPostMock).toHaveBeenCalledWith(endpointMocks['pay-bill'](bills[0].id));
    });
    expect(window.alert).toHaveBeenCalledWith('Thanh toán thành công!');
    expect(screen.getAllByText(/ĐÃ THANH TOÁN/i).length).toBeGreaterThan(0);
  });
});
