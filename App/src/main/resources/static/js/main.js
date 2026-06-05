function deleteMedicine(endpoint) {
    if (confirm("Bạn chắc chắn xóa?") === true) {
        // Lấy CSRF token từ meta tags một cách an toàn
        const csrfTokenElement = document.querySelector("meta[name='_csrf']");
        const csrfHeaderElement = document.querySelector("meta[name='_csrf_header']");
        
        // Kiểm tra xem các phần tử có tồn tại không trước khi truy cập
        if (!csrfTokenElement || !csrfHeaderElement) {
            console.error("Lỗi: Không tìm thấy CSRF token trong HTML.");
            alert("Đã xảy ra lỗi bảo mật. Vui lòng thử lại.");
            return; // Dừng hàm nếu không tìm thấy token
        }

        const token = csrfTokenElement.getAttribute("content");
        const header = csrfHeaderElement.getAttribute("content");

        // Lấy JWT token đã lưu từ localStorage
        const jwtToken = localStorage.getItem("jwtToken"); // GIẢ SỬ BẠN LƯU TOKEN VỚI KEY 'jwtToken'

        // Tạo đối tượng headers, bao gồm cả CSRF và JWT
        const headers = {
            [header]: token,
            // Thêm JWT token vào header 'Authorization'
            'Authorization': `Bearer ${jwtToken}`
        };

        fetch(endpoint, {
            method: "DELETE",
            headers: headers
        }).then(res => {
            if (res.status === 204) {
                // Xóa hàng trong bảng thay vì tải lại toàn bộ trang
                const row = document.querySelector(`[onclick*="${endpoint}"]`).closest('tr');
                if (row) {
                    row.remove();
                }
                alert("Xóa thành công!");
            } else {
                alert("Hệ thống đang có lỗi!");
            }
        }).catch(err => {
            console.error("Lỗi khi gửi yêu cầu:", err);
            alert("Đã xảy ra lỗi không mong muốn!");
        });
    }
}