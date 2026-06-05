import React, { useState, useEffect, useContext, useCallback } from "react";
import { Alert, Container, Spinner, Table, Button, Badge, Card } from "react-bootstrap";
import { authApis, endpoints } from "../config/Apis";
import { FaCalendarAlt, FaUser, FaClock, FaStethoscope } from 'react-icons/fa';
import { useNavigate } from "react-router-dom";
import { MyUserContext } from "../config/MyContexts";

const getAppointmentStatus = (status) => (status || '').trim().toUpperCase();
const isCompletedAppointment = (appointment) => getAppointmentStatus(appointment?.status) === 'COMPLETED';

const DoctorAppointments = () => {
    const [user] = useContext(MyUserContext);
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const nav = useNavigate();

    const openMedicalRecordForm = (appointment) => {
        nav("/doctor/create-medical-record", { state: { appointment } });
    };

    const fetchAppointments = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            
            // BE đã tự động lọc: a.appointmentDate >= CURRENT_DATE
            const res = await authApis().get(endpoints['my-appointments-doctor']);
            
            if (res.data.status === 200) {
                setAppointments((res.data.data || []).filter((app) => !isCompletedAppointment(app)));
            } else {
                setError(res.data.message || "Không thể tải danh sách lịch hẹn.");
            }
        } catch (err) {
            console.error("Lỗi fetchAppointments:", err);
            setError("Lỗi kết nối hoặc phiên đăng nhập không hợp lệ.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        if (user && user.role === 'DOCTOR') {
            fetchAppointments();
        }
    }, [user, fetchAppointments]);

    if (loading) return <Container className="text-center my-5"><Spinner animation="border" variant="primary" /></Container>;

    return (
        <Container className="py-5">
            <Card className="border-0 shadow-lg">
                <Card.Header className="bg-primary text-white py-4 border-0 text-center shadow-sm">
                    <h2 className="mb-0 fw-bold"><FaCalendarAlt className="me-2" /> LỊCH KHÁM HÔM NAY</h2>
                    <p className="mb-0 opacity-75 small">Dữ liệu được cập nhật từ hệ thống theo ngày hiện tại</p>
                </Card.Header>
                <Card.Body className="p-0">
                    {error && <Alert variant="danger" className="m-4 shadow-sm">{error}</Alert>}
                    
                    {appointments.length === 0 && !error ? (
                        <div className="text-center py-5">
                            <FaCalendarAlt size={50} className="text-muted opacity-25 mb-3" />
                            <p className="text-muted fs-5">Hiện tại không có lịch khám nào trong ngày.</p>
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <Table hover className="align-middle mb-0">
                                <thead className="table-light">
                                    <tr>
                                        <th className="ps-4">Bệnh nhân</th>
                                        <th>Thời gian khám</th>
                                        <th>Trạng thái</th>
                                        <th className="text-center pe-4">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {appointments.map((app) => (
                                        <tr key={app.id}>
                                            <td className="ps-4">
                                                <div className="d-flex align-items-center">
                                                    <div className="bg-light p-2 rounded-circle me-3">
                                                        <FaUser className="text-secondary" />
                                                    </div>
                                                    <div>
                                                        <div className="fw-bold text-dark">{app.patientName}</div>
                                                        <div className="small text-muted">Mã lịch: #{app.id}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <div className="small fw-bold text-dark"><FaCalendarAlt className="text-primary me-1" /> {app.appointmentDate}</div>
                                                <div className="small text-muted"><FaClock className="text-primary me-1" /> {app.appointmentTime}</div>
                                            </td>
                                            <td>
                                                <Badge bg={getAppointmentStatus(app.status) === 'AWAITING_DEPOSIT' ? 'warning' : 'success'} className="fw-normal px-3 py-2 shadow-sm">
                                                    {getAppointmentStatus(app.status) === 'AWAITING_DEPOSIT' ? 'Chờ đặt cọc' : 'Sẵn sàng khám'}
                                                </Badge>
                                            </td>
                                            <td className="text-center pe-4">
                                                <div className="d-flex gap-2 justify-content-center">
                                                    {app && (
                                                        <Button variant="primary" size="sm" className="rounded-pill px-3 shadow-sm border-0" onClick={() => openMedicalRecordForm(app)}>
                                                            <FaStethoscope className="me-1" /> Khám bệnh
                                                        </Button>
                                                    )}
                                                    {getAppointmentStatus(app.status) === 'AWAITING_DEPOSIT' && (
                                                        <span className="text-muted small">Bệnh nhân chưa đặt cọc</span>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </div>
                    )}
                </Card.Body>
            </Card>
        </Container>
    );
};

export default DoctorAppointments;
