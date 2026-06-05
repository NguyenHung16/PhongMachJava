# Tài Liệu Kiểm Thử Hệ Thống Phòng Khám QH

## 1. Mục Tiêu

Tài liệu này dùng để in và nộp kèm phần kiểm thử tự động của hệ thống phòng khám QH.

Phạm vi kiểm thử:

- Backend Java trong `App`.
- Frontend React trong `pk-app`.
- Các luồng chính: đăng nhập, đăng ký, đặt lịch, đặt cọc, khám bệnh, lập hồ sơ, kê đơn, thanh toán, xem hồ sơ và thông báo.

## 2. Công Cụ

| Phần hệ thống | Công cụ | Mục đích |
| --- | --- | --- |
| Backend | JUnit 5 | Unit test và controller test |
| Backend | Mockito | Mock service, repository và dependency |
| Backend | JaCoCo | Đo line/branch coverage |
| Frontend | Jest | Chạy test React |
| Frontend | React Testing Library | Test hành vi người dùng trên UI |

## 3. Backend Test Và JaCoCo

### Phạm Vi JaCoCo

JaCoCo trong `App/pom.xml` đang đo các package:

- `com/vnh/controllers/**/*`
- `com/vnh/repositories/**/*`
- `com/vnh/services/**/*`
- `com/vnh/utils/**/*`

### Coverage Gate

- Line coverage tối thiểu: `80%`
- Branch coverage tối thiểu: `80%`

Nếu coverage thấp hơn ngưỡng này, lệnh `verify` sẽ thất bại.

### Cách Chạy Backend Test

Chạy tại thư mục `App`:

```bash
.\mvnw.cmd clean verify
```

Báo cáo JaCoCo:

```text
App/target/site/jacoco/index.html
```

Kết quả kiểm thử backend gần nhất:

- Tests: `161 passed`
- Failures: `0`
- Errors: `0`
- Skipped: `0`
- Line coverage: khoảng `95%`
- Branch coverage: khoảng `82%`
- JaCoCo gate: passed với `80%` line và `80%` branch

## 4. Frontend Test Và Jest Coverage

### Phạm Vi Test Frontend

Frontend đã có test cho:

- App shell: `App`, `Home`, `Header`, `Footer`, `MySpinner`.
- Auth/account: `Login`, `Register`, `ProfileEditPage`.
- Patient flow: `BookingPage`, `PatientDashboard`, `PatientBillsPage`, `MedicalRecordsPage`, `PatientNotifications`, `AppointmentsPage`.
- Doctor flow: `DoctorAppointments`, `DoctorPatients`, `MedicalRecordForm`, `PrescriptionPage`.
- Unit: `Apis`, `MyUserReducer`.

### Cách Chạy Frontend Test

Chạy tại thư mục `pk-app`:

```bash
npm run test:coverage
```

Báo cáo Jest coverage:

```text
pk-app/coverage/lcov-report/index.html
```

Kết quả kiểm thử frontend gần nhất:

- Test suites: `19 passed`
- Tests: `51 passed`
- Statements coverage: `86.85%`
- Lines coverage: `87.14%`

## 5. Checklist Nghiệp Vụ Đã Kiểm Thử

| Luồng nghiệp vụ | Backend | Frontend |
| --- | --- | --- |
| Đăng nhập | Có | Có |
| Đăng ký | Có | Có |
| Đặt lịch khám | Có | Có |
| Xem lịch hẹn | Có | Có |
| Đặt cọc lịch khám | Có | Có |
| Xem lịch bác sĩ | Có | Có |
| Lập hồ sơ bệnh án | Có | Có |
| Kê đơn thuốc | Có | Có |
| Thanh toán hóa đơn | Có | Có |
| Xem hồ sơ sức khỏe | Có | Có |
| Xem thông báo | Có | Có |

## 6. Cách In Tài Liệu

Mở file sau bằng IDE hoặc trình xem Markdown rồi chọn Print:

```text
docs/TEST_DOCUMENTATION.md
```
