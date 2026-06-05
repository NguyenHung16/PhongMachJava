import { fireEvent, screen, waitFor } from '@testing-library/react';
import PrescriptionPage from './PrescriptionPage';
import { renderWithProviders } from '../test-utils/renderWithProviders';
import { authGetMock, authPostMock, endpointMocks, resetApiMocks } from '../test-utils/apiMocks';
import { medicines, medicalRecords, patientUser } from '../test-utils/fixtures';

const mockNavigate = jest.fn();

jest.mock('../config/Apis', () => require('../test-utils/apiMocks').createApisMock());

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const renderPrescriptionPage = () => {
  authGetMock
    .mockResolvedValueOnce({ data: { status: 200, data: medicines } })
    .mockResolvedValueOnce({ data: { status: 200, data: medicalRecords } });

  return renderWithProviders(<PrescriptionPage />, {
    route: '/prescriptions',
    routerState: {
      patientId: patientUser.id,
      medicalRecordId: medicalRecords[0].id,
      patientName: patientUser.fullName,
    },
  });
};

describe('PrescriptionPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetApiMocks();
  });

  test('loads medicines and medical records for selected patient', async () => {
    renderPrescriptionPage();

    expect(await screen.findByText(/Bệnh nhân: Nguyen Van Patient/i)).toBeInTheDocument();
    expect(screen.getByText(/Paracetamol/i)).toBeInTheDocument();
    expect(screen.getByText(/Ngày khám/i)).toBeInTheDocument();
  });

  test('validates save when no prescription item has been added', async () => {
    renderPrescriptionPage();

    await screen.findByText(/Paracetamol/i);
    fireEvent.click(screen.getByText(/Chưa có thuốc/i).closest('.card').querySelector('button') || screen.getByText(/Chưa có thuốc/i));

    // The save button is intentionally unavailable while the prescription is empty.
    expect(screen.queryByRole('button', { name: /Xác nhận & lưu đơn thuốc/i })).not.toBeInTheDocument();
  });

  test('adds medicine, removes it, then saves a valid prescription', async () => {
    authPostMock.mockResolvedValue({ status: 201 });
    const { container } = renderPrescriptionPage();

    await screen.findByText(/Paracetamol/i);
    fireEvent.change(container.querySelectorAll('select')[1], {
      target: { value: medicines[0].id.toString() },
    });
    fireEvent.change(screen.getByPlaceholderText(/Số lượng/i), {
      target: { value: '2' },
    });
    fireEvent.change(screen.getByPlaceholderText(/Tần suất/i), {
      target: { value: '2 lần/ngày' },
    });
    fireEvent.change(screen.getByPlaceholderText(/Hướng dẫn sử dụng/i), {
      target: { value: 'Sau ăn' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Thêm vào đơn/i }));

    expect(await screen.findByText('Paracetamol')).toBeInTheDocument();
    fireEvent.click(container.querySelector('button.text-danger'));
    expect(screen.getByText(/Chưa có thuốc/i)).toBeInTheDocument();

    fireEvent.change(container.querySelectorAll('select')[1], {
      target: { value: medicines[0].id.toString() },
    });
    fireEvent.change(screen.getByPlaceholderText(/Số lượng/i), {
      target: { value: '2' },
    });
    fireEvent.change(screen.getByPlaceholderText(/Tần suất/i), {
      target: { value: '2 lần/ngày' },
    });
    fireEvent.change(screen.getByPlaceholderText(/Hướng dẫn sử dụng/i), {
      target: { value: 'Sau ăn' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Thêm vào đơn/i }));
    fireEvent.click(await screen.findByRole('button', { name: /Xác nhận & lưu đơn thuốc/i }));

    await waitFor(() => {
      expect(authPostMock).toHaveBeenCalledWith(endpointMocks['create-prescription'], {
        medicalRecordId: medicalRecords[0].id,
        medicines: [
          {
            medicineId: medicines[0].id,
            dosage: '2',
            frequency: '2 lần/ngày',
            instructions: 'Sau ăn',
          },
        ],
      });
    });
    expect(window.alert).toHaveBeenCalledWith('Kê đơn thành công!');
    expect(mockNavigate).toHaveBeenCalledWith('/doctors/my-appointments');
  });
});
