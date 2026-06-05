import React, { useState, useRef } from "react";
import { Button, Form, Container, Row, Col, Card, Alert, Spinner } from "react-bootstrap";
import { useNavigate, Link } from "react-router-dom";
import { FaUserPlus, FaUser, FaLock, FaEnvelope, FaIdCard, FaCamera } from 'react-icons/fa';
import Apis, { endpoints } from "../config/Apis";

const Register = () => {
    const [user, setUser] = useState({
        "fullName": "",
        "username": "",
        "password": "",
        "confirmPassword": "",
        "email": "",
        "role": "PATIENT"
    });
    const [avatar, setAvatar] = useState(null);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const avatarInput = useRef();
    const nav = useNavigate();

    const change = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const register = async (e) => {
        e.preventDefault();
        setErr("");

        if (user.password !== user.confirmPassword) {
            setErr("Mật khẩu xác nhận không khớp!");
            return;
        }

        const formData = new FormData();
        for (let key in user) {
            if (key !== 'confirmPassword') {
                formData.append(key, user[key]);
            }
        }
        if (avatarInput.current.files[0]) {
            formData.append("avatar", avatarInput.current.files[0]);
        }

        try {
            setLoading(true);
            const res = await Apis.post(endpoints['register'], formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });

            if (res.data.status === 200 || res.status === 201) {
                alert("Đăng ký thành công!");
                nav("/login");
            } else {
                setErr(res.data.message || "Đăng ký thất bại.");
            }
        } catch (ex) {
            console.error(ex);
            setErr("Có lỗi xảy ra trong quá trình đăng ký.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="my-5">
            <Row className="justify-content-center">
                <Col md={8} lg={6}>
                    <Card className="border-0 shadow-lg">
                        <Card.Header className="bg-primary text-white text-center py-4 border-0">
                            <h3 className="mb-0 fw-bold"><FaUserPlus className="me-2" /> ĐĂNG KÝ TÀI KHOẢN</h3>
                        </Card.Header>
                        <Card.Body className="p-4 p-md-5">
                            {err && <Alert variant="danger">{err}</Alert>}
                            <Form onSubmit={register}>
                                <div className="text-center mb-4 position-relative">
                                    <div className="d-inline-block position-relative">
                                        <div 
                                            className="bg-light rounded-circle d-flex align-items-center justify-content-center border"
                                            style={{ width: '120px', height: '120px', overflow: 'hidden' }}
                                        >
                                            {avatar ? (
                                                <img src={URL.createObjectURL(avatar)} alt="Avatar" className="w-100 h-100 object-fit-cover" />
                                            ) : (
                                                <FaUser size={60} className="text-secondary opacity-50" />
                                            )}
                                        </div>
                                        <Button 
                                            variant="primary" 
                                            size="sm" 
                                            className="position-absolute bottom-0 end-0 rounded-circle shadow"
                                            onClick={() => avatarInput.current.click()}
                                        >
                                            <FaCamera />
                                        </Button>
                                    </div>
                                    <input type="file" ref={avatarInput} hidden onChange={(e) => setAvatar(e.target.files[0])} accept="image/*" />
                                    <p className="mt-2 text-muted small">Ảnh đại diện</p>
                                </div>

                                <Row>
                                    <Col md={12}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold"><FaIdCard className="me-2 text-primary" />Họ và tên</Form.Label>
                                            <Form.Control type="text" name="fullName" value={user.fullName} onChange={change} required placeholder="Nguyễn Văn A" />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold"><FaUser className="me-2 text-primary" />Tên đăng nhập</Form.Label>
                                            <Form.Control type="text" name="username" value={user.username} onChange={change} required />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold"><FaEnvelope className="me-2 text-primary" />Email</Form.Label>
                                            <Form.Control type="email" name="email" value={user.email} onChange={change} required />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold"><FaLock className="me-2 text-primary" />Mật khẩu</Form.Label>
                                            <Form.Control type="password" name="password" value={user.password} onChange={change} required />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold"><FaLock className="me-2 text-primary" />Xác nhận</Form.Label>
                                            <Form.Control type="password" name="confirmPassword" value={user.confirmPassword} onChange={change} required />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <div className="d-grid gap-2 mt-4">
                                    <Button variant="primary" type="submit" size="lg" disabled={loading} className="rounded-pill fw-bold">
                                        {loading ? <Spinner animation="border" size="sm" /> : "TẠO TÀI KHOẢN"}
                                    </Button>
                                </div>
                            </Form>
                            <div className="text-center mt-4 text-muted">
                                Đã có tài khoản? <Link to="/login" className="text-primary fw-bold text-decoration-none">Đăng nhập ngay</Link>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Register;
