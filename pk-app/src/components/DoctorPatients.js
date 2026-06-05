import React, { useState, useEffect } from "react";
import { Alert, Container, Spinner, Table, Card, Button, Modal, Badge, Image } from "react-bootstrap";
import { authApis, endpoints, getImageUrl } from "../config/Apis";
import { FaHistory, FaUserInjured } from "react-icons/fa";

const DoctorPatients = () => {
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedPatient, setSelectedPatient] = useState(null);
    const [history, setHistory] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [loadingHistory, setLoadingHistory] = useState(false);

    useEffect(() => {
        const fetchPatients = async () => {
            try {
                const res = await authApis().get(endpoints["doctor-patients"]);
                if (res.data.status === 200) {
                    setPatients(res.data.data || []);
                }
            } catch (err) {
                setError("Không thể tải danh sách bệnh nhân.");
            } finally {
                setLoading(false);
            }
        };
        fetchPatients();
    }, []);

    const handleViewHistory = async (patient) => {
        setSelectedPatient(patient);
        setShowModal(true);
        setLoadingHistory(true);
        try {
            const res = await authApis().get(endpoints["medical-records-patient"](patient.id));
            if (res.data.status === 200) {
                setHistory(res.data.data || []);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoadingHistory(false);
        }
    };

    if (loading) {
        return (
            <Container className="text-center my-5">
                <Spinner animation="border" variant="primary" />
            </Container>
        );
    }

    return (
        <Container className="py-5">
            <h2 className="fw-bold text-primary mb-4">
                <FaUserInjured className="me-2" /> BỆNH NHÂN ĐANG PHỤ TRÁCH
            </h2>
            {error && <Alert variant="danger" className="mb-4">{error}</Alert>}
            <Card className="border-0 shadow-sm">
                <Card.Body className="p-0">
                    <Table hover responsive className="align-middle mb-0">
                        <thead className="table-light">
                            <tr>
                                <th className="ps-4">Ảnh</th>
                                <th>Mã BN</th>
                                <th>Họ tên</th>
                                <th className="text-center pe-4">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            {patients.map((p) => (
                                <tr key={p.id}>
                                    <td className="ps-4">
                                        <Image
                                            src={getImageUrl(p.avatar)}
                                            roundedCircle
                                            style={{ width: "40px", height: "40px", objectFit: "cover" }}
                                            onError={(e) => (e.target.src = "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y")}
                                        />
                                    </td>
                                    <td><Badge bg="secondary">{p.patientCode}</Badge></td>
                                    <td className="fw-bold">{p.fullName}</td>
                                    <td className="text-center pe-4">
                                        <Button variant="outline-primary" size="sm" className="rounded-pill" onClick={() => handleViewHistory(p)}>
                                            <FaHistory className="me-1" /> Lịch sử khám
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </Card.Body>
            </Card>

            <Modal show={showModal} onHide={() => setShowModal(false)} size="lg" centered>
                <Modal.Header closeButton className="bg-primary text-white">
                    <Modal.Title>Lịch sử bệnh án: {selectedPatient?.fullName}</Modal.Title>
                </Modal.Header>
                <Modal.Body className="p-4 bg-light">
                    {loadingHistory ? (
                        <div className="text-center py-5"><Spinner animation="border" /></div>
                    ) : history.length === 0 ? (
                        <Alert variant="info">Chưa có lịch sử khám.</Alert>
                    ) : (
                        history.map((h) => (
                            <Card key={h.id} className="border-0 shadow-sm mb-3">
                                <Card.Body>
                                    <h6 className="text-primary fw-bold">Ngày khám: {h.createdDate}</h6>
                                    <p className="mb-2"><strong>Chẩn đoán:</strong> {h.diagnosis}</p>
                                    <p className="mb-2"><strong>Triệu chứng:</strong> {h.symptoms || "N/A"}</p>
                                    <div>
                                        <strong>Đơn thuốc:</strong>
                                        {h.prescriptionItems && h.prescriptionItems.length > 0 ? (
                                            <ul className="mb-0 mt-2">
                                                {h.prescriptionItems.map((item, index) => (
                                                    <li key={index}>
                                                        {item.medicineName} - SL: {item.quantity}
                                                        {item.instruction ? ` - ${item.instruction}` : ""}
                                                    </li>
                                                ))}
                                            </ul>
                                        ) : (
                                            <span className="text-muted ms-2">Chưa kê đơn</span>
                                        )}
                                    </div>
                                </Card.Body>
                            </Card>
                        ))
                    )}
                </Modal.Body>
            </Modal>
        </Container>
    );
};

export default DoctorPatients;
