import React, { useEffect, useState } from 'react';
import { Form, Button, Row, Col, Alert, Card, Container } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import APIs, { authApis, endpoints } from "../config/Apis";
import MySpinner from './layout/MySpinner';
import { FaCalendarAlt, FaUserMd, FaStethoscope, FaClock, FaNotesMedical } from 'react-icons/fa';

const BookingPage = () => {
    const navigate = useNavigate();
    const [specialties, setSpecialties] = useState([]);
    const [doctors, setDoctors] = useState([]);
    const [selectedSpecialty, setSelectedSpecialty] = useState('');
    const [selectedDoctor, setSelectedDoctor] = useState('');
    const [appointmentDate, setAppointmentDate] = useState('');
    const [appointmentTime, setAppointmentTime] = useState('');
    const [reason, setReason] = useState('');
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [message, setMessage] = useState(null);
    const [patientInfo, setPatientInfo] = useState(null);

    useEffect(() => {
        const loadData = async () => {
            try {
                // 1. Lấy info patient hiện tại
                const pRes = await authApis().get(endpoints['current-patient']);
                if (pRes.data.status === 200) setPatientInfo(pRes.data.data);

                // 2. Lấy danh sách chuyên khoa
                const sRes = await APIs.get(endpoints['specialties']);
                if (sRes.data.status === 200) setSpecialties(sRes.data.data);

                // 3. Lấy danh sách bác sĩ mặc định
                const dRes = await APIs.get(endpoints['doctors']);
                if (dRes.data.status === 200) setDoctors(dRes.data.data);
            } catch (ex) {
                console.error(ex);
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, []);

    const handleSpecialtyChange = async (e) => {
        const sid = e.target.value;
        setSelectedSpecialty(sid);
        setSelectedDoctor('');
        try {
            const res = await APIs.get(sid ? `${endpoints['doctors']}?specialtyId=${sid}` : endpoints['doctors']);
            if (res.data.status === 200) setDoctors(res.data.data);
        } catch (ex) { console.error(ex); }
    };

    const handleBooking = async (e) => {
        e.preventDefault();
        if (!selectedDoctor || !appointmentDate || !appointmentTime) return;

        try {
            setSubmitting(true);
            const reqData = {
                doctorId: parseInt(selectedDoctor),
                patientId: patientInfo.id,
                appointmentDate: appointmentDate,
                appointmentTime: appointmentTime,
                reason: reason
            };
            const res = await authApis().post(endpoints['appointments'], reqData);
            if (res.data.status === 200 || res.status === 201) {
                setMessage({ type: 'success', text: 'Đặt lịch thành công! Vui lòng đặt cọc trong trang cá nhân.' });
                setTimeout(() => navigate("/profile"), 2000);
            }
        } catch (ex) {
            setMessage({ type: 'danger', text: 'Đặt lịch thất bại. Vui lòng thử lại.' });
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <Container className="text-center my-5"><MySpinner /></Container>;

    return (
        <Container className="py-5">
            <Row className="justify-content-center">
                <Col lg={9}>
                    <Card className="border-0 shadow-lg">
                        <Card.Header className="bg-primary text-white py-4 text-center border-0">
                            <h2 className="mb-0 fw-bold"><FaCalendarAlt className="me-2" /> ĐẶT LỊCH KHÁM ONLINE</h2>
                            <p className="mb-0 opacity-75">Bệnh nhân: {patientInfo?.fullName} ({patientInfo?.patientCode})</p>
                        </Card.Header>
                        <Card.Body className="p-4 p-md-5">
                            {message && <Alert variant={message.type} className="shadow-sm">{message.text}</Alert>}
                            <Form onSubmit={handleBooking}>
                                <Row className="gy-4">
                                    <Col md={6}>
                                        <Form.Label className="fw-bold text-muted small"><FaStethoscope className="me-2" />CHUYÊN KHOA</Form.Label>
                                        <Form.Select className="bg-light border-0 py-3" value={selectedSpecialty} onChange={handleSpecialtyChange}>
                                            <option value="">-- Tất cả chuyên khoa --</option>
                                            {specialties.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
                                        </Form.Select>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Label className="fw-bold text-muted small"><FaUserMd className="me-2" />BÁC SĨ</Form.Label>
                                        <Form.Select className="bg-light border-0 py-3" value={selectedDoctor} onChange={(e) => setSelectedDoctor(e.target.value)} required>
                                            <option value="">-- Chọn bác sĩ --</option>
                                            {doctors.map(d => <option key={d.id} value={d.id}>{d.fullName}</option>)}
                                        </Form.Select>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Label className="fw-bold text-muted small"><FaCalendarAlt className="me-2" />NGÀY HẸN</Form.Label>
                                        <Form.Control type="date" className="bg-light border-0 py-3" value={appointmentDate} onChange={(e) => setAppointmentDate(e.target.value)} min={new Date().toISOString().split('T')[0]} required />
                                    </Col>
                                    <Col md={6}>
                                        <Form.Label className="fw-bold text-muted small"><FaClock className="me-2" />GIỜ HẸN</Form.Label>
                                        <Form.Control type="time" className="bg-light border-0 py-3" value={appointmentTime} onChange={(e) => setAppointmentTime(e.target.value)} required />
                                    </Col>
                                    <Col md={12}>
                                        <Form.Label className="fw-bold text-muted small"><FaNotesMedical className="me-2" />LÝ DO KHÁM</Form.Label>
                                        <Form.Control as="textarea" rows={3} className="bg-light border-0 py-3" placeholder="Mô tả triệu chứng hiện tại của bạn..." value={reason} onChange={(e) => setReason(e.target.value)} />
                                    </Col>
                                </Row>
                                <div className="d-grid mt-5">
                                    <Button variant="primary" type="submit" size="lg" className="rounded-pill fw-bold py-3 shadow" disabled={submitting}>
                                        {submitting ? <><MySpinner size="sm" /> Đang xử lý...</> : "XÁC NHẬN ĐẶT LỊCH KHÁM"}
                                    </Button>
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default BookingPage;
