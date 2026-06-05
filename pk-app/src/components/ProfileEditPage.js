import React, { useState, useEffect, useContext } from 'react';
import { Container, Form, Button, Alert, Card, Image, Row, Col, Spinner, Badge } from 'react-bootstrap';
import { FaUserCircle, FaEnvelope, FaPhone, FaVenusMars, FaSave, FaCamera } from 'react-icons/fa';
import cookie from 'react-cookies';
import { authApis, endpoints, getImageUrl } from '../config/Apis';
import { MyUserContext } from '../config/MyContexts';

const ProfileEditPage = () => {
    const [user, dispatch] = useContext(MyUserContext);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);
    const [newAvatar, setNewAvatar] = useState(null);
    const [previewAvatar, setPreviewAvatar] = useState(null);

    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        phoneNumber: '',
        gender: '',
    });

    useEffect(() => {
        if (user) {
            setFormData({
                fullName: user.fullName || '',
                email: user.email || '',
                phoneNumber: user.phoneNumber || '',
                gender: user.gender || '',
            });
            setPreviewAvatar(getImageUrl(user.avatar));
        }
    }, [user]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleAvatarChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setNewAvatar(file);
            setPreviewAvatar(URL.createObjectURL(file));
        }
    };

    const handleFormSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        setLoading(true);

        const userId = user?.id;
        
        if (!userId) {
            setMessage({ type: 'danger', text: 'Lỗi: Không tìm thấy ID người dùng. Thử đăng xuất và đăng nhập lại.' });
            setLoading(false);
            return;
        }

        const data = new FormData();
        data.append('fullName', formData.fullName);
        data.append('email', formData.email);
        data.append('phoneNumber', formData.phoneNumber);
        data.append('gender', formData.gender);
        if (newAvatar) {
            data.append('avatar', newAvatar);
        }

        try {
            // BE của bạn dùng @PostMapping cho việc update profile
            const res = await authApis().post(endpoints['update-profile'](userId), data, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });

            if (res.data.status === 200) {
                const updatedUser = { ...user, ...res.data.data };
                setMessage({ type: 'success', text: 'Cập nhật hồ sơ thành công!' });
                cookie.save('user', updatedUser);
                dispatch({ type: 'login', payload: updatedUser });
            } else {
                setMessage({ type: 'danger', text: res.data.message || 'Cập nhật thất bại.' });
            }
        } catch (ex) {
            console.error("Lỗi cập nhật:", ex);
            setMessage({ type: 'danger', text: 'Lỗi kết nối máy chủ.' });
        } finally {
            setLoading(false);
        }
    };

    if (!user) return <Container className="my-5"><Alert variant="danger">Vui lòng đăng nhập!</Alert></Container>;

    return (
        <Container className="py-5">
            <Row className="justify-content-center">
                <Col lg={10}>
                    <Card className="border-0 shadow-lg">
                        <Card.Header className="bg-primary text-white py-4 border-0 text-center">
                            <h2 className="mb-0 fw-bold">CHỈNH SỬA THÔNG TIN CÁ NHÂN</h2>
                        </Card.Header>
                        <Card.Body className="p-4 p-md-5">
                            {message && <Alert variant={message.type} dismissible onClose={() => setMessage(null)}>{message.text}</Alert>}
                            <Form onSubmit={handleFormSubmit}>
                                <Row className="gy-4">
                                    <Col md={4} className="text-center border-end">
                                        <div className="position-relative d-inline-block mb-3">
                                            <Image src={previewAvatar} roundedCircle crossOrigin="anonymous" className="shadow border border-4 border-white" style={{ width: '180px', height: '180px', objectFit: 'cover' }} onError={(e) => { e.target.src = "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y" }} />
                                            <Button variant="primary" size="sm" className="position-absolute bottom-0 end-0 rounded-circle shadow p-2" onClick={() => document.getElementById('avatar-upload').click()}><FaCamera size={18} /></Button>
                                            <input type="file" id="avatar-upload" hidden onChange={handleAvatarChange} accept="image/*" />
                                        </div>
                                        <h5 className="fw-bold">{user.fullName || user.username}</h5>
                                        <Badge bg="info" className="px-3 py-2 rounded-pill mt-2">{user.role}</Badge>
                                    </Col>
                                    <Col md={8} className="ps-md-5">
                                        <Row>
                                            <Col md={12} className="mb-4">
                                                <Form.Group>
                                                    <Form.Label className="fw-bold"><FaUserCircle className="me-2 text-primary" />Họ và tên</Form.Label>
                                                    <Form.Control className="bg-light border-0 py-2" type="text" name="fullName" value={formData.fullName} onChange={handleInputChange} required />
                                                </Form.Group>
                                            </Col>
                                            <Col md={6} className="mb-4">
                                                <Form.Group>
                                                    <Form.Label className="fw-bold"><FaEnvelope className="me-2 text-primary" />Email</Form.Label>
                                                    <Form.Control className="bg-light border-0 py-2" type="email" name="email" value={formData.email} onChange={handleInputChange} required />
                                                </Form.Group>
                                            </Col>
                                            <Col md={6} className="mb-4">
                                                <Form.Group>
                                                    <Form.Label className="fw-bold"><FaPhone className="me-2 text-primary" />Số điện thoại</Form.Label>
                                                    <Form.Control className="bg-light border-0 py-2" type="tel" name="phoneNumber" value={formData.phoneNumber} onChange={handleInputChange} />
                                                </Form.Group>
                                            </Col>
                                            <Col md={6} className="mb-4">
                                                <Form.Group>
                                                    <Form.Label className="fw-bold"><FaVenusMars className="me-2 text-primary" />Giới tính</Form.Label>
                                                    <Form.Select className="bg-light border-0 py-2" name="gender" value={formData.gender} onChange={handleInputChange}>
                                                        <option value="">Chọn giới tính</option>
                                                        <option value="MALE">Nam</option>
                                                        <option value="FEMALE">Nữ</option>
                                                    </Form.Select>
                                                </Form.Group>
                                            </Col>
                                        </Row>
                                        <div className="d-grid mt-4">
                                            <Button variant="primary" type="submit" size="lg" disabled={loading} className="rounded-pill fw-bold shadow">{loading ? <Spinner animation="border" size="sm" /> : <><FaSave className="me-2" /> LƯU THAY ĐỔI</>}</Button>
                                        </div>
                                    </Col>
                                </Row>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default ProfileEditPage;
