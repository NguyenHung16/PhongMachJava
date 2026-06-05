// src/components/AppointmentsPage.js

import React, { useEffect, useState } from 'react';
import { Alert, Card, ListGroup } from 'react-bootstrap';
import cookie from 'react-cookies';
import { authApis, endpoints } from '../config/Apis';
import MySpinner from './layout/MySpinner';
import moment from 'moment';

const AppointmentsPage = () => {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState('');
    const [variant, setVariant] = useState('');

    useEffect(() => {
        const fetchAppointments = async () => {
            const user = cookie.load('user');

            if (!user || !user.id || user.role !== 'PATIENT') {
                setMessage('Bạn không có quyền truy cập vào trang này.');
                setVariant('warning');
                setLoading(false);
                return;
            }

            try {
                const res = await authApis().get(endpoints['my-appointments-patient']);
                
                if (Array.isArray(res.data)) {
                    setAppointments(res.data);
                } else {
                    console.error("API did not return an array:", res.data);
                    setMessage('Dữ liệu trả về từ API không hợp lệ.');
                    setVariant('danger');
                }
            } catch (ex) {
                console.error("Lỗi khi tải lịch hẹn:", ex);
                setMessage('Có lỗi xảy ra khi tải lịch hẹn.');
                setVariant('danger');
            } finally {
                setLoading(false);
            }
        };

        fetchAppointments();
    }, []);

    if (loading) {
        return <MySpinner />;
    }

    return (
        <div className="container mt-5">
            <h1 className="text-center text-primary mb-4">Lịch Hẹn Của Tôi</h1>
            {message && <Alert variant={variant}>{message}</Alert>}

            {appointments.length > 0 ? (
                <ListGroup>
                    {appointments.map(appointment => (
                        <ListGroup.Item key={appointment.id}>
                            <Card>
                                <Card.Body>
                                    <Card.Title>Lịch hẹn với Bác sĩ {appointment.doctorName || 'Đang cập nhật'}</Card.Title>
                                    <Card.Text>
                                        <strong>Ngày hẹn:</strong> {moment(appointment.appointmentDate).format('DD/MM/YYYY')}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Thời gian:</strong> {appointment.appointmentTime || moment(appointment.appointmentDate).format('HH:mm')}
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            ) : (
                <Alert variant="info" className="mt-3">
                    Bạn chưa có lịch hẹn nào.
                </Alert>
            )}
        </div>
    );
};

export default AppointmentsPage;
