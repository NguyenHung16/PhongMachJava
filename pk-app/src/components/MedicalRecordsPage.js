import React, { useEffect, useState, useContext } from 'react';
import { Container, Table, Button, Modal, Badge, Card, Row, Col, Spinner } from 'react-bootstrap';
import { authApis, endpoints } from '../config/Apis';
import { MyUserContext } from '../config/MyContexts';
import { FaFileMedical, FaPrescriptionBottleAlt, FaCalendarAlt, FaSearchPlus } from 'react-icons/fa';

const MedicalRecordsPage = () => {
    const [user] = useContext(MyUserContext);
    const [records, setRecords] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedRecord, setSelectedRecord] = useState(null);
    const [showModal, setShowModal] = useState(false);

    useEffect(() => {
        const fetchRecords = async () => {
            const patientId = user?.id || user?.patientId;
            if (patientId) {
                try {
                    setLoading(true);
                    const res = await authApis().get(endpoints['medical-records-patient'](patientId));
                    if (res.data.status === 200) setRecords(res.data.data || []);
                } catch (err) {
                    console.error("Lỗi tải bệnh án:", err);
                } finally {
                    setLoading(false);
                }
            }
        };
        fetchRecords();
    }, [user]);

    const handleShowDetails = (record) => {
        setSelectedRecord(record);
        setShowModal(true);
    };

    if (loading) return <Container className="text-center my-5"><Spinner animation="border" variant="primary" /></Container>;

    return (
        <Container className="py-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="fw-bold text-primary"><FaFileMedical className="me-2" /> HỒ SƠ SỨC KHỎE CỦA BẠN</h2>
                <Badge bg="primary" pill className="px-3 py-2 shadow-sm">Tổng cộng: {records.length}</Badge>
            </div>

            {records.length === 0 ? (
                <Card className="border-0 shadow-sm text-center py-5">
                    <Card.Body>
                        <FaFileMedical size={60} className="text-muted opacity-25 mb-3" />
                        <h4 className="text-muted">Bạn chưa có hồ sơ khám bệnh nào.</h4>
                    </Card.Body>
                </Card>
            ) : (
                <Row className="g-4">
                    {records.map((record) => (
                        <Col md={6} lg={4} key={record.id}>
                            <Card className="border-0 shadow-sm h-100 transition hover-up border-top border-4 border-primary">
                                <Card.Body>
                                    <div className="d-flex justify-content-between mb-3">
                                        <div className="text-muted small fw-bold">
                                            <FaCalendarAlt className="me-1 text-primary" /> {new Date(record.createdDate).toLocaleDateString('vi-VN')}
                                        </div>
                                        <Badge bg="success">Hoàn thành</Badge>
                                    </div>
                                    <h5 className="fw-bold text-dark mb-2 text-truncate">{record.diagnosis || "Khám bệnh"}</h5>
                                    <p className="text-muted small mb-4">Mã số hồ sơ: <strong>#MR-{record.id}</strong></p>
                                    <Button variant="primary" className="w-100 rounded-pill fw-bold shadow-sm" onClick={() => handleShowDetails(record)}>
                                        <FaSearchPlus className="me-2" /> XEM CHI TIẾT
                                    </Button>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}

            <Modal show={showModal} onHide={() => setShowModal(false)} size="lg" centered>
                <Modal.Header closeButton className="bg-light border-0 px-4 pt-4">
                    <Modal.Title className="fw-bold text-primary">KẾT QUẢ KHÁM CHI TIẾT</Modal.Title>
                </Modal.Header>
                <Modal.Body className="p-4">
                    {selectedRecord && (
                        <>
                            <div className="bg-white border rounded p-3 mb-4 shadow-sm">
                                <Row className="gy-3 text-center text-sm-start">
                                    <Col sm={6} className="border-sm-end">
                                        <small className="text-muted d-block fw-bold text-uppercase">Bệnh nhân</small>
                                        <span className="fw-bold text-dark fs-5">{selectedRecord.patientName}</span>
                                    </Col>
                                    <Col sm={6} className="ps-sm-4">
                                        <small className="text-muted d-block fw-bold text-uppercase">Ngày thực hiện</small>
                                        <span className="fw-bold text-dark fs-5">{new Date(selectedRecord.createdDate).toLocaleDateString('vi-VN')}</span>
                                    </Col>
                                </Row>
                            </div>

                            <div className="mb-4">
                                <h6 className="fw-bold text-dark border-start border-4 border-primary ps-2 mb-3">CHẨN ĐOÁN & ĐIỀU TRỊ</h6>
                                <Card className="bg-light border-0 p-3 shadow-none">
                                    <p className="mb-2"><strong>Triệu chứng:</strong> {selectedRecord.symptoms}</p>
                                    <p className="mb-2"><strong>Chẩn đoán:</strong> <span className="text-primary fw-bold">{selectedRecord.diagnosis}</span></p>
                                    {selectedRecord.treatmentPlan && (
                                        <p className="mb-0 border-top pt-2 mt-2"><strong>Kế hoạch điều trị:</strong> {selectedRecord.treatmentPlan}</p>
                                    )}
                                </Card>
                            </div>

                            <h6 className="fw-bold text-dark border-start border-4 border-danger ps-2 mb-3">
                                <FaPrescriptionBottleAlt className="text-danger me-2" /> ĐƠN THUỐC ĐÃ KÊ
                            </h6>

                            {selectedRecord.prescriptionItems && selectedRecord.prescriptionItems.length > 0 ? (
                                <div className="border rounded overflow-hidden shadow-sm">
                                    <Table hover responsive borderless className="align-middle mb-0">
                                        <thead className="table-light">
                                            <tr className="small text-muted text-uppercase">
                                                <th className="ps-3">Tên thuốc</th>
                                                <th>Số lượng / Liều</th>
                                                <th>Hướng dẫn sử dụng</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {selectedRecord.prescriptionItems.map((m, idx) => (
                                                <tr key={idx} className="border-bottom">
                                                    <td className="ps-3 fw-bold text-primary">{m.medicineName}</td>
                                                    <td><Badge bg="info" className="fw-normal">{m.quantity}</Badge></td>
                                                    <td className="small text-muted italic">{m.instruction || "Dùng theo chỉ định của bác sĩ"}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </Table>
                                </div>
                            ) : (
                                <div className="text-center py-4 bg-light rounded-3 text-muted small border border-dashed">
                                    Không có thông tin đơn thuốc chi tiết trong hồ sơ này.
                                </div>
                            )}
                        </>
                    )}
                </Modal.Body>
                <Modal.Footer className="border-0 px-4 pb-4">
                    <Button variant="secondary" className="rounded-pill px-4 fw-bold shadow-sm" onClick={() => setShowModal(false)}>Đóng cửa sổ</Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default MedicalRecordsPage;
