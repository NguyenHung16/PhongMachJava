import React, { useContext, useEffect, useState } from "react";
import { Alert, Badge, Button, Card, Container, Spinner, Table } from "react-bootstrap";
import { FaFileInvoiceDollar, FaCreditCard, FaRegClock } from "react-icons/fa";
import { MyUserContext } from "../config/MyContexts";
import { authApis, endpoints } from "../config/Apis";

const money = new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" });

const PatientBillsPage = () => {
    const [user] = useContext(MyUserContext);
    const [bills, setBills] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [payingId, setPayingId] = useState(null);

    useEffect(() => {
        const loadBills = async () => {
            try {
                const res = await authApis().get(endpoints["patient-bills"](user.id));
                if (res.data.status === 200) {
                    setBills(res.data.data || []);
                } else {
                    setError(res.data.message || "Không thể tải hóa đơn.");
                }
            } catch (err) {
                setError("Không thể tải hóa đơn.");
            } finally {
                setLoading(false);
            }
        };

        if (user && user.role === "PATIENT") {
            loadBills();
        } else {
            setLoading(false);
            setError("Vui lòng đăng nhập với tài khoản bệnh nhân.");
        }
    }, [user]);

    const handlePay = async (billId) => {
        try {
            setPayingId(billId);
            await authApis().post(endpoints["pay-bill"](billId));
            setBills((current) =>
                current.map((bill) =>
                    bill.id === billId ? { ...bill, paymentStatus: "PAID" } : bill
                )
            );
            alert("Thanh toán thành công!");
        } catch (err) {
            alert("Không thể thanh toán. Vui lòng thử lại.");
        } finally {
            setPayingId(null);
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
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark mb-1">
                        <FaFileInvoiceDollar className="text-warning me-2" /> Hóa đơn của tôi
                    </h2>
                    <div className="text-muted">Theo dõi trạng thái thanh toán và hóa đơn khám bệnh</div>
                </div>
                <Badge bg="light" text="dark" className="border px-3 py-2">
                    {user?.fullName || user?.username}
                </Badge>
            </div>

            {error && <Alert variant="danger" className="mb-4">{error}</Alert>}

            <Card className="border-0 shadow-sm">
                <Card.Body className="p-0">
                    <Table hover responsive className="align-middle mb-0">
                        <thead className="table-light">
                            <tr>
                                <th className="ps-4">Mã hóa đơn</th>
                                <th>Ngày lập</th>
                                <th>Tiền</th>
                                <th>Trạng thái</th>
                                <th className="text-end pe-4">Thanh toán</th>
                            </tr>
                        </thead>
                        <tbody>
                            {bills.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="text-center py-5 text-muted">
                                        Chưa có hóa đơn nào.
                                    </td>
                                </tr>
                            ) : (
                                bills.map((bill) => {
                                    const unpaid = bill.paymentStatus === "UNPAID";
                                    return (
                                        <tr key={bill.id}>
                                            <td className="ps-4 fw-bold">#{bill.id}</td>
                                            <td>
                                                <FaRegClock className="me-1 text-muted" />
                                                {bill.billDate ? new Date(bill.billDate).toLocaleDateString("vi-VN") : "N/A"}
                                            </td>
                                            <td className="fw-bold text-dark">{bill.totalAmount ? money.format(bill.totalAmount) : "N/A"}</td>
                                            <td>
                                                <Badge bg={unpaid ? "warning" : "success"}>
                                                    {unpaid ? "CHỜ THANH TOÁN" : "ĐÃ THANH TOÁN"}
                                                </Badge>
                                            </td>
                                            <td className="text-end pe-4">
                                                {unpaid ? (
                                                    <Button
                                                        variant="primary"
                                                        size="sm"
                                                        className="rounded-pill px-4"
                                                        disabled={payingId === bill.id}
                                                        onClick={() => handlePay(bill.id)}
                                                    >
                                                        {payingId === bill.id ? <Spinner size="sm" /> : <><FaCreditCard className="me-2" /> Thanh toán</>}
                                                    </Button>
                                                ) : (
                                                    <span className="text-muted small">Đã hoàn tất</span>
                                                )}
                                            </td>
                                        </tr>
                                    );
                                })
                            )}
                        </tbody>
                    </Table>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default PatientBillsPage;
