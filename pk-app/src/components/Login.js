import React, { useContext, useState } from "react";
import { Alert, Button, Form, Container, Row, Col, Card } from "react-bootstrap";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { FaUser, FaLock, FaSignInAlt } from 'react-icons/fa';
import cookie from 'react-cookies';

import MySpinner from "./layout/MySpinner";
import { MyUserContext } from "../config/MyContexts";
import Apis, { authApis, endpoints } from "../config/Apis";

const Login = () => {
    const [user, setUser] = useState({ username: '', password: '' });
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const nav = useNavigate();
    const [, dispatch] = useContext(MyUserContext);
    const [q] = useSearchParams();

    const change = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
        setErr(""); 
    };

    const login = async (e) => {
        e.preventDefault();
        setErr(""); 

        try {
            setLoading(true);
            let res = await Apis.post(endpoints['login'], user);

            if (res.data.status === 200) {
                const { token, user: userData } = res.data.data;
                cookie.save('token', token);

                let fullUser = userData;

                // Nếu là bệnh nhân, gọi API /patients/me để lấy ID bệnh nhân và mã BN
                if (userData.role === 'PATIENT') {
                    try {
                        const profileRes = await authApis().get(endpoints['current-patient']);
                        if (profileRes.data.status === 200) {
                            // fullUser bây giờ sẽ có đầy đủ id (của Patient), patientCode, fullName,...
                            fullUser = { ...userData, ...profileRes.data.data };
                        }
                    } catch (pErr) {
                        console.error("Lỗi lấy thông tin bệnh nhân chi tiết:", pErr);
                    }
                } else if (userData.role === 'DOCTOR') {
                     // Nếu là bác sĩ, có thể gọi API tương tự cho bác sĩ nếu bạn có
                }

                cookie.save('user', fullUser);
                dispatch({
                    "type": "login",
                    "payload": fullUser
                });
                
                let next = q.get('next');
                nav(next === null ? "/" : next);
            } else {
                setErr(res.data.message || "Đăng nhập thất bại.");
            }
        } catch (ex) {
            setErr("Tên đăng nhập hoặc mật khẩu không chính xác!");
            console.error(ex);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="my-5">
            <Row className="justify-content-center">
                <Col md={6} lg={5}>
                    <Card className="border-0 shadow-lg overflow-hidden">
                        <div className="bg-primary text-white text-center py-4">
                            <h3 className="mb-0 fw-bold">Chào mừng trở lại!</h3>
                            <p className="opacity-75 mb-0">Đăng nhập để tiếp tục</p>
                        </div>
                        <Card.Body className="p-4 p-md-5">
                            {err && <Alert variant="danger" className="mb-4">{err}</Alert>}
                            <Form onSubmit={login}>
                                <Form.Group className="mb-4">
                                    <Form.Label className="fw-bold"><FaUser className="me-2 text-primary" />Tên đăng nhập</Form.Label>
                                    <Form.Control 
                                        name="username" value={user.username} onChange={change} 
                                        type="text" placeholder="Nhập tên đăng nhập" required 
                                        className="bg-light border-0"
                                    />
                                </Form.Group>
                                <Form.Group className="mb-4">
                                    <Form.Label className="fw-bold"><FaLock className="me-2 text-primary" />Mật khẩu</Form.Label>
                                    <Form.Control 
                                        name="password" value={user.password} onChange={change} 
                                        type="password" placeholder="Nhập mật khẩu" required 
                                        className="bg-light border-0"
                                    />
                                </Form.Group>
                                <div className="d-grid gap-2">
                                    {loading ? (
                                        <Button variant="primary" disabled className="rounded-pill py-2"><MySpinner size="sm" /> Đang xử lý...</Button>
                                    ) : (
                                        <Button variant="primary" type="submit" className="rounded-pill py-2 fw-bold">Đăng nhập <FaSignInAlt className="ms-2" /></Button>
                                    )}
                                </div>
                            </Form>
                            <div className="text-center mt-4">
                                <span className="text-muted">Chưa có tài khoản? </span>
                                <Link to="/register" className="text-primary fw-bold text-decoration-none">Đăng ký ngay</Link>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Login;
