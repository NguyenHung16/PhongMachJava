import React, { useState, useEffect, useCallback } from "react";
import { Container, Form, Button, Row, Col, Card, Alert, Spinner, Badge, Table } from "react-bootstrap";
import { authApis, endpoints } from "../config/Apis";
import { useLocation, useNavigate } from "react-router-dom";
import { FaPills, FaTrash, FaFilePrescription, FaClipboardList, FaClock } from "react-icons/fa";

const money = new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" });

const PrescriptionPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const patientId = location.state?.patientId;
    const medicalRecordId = location.state?.medicalRecordId;

    const [medicines, setMedicines] = useState([]);
    const [medicalRecords, setMedicalRecords] = useState([]);
    const [selectedRecord, setSelectedRecord] = useState(null);
    const [prescriptionItems, setPrescriptionItems] = useState([]);
    const [currentMedicine, setCurrentMedicine] = useState("");
    const [dosage, setDosage] = useState("");
    const [frequency, setFrequency] = useState("");
    const [instructions, setInstructions] = useState("");
    const [loading, setLoading] = useState(true);
    const [loadingRecords, setLoadingRecords] = useState(false);
    const [error, setError] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const totalAmount = prescriptionItems.reduce((sum, item) => sum + item.quantity * item.price, 0);

    const fetchRecords = useCallback(async (id) => {
        setLoadingRecords(true);
        try {
            const res = await authApis().get(endpoints["medical-records-patient"](id));
            if (res.data.status === 200) {
                const records = res.data.data || [];
                setMedicalRecords(records);
                setSelectedRecord(
                    records.find((record) => record.id?.toString() === medicalRecordId?.toString()) || records[0] || null
                );
            }
        } catch (err) {
            setError("Không thể tải hồ sơ bệnh án.");
        } finally {
            setLoadingRecords(false);
        }
    }, [medicalRecordId]);

    useEffect(() => {
        const init = async () => {
            try {
                const res = await authApis().get(endpoints["medicines"]);
                if (res.data.status === 200) setMedicines(res.data.data || []);
                if (patientId) await fetchRecords(patientId);
            } catch (err) {
                setError("Không thể tải dữ liệu kê đơn.");
            } finally {
                setLoading(false);
            }
        };
        init();
    }, [patientId, fetchRecords]);

    const handleAddItem = () => {
        const medicine = medicines.find((m) => m.id.toString() === currentMedicine);
        const quantity = parseInt(dosage);
        if (!medicine || !quantity || quantity <= 0 || !frequency) return;

        setPrescriptionItems((current) => [
            ...current,
            {
                medicineId: parseInt(medicine.id),
                name: medicine.name,
                unit: medicine.unit,
                quantity,
                dosage: quantity.toString(),
                frequency,
                instructions,
                price: Number(medicine.price || 0),
            },
        ]);
        setCurrentMedicine("");
        setDosage("");
        setFrequency("");
        setInstructions("");
    };

    const handleSave = async (e) => {
        e.preventDefault();
        if (!selectedRecord || prescriptionItems.length === 0) {
            setError("Vui lòng chọn hồ sơ bệnh án và thêm thuốc.");
            return;
        }

        try {
            setIsSubmitting(true);
            const requestBody = {
                medicalRecordId: parseInt(selectedRecord.id),
                medicines: prescriptionItems.map((item) => ({
                    medicineId: item.medicineId,
                    dosage: item.dosage,
                    frequency: item.frequency,
                    instructions: item.instructions,
                })),
            };
            const res = await authApis().post(endpoints["create-prescription"], requestBody);
            if (res.status === 201) {
                alert("Kê đơn thành công!");
                navigate("/doctors/my-appointments");
            }
        } catch (err) {
            setError(err.response?.data || "Không thể lưu đơn thuốc.");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) {
        return <Container className="text-center my-5"><Spinner animation="border" variant="primary" /></Container>;
    }

    return (
        <Container className="py-5">
            <div className="d-flex flex-column flex-md-row justify-content-between gap-3 mb-4">
                <div>
                    <h2 className="fw-bold text-dark mb-1"><FaFilePrescription className="text-primary me-2" /> Kê đơn thuốc</h2>
                    {location.state?.patientName && <Badge bg="info" className="p-2">Bệnh nhân: {location.state.patientName}</Badge>}
                </div>
                <div className="text-md-end">
                    <div className="text-muted small">Tổng tiền thuốc</div>
                    <div className="h4 fw-bold text-success mb-0">{money.format(totalAmount)}</div>
                </div>
            </div>
            {error && <Alert variant="danger" dismissible onClose={() => setError(null)}>{error}</Alert>}
            <Row className="g-4">
                <Col lg={4}>
                    <Card className="border-0 shadow-sm mb-4">
                        <Card.Header className="bg-white py-3 border-0 fw-bold"><FaClipboardList className="me-2 text-primary" /> Hồ sơ bệnh án</Card.Header>
                        <Card.Body>
                            <Form.Label className="small text-muted">Chọn bệnh án để gắn đơn thuốc</Form.Label>
                            <Form.Select className="bg-light border-0 shadow-sm" value={selectedRecord?.id || ""} onChange={(e) => setSelectedRecord(medicalRecords.find((r) => r.id.toString() === e.target.value))}>
                                <option value="">-- Chọn bệnh án --</option>
                                {medicalRecords.map((r) => <option key={r.id} value={r.id}>Ngày {r.createdDate}: {r.diagnosis}</option>)}
                            </Form.Select>
                            {loadingRecords && <Spinner size="sm" className="mt-2" />}
                            {selectedRecord && <div className="small bg-info bg-opacity-10 p-2 rounded mt-3"><FaClock className="me-1" /> Ngày khám: <strong>{selectedRecord.createdDate}</strong></div>}
                        </Card.Body>
                    </Card>
                    <Card className="border-0 shadow-sm">
                        <Card.Header className="bg-white py-3 border-0 fw-bold"><FaPills className="me-2 text-primary" /> Thêm thuốc</Card.Header>
                        <Card.Body>
                            <Form.Select className="bg-light border-0 mb-3" value={currentMedicine} onChange={(e) => setCurrentMedicine(e.target.value)}>
                                <option value="">-- Tìm chọn thuốc --</option>
                                {medicines.map((m) => <option key={m.id} value={m.id}>{m.name} - {money.format(m.price || 0)} / {m.unit}</option>)}
                            </Form.Select>
                            <Row className="mb-3">
                                <Col xs={6}><Form.Control className="bg-light border-0" type="number" min="1" placeholder="Số lượng" value={dosage} onChange={(e) => setDosage(e.target.value)} /></Col>
                                <Col xs={6}><Form.Control className="bg-light border-0" placeholder="Tần suất" value={frequency} onChange={(e) => setFrequency(e.target.value)} /></Col>
                            </Row>
                            <Form.Control as="textarea" rows={2} className="bg-light border-0 mb-3" placeholder="Hướng dẫn sử dụng..." value={instructions} onChange={(e) => setInstructions(e.target.value)} />
                            <Button variant="outline-primary" className="w-100 rounded-pill fw-bold" onClick={handleAddItem}>+ Thêm vào đơn</Button>
                        </Card.Body>
                    </Card>
                </Col>
                <Col lg={8}>
                    <Card className="border-0 shadow-lg h-100">
                        <Card.Header className="bg-white py-3 border-0 d-flex justify-content-between"><h6 className="mb-0 fw-bold">Chi tiết đơn thuốc</h6><Badge bg="primary" pill>{prescriptionItems.length}</Badge></Card.Header>
                        <Card.Body className="p-0">
                            {prescriptionItems.length === 0 ? <div className="text-center py-5 text-muted opacity-50"><FaPills size={50} className="mb-3" /><p>Chưa có thuốc</p></div> : (
                                <>
                                    <Table hover className="align-middle mb-0">
                                        <thead className="table-light"><tr><th className="ps-4">Tên thuốc</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th><th className="text-end pe-4">Xóa</th></tr></thead>
                                        <tbody>{prescriptionItems.map((item, idx) => <tr key={idx}><td className="ps-4"><div className="fw-bold">{item.name}</div><div className="small text-muted fst-italic">{item.frequency} - {item.instructions}</div></td><td>{item.quantity} {item.unit}</td><td>{money.format(item.price)}</td><td className="fw-bold">{money.format(item.quantity * item.price)}</td><td className="text-end pe-4"><Button variant="link" className="text-danger" onClick={() => setPrescriptionItems(prescriptionItems.filter((_, i) => i !== idx))}><FaTrash /></Button></td></tr>)}</tbody>
                                    </Table>
                                    <div className="p-4 bg-light border-top"><Button variant="success" size="lg" className="w-100 rounded-pill fw-bold shadow py-3" onClick={handleSave} disabled={isSubmitting}>{isSubmitting ? <Spinner size="sm" /> : "Xác nhận & lưu đơn thuốc"}</Button></div>
                                </>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default PrescriptionPage;
