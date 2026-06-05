import { fireEvent, screen, waitFor } from '@testing-library/react';
import MedicalRecordForm from './MedicalRecordForm';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { authGetMock, authPostMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { doctorAppointments, patientUser } from '../test-utils/fixtures';

const mockNavigate = jest.fn();

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('MedicalRecordForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
    jest.useRealTimers();
  });

  test('loads confirmed appointments into the appointment selector', async () => {
    authGetMock
      .mockResolvedValueOnce({
        data: {
          status: 200,
          data: [
            ...doctorAppointments,
            { id: 502, patientName: 'Completed Patient', status: 'COMPLETED' },
          ],
        },
      })
      .mockResolvedValueOnce({ data: { status: 200, data: [patientUser] } });

    renderWithProviders(<MedicalRecordForm />);

    expect(await screen.findByText(/Nguyen Van Patient/i)).toBeInTheDocument();
    expect(screen.queryByText(/Completed Patient/i)).not.toBeInTheDocument();
  });

  test('creates a medical record from selected schedule appointment and navigates to prescription', async () => {
    authGetMock
      .mockResolvedValueOnce({ data: { status: 200, data: doctorAppointments } })
      .mockResolvedValueOnce({ data: { status: 200, data: [patientUser] } });
    authPostMock.mockResolvedValue({
      data: { status: 200, data: { id: 999 } },
    });
    const appointment = doctorAppointments[0];

    renderWithProviders(<MedicalRecordForm />, {
      route: '/doctor/create-medical-record',
      routerState: { appointment },
    });

    expect(await screen.findByText(appointment.patientName)).toBeInTheDocument();
    fireEvent.change(screen.getByPlaceholderText(/Mô tả triệu chứng/i), {
      target: { value: 'Sốt cao' },
    });
    fireEvent.change(screen.getByPlaceholderText(/Nhập kết luận bệnh lý/i), {
      target: { value: 'Cảm cúm' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Lưu hồ sơ & sang kê đơn/i }));

    await waitFor(() => {
      expect(authPostMock).toHaveBeenCalledWith(endpointMocks['create-medical-record'], {
        patientId: appointment.patientId,
        appointmentId: appointment.id,
        diagnosis: 'Cảm cúm',
        symptoms: 'Sốt cao',
      });
    });
    expect(await screen.findByText(/Tạo hồ sơ bệnh án thành công/i)).toBeInTheDocument();
    await waitFor(
      () => {
        expect(mockNavigate).toHaveBeenCalledWith('/prescriptions', {
          state: {
            patientId: appointment.patientId,
            medicalRecordId: 999,
            patientName: appointment.patientName,
          },
        });
      },
      { timeout: 1500 }
    );
  });
});
