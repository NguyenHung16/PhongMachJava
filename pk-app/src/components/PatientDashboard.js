import React, { useEffect, useState, useContext } from 'react';
import { Container, Row, Col, Card, Button, Badge, Table, Alert, Spinner } from 'react-bootstrap';
import { FaUserEdit, FaCalendarPlus, FaFileMedical, FaCalendarAlt, FaCreditCard, FaFileInvoiceDollar } from 'react-icons/fa';
import { Link } from 'react-router-dom';
import { MyUserContext } from '../config/MyContexts';
import { authApis, endpoints } from '../config/Apis';

const PatientDashboard = () => {
    const [user] = useContext(MyUserContext);
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [paymentLoading, setPaymentLoading] = useState(false);
    const [paidAppointments, setPaidAppointments] = useState([]);

    const getAppointmentStatus = (status) => (status || '').toUpperCase();

    const fetchMyAppointments = async () => {
        try {
            setLoading(true);
            const res = await authApis().get(endpoints['my-appointments-patient']);
            if (res.data.status === 200) {
                setAppointments(res.data.data || []);
            }
        } catch (err) {
            console.error("Lỗi tải lịch hẹn:", err);
            setError("Không thể tải danh sách lịch hẹn.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (user) fetchMyAppointments();
    }, [user]);

    const handleDeposit = async (appId) => {
        try {
            setPaymentLoading(true);
            await authApis().post(endpoints['pay-appointment-deposit'](appId));
            setPaidAppointments((current) => [...current, appId]);
            setAppointments((current) =>
                current.map((app) =>
                    app.id === appId ? { ...app, status: 'CONFIRMED' } : app
                )
            );
            alert("Đặt cọc thành công!");
        } catch (err) {
            console.error("Deposit error:", err);
            alert("Không thể đặt cọc. Vui lòng thử lại.");
        } finally {
            setPaymentLoading(false);
        }
    };

    return (
        <Container className="py-5">
            {error && <Alert variant="danger" className="mb-4">{error}</Alert>}
            <Row className="mb-5 align-items-center">
                <Col md={8}>
                    <h1 className="fw-bold text-primary mb-1">Chào mừng, {user?.fullName || user?.username}!</h1>
                    <p className="text-muted fs-5">Mã bệnh nhân: <Badge bg="light" text="dark" className="border">{user?.patientCode || 'N/A'}</Badge></p>
                </Col>
                <Col md={4} className="text-md-end">
                    <Link to="/profile/edit">
                        <Button variant="outline-primary" className="rounded-pill px-4 fw-bold shadow-sm">
                            <FaUserEdit className="me-2" /> CHỈNH SỬA HỒ SƠ
                        </Button>
                    </Link>
                </Col>
            </Row>

            <Row className="g-4 mb-5">
                <Col md={4}>
                    <Link to="/booking" className="text-decoration-none">
                        <Card className="border-0 shadow-lg bg-primary text-white hover-shadow h-100">
                            <Card.Body className="p-4">
                                <div className="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h3 className="fw-bold mb-1">Đặt lịch khám</h3>
                                        <p className="mb-0 opacity-75 small">Chăm sóc sức khỏe ngay hôm nay</p>
                                    </div>
                                    <div className="fs-1 opacity-50"><FaCalendarPlus /></div>
                                </div>
                            </Card.Body>
                        </Card>
                    </Link>
                </Col>
                <Col md={4}>
                    <Link to="/bills" className="text-decoration-none">
                        <Card className="border-0 shadow-lg bg-warning text-dark hover-shadow h-100">
                            <Card.Body className="p-4">
                                <div className="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h3 className="fw-bold mb-1">Hóa đơn</h3>
                                        <p className="mb-0 opacity-75 small">Xem trạng thái thanh toán và hóa đơn</p>
                                    </div>
                                    <div className="fs-1 opacity-50"><FaFileInvoiceDollar /></div>
                                </div>
                            </Card.Body>
                        </Card>
                    </Link>
                </Col>
                <Col md={4}>
                    <Link to="/medical-records" className="text-decoration-none">
                        <Card className="border-0 shadow-lg bg-success text-white hover-shadow h-100">
                            <Card.Body className="p-4">
                                <div className="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h3 className="fw-bold mb-1">Hồ sơ bệnh án</h3>
                                        <p className="mb-0 opacity-75 small">Quản lý hồ sơ sức khỏe điện tử</p>
                                    </div>
                                    <div className="fs-1 opacity-50"><FaFileMedical /></div>
                                </div>
                            </Card.Body>
                        </Card>
                    </Link>
                </Col>
            </Row>

            <Card className="border-0 shadow-lg">
                <Card.Header className="bg-white py-4 border-0 d-flex justify-content-between align-items-center">
                    <h4 className="fw-bold mb-0 text-dark"><FaCalendarAlt className="me-2 text-primary" /> DANH SÁCH LỊCH HẸN SẮP TỚI</h4>
                    <Button variant="link" className="text-decoration-none fw-bold" onClick={fetchMyAppointments}>Làm mới</Button>
                </Card.Header>
                <Card.Body className="p-0">
                    {loading ? (
                        <div className="text-center py-5"><Spinner animation="border" variant="primary" /></div>
                    ) : (
                        <div className="table-responsive">
                            <Table hover className="align-middle mb-0">
                                <thead className="bg-light">
                                    <tr>
                                        <th className="ps-4 py-3">Bác sĩ / Chuyên khoa</th>
                                        <th>Thời gian khám</th>
                                        <th>Trạng thái</th>
                                        <th className="text-end pe-4">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {appointments.length === 0 ? (
                                        <tr><td colSpan="4" className="text-center py-5 text-muted">Bạn chưa có lịch hẹn nào sắp tới.</td></tr>
                                    ) : (
                                        appointments.map((app) => (
                                            <tr key={app.id}>
                                                <td className="ps-4">
                                                    <div className="fw-bold text-dark">{app.doctorName}</div>
                                                    <div className="small text-muted">{app.specialtyName || "Dịch vụ y tế"}</div>
                                                </td>
                                                <td>
                                                    <div className="small fw-bold text-dark">{app.appointmentDate}</div>
                                                    <div className="small text-muted">{app.appointmentTime}</div>
                                                </td>
                                                <td>
                                                    <Badge bg={getAppointmentStatus(app.status) === 'AWAITING_DEPOSIT' ? 'warning' : 'success'} className="px-3 py-2 fw-normal">
                                                        {getAppointmentStatus(app.status) === 'AWAITING_DEPOSIT' ? 'Chờ đặt cọc' : 'Đã đặt cọc'}
                                                    </Badge>
                                                </td>
                                                <td className="text-end pe-4">
                                                    {getAppointmentStatus(app.status) === 'AWAITING_DEPOSIT' && !paidAppointments.includes(app.id) && (
                                                        <Button
                                                            variant="primary"
                                                            size="sm"
                                                            disabled={paymentLoading}
                                                            className="rounded-pill px-4 shadow-sm fw-bold border-0"
                                                            style={{ background: 'linear-gradient(45deg, #00a8cc, #005073)' }}
                                                            onClick={() => handleDeposit(app.id)}
                                                        >
                                                            {paymentLoading ? <Spinner size="sm" /> : <><FaCreditCard className="me-2" /> ĐẶT CỌC</>}
                                                        </Button>
                                                    )}
                                                    {(getAppointmentStatus(app.status) === 'CONFIRMED' || paidAppointments.includes(app.id)) && (
                                                        <Badge bg="success" className="px-3 py-2 fw-normal">Đã đặt cọc</Badge>
                                                    )}
                                                </td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </Table>
                        </div>
                    )}
                </Card.Body>
            </Card>
        </Container>
    );
};

export default PatientDashboard;
