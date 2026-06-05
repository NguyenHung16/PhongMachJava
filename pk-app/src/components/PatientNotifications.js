import React, { useState, useEffect } from 'react';
import { Alert, Container, Spinner, ListGroup } from 'react-bootstrap';
import { authApis, endpoints } from '../config/Apis';
import cookie from 'react-cookies';

const PatientNotifications = () => {
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                const token = cookie.load('token');
                if (!token) {
                    setError("Vui lòng đăng nhập để xem thông báo.");
                    setLoading(false);
                    return;
                }

                const api = authApis();
                const res = await api.get(endpoints['my-notifications']);
                setNotifications(Array.isArray(res.data) ? res.data : []);
            } catch (err) {
                console.error("Lỗi khi tải thông báo:", err);
                if (err.response && (err.response.status === 401 || err.response.status === 403)) {
                    setError("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
                } else {
                    setError("Đã xảy ra lỗi khi tải thông báo. Vui lòng thử lại sau.");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchNotifications();
    }, []);

    if (loading) {
        return <div className="text-center mt-5"><Spinner animation="border" /></div>;
    }

    if (error) {
        return <Alert variant="danger" className="m-5">{error}</Alert>;
    }

    if (!notifications || notifications.length === 0) {
        return <Alert variant="info" className="m-5">Bạn không có thông báo nào.</Alert>;
    }

    return (
        <Container className="my-5">
            <h2 className="text-center text-success mb-4">Thông báo của tôi</h2>
            <ListGroup>
                {notifications.map(notif => (
                    <ListGroup.Item 
                        key={notif.id}
                        variant={notif.isRead ? "light" : "primary"}
                    >
                        {notif.message}
                        <small className="d-block text-muted mt-1">
                            {new Date(notif.createdAt).toLocaleString('vi-VN')}
                        </small>
                    </ListGroup.Item>
                ))}
            </ListGroup>
        </Container>
    );
};

export default PatientNotifications;
