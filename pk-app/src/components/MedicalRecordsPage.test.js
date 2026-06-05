import { fireEvent, screen } from '@testing-library/react';
import MedicalRecordsPage from './MedicalRecordsPage';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { authGetMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { medicalRecords, patientUser } from '../test-utils/fixtures';

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

describe('MedicalRecordsPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('renders patient medical records and opens detail modal', async () => {
    authGetMock.mockResolvedValue({ data: { status: 200, data: medicalRecords } });

    renderWithProviders(<MedicalRecordsPage />, { user: patientUser });

    expect(await screen.findByText(/Viêm họng/i)).toBeInTheDocument();
    expect(authGetMock).toHaveBeenCalledWith(endpointMocks['medical-records-patient'](patientUser.id));
    fireEvent.click(screen.getByRole('button', { name: /XEM CHI TIẾT/i }));

    expect(screen.getByText(/KẾT QUẢ KHÁM CHI TIẾT/i)).toBeInTheDocument();
    expect(screen.getByText(/Ho và sốt/i)).toBeInTheDocument();
    expect(screen.getByText(/Paracetamol/i)).toBeInTheDocument();
  });

  test('shows empty state when no records exist', async () => {
    authGetMock.mockResolvedValue({ data: { status: 200, data: [] } });

    renderWithProviders(<MedicalRecordsPage />, { user: patientUser });

    expect(await screen.findByText(/Bạn chưa có hồ sơ khám bệnh nào/i)).toBeInTheDocument();
  });
});
