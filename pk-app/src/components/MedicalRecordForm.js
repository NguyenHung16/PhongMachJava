import React, { useState, useEffect } from 'react';
import { Form, Button, Container, Alert, Spinner, Card, Row, Col, Badge } from 'react-bootstrap';
import { authApis, endpoints } from '../config/Apis';
import { useLocation, useNavigate } from 'react-router-dom';
import { FaFileMedical, FaStethoscope, FaClipboardList, FaArrowRight, FaCalendarCheck, FaUserInjured, FaClock } from 'react-icons/fa';

const MedicalRecordForm = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const appointmentFromSchedule = Boolean(location.state?.appointment);

    const [appointments, setAppointments] = useState([]);
    const [patients, setPatients] = useState([]);
    const [selectedApp, setSelectedApp] = useState(location.state?.appointment || null);
    const [autoPatientId, setAutoPatientId] = useState(location.state?.appointment?.patientId || '');
    const [diagnosis, setDiagnosis] = useState('');
    const [symptoms, setSymptoms] = useState('');
    const [loading, setLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [message, setMessage] = useState(null);

    const formatDate = (value) => {
        const date = value ? new Date(value) : null;
        return date && !Number.isNaN(date.getTime()) ? date.toLocaleDateString('vi-VN') : value || 'N/A';
    };
    const formatTime = (value) => {
        const date = value ? new Date(value) : null;
        return date && !Number.isNaN(date.getTime()) ? date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) : value || 'N/A';
    };

    useEffect(() => {
        const fetchInitialData = async () => {
            try {
                const [aRes, pRes] = await Promise.all([
                    authApis().get(endpoints['my-appointments-doctor']),
                    authApis().get(endpoints['doctor-patients'])
                ]);

                if (aRes.data.status === 200) {
                    const confirmedApps = aRes.data.data.filter(app =>
                        app.status === 'confirmed' || app.status === 'CONFIRMED'
                    );
                    setAppointments(confirmedApps);
                }
                if (pRes.data.status === 200) setPatients(pRes.data.data);
            } catch (err) {
                console.error(err);
                setMessage({ type: 'danger', text: 'Không thể tải dữ liệu.' });
            } finally {
                setLoading(false);
            }
        };
        fetchInitialData();
    }, []);

    useEffect(() => {
        if (selectedApp?.patientId) {
            setAutoPatientId(selectedApp.patientId);
            return;
        }

        if (selectedApp && patients.length > 0) {
            const patient = patients.find(p => p.fullName === selectedApp.patientName);
            setAutoPatientId(patient ? patient.id : '');
        }
    }, [selectedApp, patients]);

    const handleSelectApp = (e) => {
        const appId = e.target.value;
        const app = appointments.find(a => a.id.toString() === appId.toString());
        setSelectedApp(app);
        setAutoPatientId(app?.patientId || '');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedApp || !autoPatientId) {
            setMessage({ type: 'danger', text: 'Không tìm thấy mã bệnh nhân. Vui lòng kiểm tra lại lịch hẹn.' });
            return;
        }

        try {
            setIsSubmitting(true);
            const medicalRecordData = {
                patientId: parseInt(autoPatientId),
                appointmentId: parseInt(selectedApp.id),
                diagnosis,
                symptoms
            };

            const res = await authApis().post(endpoints['create-medical-record'], medicalRecordData);

            if (res.data.status === 200 || res.status === 201) {
                setMessage({ type: 'success', text: 'Tạo hồ sơ bệnh án thành công!' });
                setTimeout(() => {
                    navigate('/prescriptions', {
                        state: {
                            patientId: autoPatientId,
                            medicalRecordId: res.data.data?.id,
                            patientName: selectedApp.patientName
                        }
                    });
                }, 1200);
            }
        } catch (err) {
            setMessage({ type: 'danger', text: err.response?.data?.message || 'Lỗi server khi tạo hồ sơ.' });
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) return <Container className="text-center my-5"><Spinner animation="border" variant="primary" /></Container>;

    return (
        <Container className="py-4 py-md-5">
            <Row className="justify-content-center">
                <Col lg={9} xl={8}>
                    <Card className="border-0 shadow-sm overflow-hidden">
                        <Card.Header className="bg-white py-4 border-0 border-bottom">
                            <div className="d-flex flex-column flex-md-row justify-content-between gap-3">
                                <div>
                                    <Badge bg="primary" className="mb-2 px-3 py-2 rounded-pill">Khám bệnh</Badge>
                                    <h3 className="mb-1 fw-bold text-dark"><FaFileMedical className="me-2 text-primary" /> Lập hồ sơ bệnh án</h3>
                                    <div className="text-muted">Ghi nhận triệu chứng, chẩn đoán và chuyển sang kê đơn.</div>
                                </div>
                                {selectedApp && (
                                    <div className="text-md-end small text-muted">
                                        <div className="fw-bold text-dark">Mã lịch #{selectedApp.id}</div>
                                        <div>{formatDate(selectedApp.appointmentDate)} - {formatTime(selectedApp.appointmentTime)}</div>
                                    </div>
                                )}
                            </div>
                        </Card.Header>
                        <Card.Body className="p-4 p-md-5">
                            {message && <Alert variant={message.type} dismissible onClose={() => setMessage(null)}>{message.text}</Alert>}
                            <Form onSubmit={handleSubmit}>
                                {appointmentFromSchedule && selectedApp ? (
                                    <div className="bg-light rounded-3 p-4 mb-4 border">
                                        <Row className="g-3 align-items-center">
                                            <Col md={5}>
                                                <div className="d-flex align-items-center gap-3">
                                                    <div className="bg-primary bg-opacity-10 text-primary rounded-circle d-flex align-items-center justify-content-center" style={{ width: 48, height: 48 }}>
                                                        <FaUserInjured />
                                                    </div>
                                                    <div>
                                                        <div className="text-muted small fw-bold text-uppercase">Bệnh nhân</div>
                                                        <h5 className="mb-0 fw-bold text-dark">{selectedApp.patientName}</h5>
                                                    </div>
                                                </div>
                                            </Col>
                                            <Col md={4}>
                                                <div className="text-muted small fw-bold text-uppercase"><FaCalendarCheck className="me-1 text-primary" /> Lịch khám</div>
                                                <div className="fw-bold text-dark">{formatDate(selectedApp.appointmentDate)}</div>
                                                <div className="text-muted"><FaClock className="me-1" /> {formatTime(selectedApp.appointmentTime)}</div>
                                            </Col>
                                            <Col md={3} className="text-md-end">
                                                <Badge bg={autoPatientId ? 'success' : 'danger'} className="px-3 py-2 rounded-pill">
                                                    {autoPatientId ? `Mã BN: #${autoPatientId}` : 'Thiếu mã BN'}
                                                </Badge>
                                            </Col>
                                        </Row>
                                    </div>
                                ) : (
                                    <Form.Group className="mb-4">
                                        <Form.Label className="fw-bold text-primary"><FaCalendarCheck className="me-2" />Chọn lịch hẹn đang khám</Form.Label>
                                        <Form.Select className="bg-light border-0 py-3 shadow-sm" value={selectedApp?.id || ''} onChange={handleSelectApp} required>
                                            <option value="">-- Chọn lịch hẹn để lấy thông tin tự động --</option>
                                            {appointments.map(a => (
                                                <option key={a.id} value={a.id}>
                                                    {a.patientName} - {formatDate(a.appointmentDate)}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>
                                )}
                                <Form.Group className="mb-4">
                                    <Form.Label className="fw-bold"><FaClipboardList className="me-2 text-danger" />Triệu chứng</Form.Label>
                                    <Form.Control as="textarea" rows={4} className="bg-light border-0 shadow-sm" placeholder="Mô tả triệu chứng hiện tại..." value={symptoms} onChange={(e) => setSymptoms(e.target.value)} required />
                                </Form.Group>
                                <Form.Group className="mb-4">
                                    <Form.Label className="fw-bold"><FaStethoscope className="me-2 text-success" />Chẩn đoán</Form.Label>
                                    <Form.Control as="textarea" rows={4} className="bg-light border-0 shadow-sm" placeholder="Nhập kết luận bệnh lý..." value={diagnosis} onChange={(e) => setDiagnosis(e.target.value)} required />
                                </Form.Group>
                                <div className="d-grid mt-5">
                                    <Button variant="primary" type="submit" size="lg" className="fw-bold py-3 shadow-sm" disabled={isSubmitting || !autoPatientId}>
                                        {isSubmitting ? <Spinner size="sm" /> : <><FaArrowRight className="me-2" /> Lưu hồ sơ & sang kê đơn</>}
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

export default MedicalRecordForm;
