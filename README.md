# 🏥 Hệ Thống Quản Lý Phòng Khám Đa Khoa QH (QH Clinic)

Hệ thống cung cấp giải pháp quản lý y tế toàn diện cho Bệnh nhân và Bác sĩ. Hỗ trợ đặt lịch online, quản lý hồ sơ bệnh án điện tử và thanh toán trực tuyến qua VNPAY.

---

## 📂 Cấu trúc dự án
*   **/App**: Mã nguồn Backend (Java Spring Boot, Hibernate).
*   **/pk-app**: Mã nguồn Frontend (ReactJS).

---

## ⚙️ Cấu hình Backend (Java Spring Boot)

### 1. Yêu cầu môi trường
*   Java JDK 17+.
*   MySQL 8.0+.
*   Apache Tomcat 9.0 (nếu deploy file .war) hoặc chạy trực tiếp bằng Maven.

### 2. Cấu hình Database
*   Tạo database trong MySQL: `CREATE DATABASE phongmachdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
*   Mở file: `App/src/main/resources/database.properties`
*   Cập nhật thông số:
    ```properties
    db.driver=com.mysql.cj.jdbc.Driver
    db.url=jdbc:mysql://localhost:3306/phongmachdb?useSSL=false
    db.username=root
    db.password=your_password
    ```

### 3. Cấu hình Cloudinary (Lưu trữ ảnh)
*   Đăng ký tài khoản tại [Cloudinary](https://cloudinary.com/).
*   Mở file cấu hình Spring (hoặc class cấu hình Cloudinary) và điền:
    *   `Cloud Name`, `API Key`, `API Secret`.

### 4. Cấu hình VNPAY (Thanh toán)
*   Sử dụng thông tin Test của [VNPAY Sandbox](https://sandbox.vnpayment.vn/).
*   Kiểm tra file: `App/src/main/java/com/vnh/utils/VNPayUtils.java`
*   Đảm bảo `vnp_TmnCode`, `vnp_HashSecret`, và `vnp_Url` đã được cấu hình đúng.

### 5. Cách khởi chạy
*   Mở terminal tại thư mục `/App`:
    ```sh
    mvn clean install
    mvn spring-boot:run
    ```
*   **Base URL:** `http://localhost:8080/App/api`

---

## 💻 Cấu hình Frontend (ReactJS)

### 1. Yêu cầu môi trường
*   Node.js 16.x hoặc 18.x.
*   npm (thường đi kèm Node.js).

### 2. Cài đặt thư viện
*   Mở terminal tại thư mục `/pk-app`:
    ```sh
    npm install
    ```

### 3. Cấu hình Endpoint API
*   Mở file: `pk-app/src/config/Apis.js`
*   Kiểm tra dòng `BASE_URL`:
    ```javascript
    const BASE_URL = "http://localhost:8080/App/api";
    ```
    *(Lưu ý: Nếu Backend chạy không có context path, hãy bỏ `/App`)*.

### 4. Cách khởi chạy
*   Tại thư mục `/pk-app`:
    ```sh
    npm start
    ```
*   **Địa chỉ truy cập:** `http://localhost:3000`

---

## 🛠 Luồng nghiệp vụ chuẩn (Để Team Test)

1.  **Đăng ký:** Tạo tài khoản Bệnh nhân (có tải ảnh đại diện).
2.  **Đặt lịch:** Bệnh nhân chọn Bác sĩ & Chuyên khoa -> Đặt lịch (Trạng thái: AWAITING_DEPOSIT).
3.  **Đặt cọc:** Bệnh nhân vào Dashboard -> Bấm **Đặt cọc** -> Hệ thống giả lập thành công (Trạng thái chuyển sang CONFIRMED).
4.  **Khám bệnh:** Bác sĩ đăng nhập -> Xem lịch khám trong ngày -> Bấm **Khám bệnh** -> Nhập triệu chứng, chẩn đoán -> Lưu hồ sơ.
5.  **Kê đơn:** Sau khi lưu hồ sơ, hệ thống chuyển sang **Kê đơn** -> Chọn thuốc có đơn giá -> Lưu đơn. Lịch hẹn chuyển sang COMPLETED và không còn hiển thị trong lịch làm việc.
6.  **Thanh toán sau khám:** Khi bác sĩ lưu đơn, hóa đơn cập nhật tổng tiền khám + thuốc. Bệnh nhân vào **Hóa đơn** -> Bấm **Thanh toán** -> Hệ thống lưu trạng thái PAID và admin thống kê doanh thu từ hóa đơn đã thanh toán.
7.  **Lịch sử khám:** Bác sĩ vào **Bệnh nhân** -> **Lịch sử khám** để xem chẩn đoán và đơn thuốc đã kê.
8.  **Kết quả:** Bệnh nhân vào **Hồ sơ sức khỏe** -> Xem chi tiết chẩn đoán và Đơn thuốc.

---

## ⚠️ Lưu ý quan trọng
*   **CORS:** Nếu FE không gọi được BE, hãy kiểm tra `@CrossOrigin` trong các Controller ở BE.
*   **Multipart:** Đảm bảo cấu hình `max-file-size` trong BE đủ lớn để upload ảnh.
*   **Token:** Token JWT được lưu trong Cookie (`token`), hãy đảm bảo không xóa cookie khi đang thao tác.

---
*Chúc Team phát triển dự án thành công!*
