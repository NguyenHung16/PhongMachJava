import { fireEvent, screen } from '@testing-library/react';
import DoctorPatients from './DoctorPatients';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { authGetMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { medicalRecords, patientUser } from '../test-utils/fixtures';

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

describe('DoctorPatients', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('renders assigned patients and opens medical history modal', async () => {
    authGetMock
      .mockResolvedValueOnce({ data: { status: 200, data: [patientUser] } })
      .mockResolvedValueOnce({ data: { status: 200, data: medicalRecords } });

    renderWithProviders(<DoctorPatients />);

    expect(await screen.findByText(patientUser.fullName)).toBeInTheDocument();
    expect(screen.getByText(patientUser.patientCode)).toBeInTheDocument();
    expect(authGetMock).toHaveBeenCalledWith(endpointMocks['doctor-patients']);

    fireEvent.click(screen.getByRole('button', { name: /Lịch sử khám/i }));

    expect(await screen.findByText(`Lịch sử bệnh án: ${patientUser.fullName}`)).toBeInTheDocument();
    expect(await screen.findByText(/Viêm họng/i)).toBeInTheDocument();
    expect(screen.getByText(/Paracetamol - SL: 2/i)).toBeInTheDocument();
  });

  test('shows an error when patient list cannot be loaded', async () => {
    authGetMock.mockRejectedValue(new Error('Network error'));

    renderWithProviders(<DoctorPatients />);

    expect(await screen.findByText(/Không thể tải danh sách bệnh nhân/i)).toBeInTheDocument();
  });
});
