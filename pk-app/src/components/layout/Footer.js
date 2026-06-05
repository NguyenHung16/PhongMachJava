import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import { FaFacebook, FaTwitter, FaInstagram, FaPhoneAlt, FaEnvelope, FaMapMarkerAlt, FaGlobe } from 'react-icons/fa';

const Footer = () => {
    return (
        <footer className="bg-white border-top py-5 mt-auto">
            <Container>
                <Row className="gy-4">
                    <Col lg={4} md={12}>
                        <div className="d-flex align-items-center mb-3">
                            <img src="/logo.svg" alt="Logo" width="40" className="me-2" />
                            <h4 className="fw-bold text-primary mb-0">PHÒNG KHÁM QH</h4>
                        </div>
                        <p className="text-muted mb-4" style={{ maxWidth: '350px' }}>
                            Tự hào là hệ thống phòng khám đa khoa uy tín hàng đầu, mang đến dịch vụ chăm sóc sức khỏe chất lượng cao với đội ngũ chuyên gia tận tâm.
                        </p>
                        <div className="d-flex gap-3">
                            {[FaFacebook, FaTwitter, FaInstagram, FaGlobe].map((Icon, idx) => (
                                <a key={idx} href="#!" className="bg-light text-primary p-2 rounded-circle hover-primary transition">
                                    <Icon size={18} />
                                </a>
                            ))}
                        </div>
                    </Col>
                    
                    <Col lg={2} md={4} sm={6}>
                        <h6 className="fw-bold text-dark mb-3">Dịch vụ</h6>
                        <ul className="list-unstyled small text-muted">
                            <li className="mb-2">Khám tổng quát</li>
                            <li className="mb-2">Đặt lịch online</li>
                            <li className="mb-2">Hồ sơ điện tử</li>
                            <li className="mb-2">Tư vấn từ xa</li>
                        </ul>
                    </Col>

                    <Col lg={2} md={4} sm={6}>
                        <h6 className="fw-bold text-dark mb-3">Hỗ trợ</h6>
                        <ul className="list-unstyled small text-muted">
                            <li className="mb-2">Hướng dẫn đặt lịch</li>
                            <li className="mb-2">Chính sách bảo mật</li>
                            <li className="mb-2">Câu hỏi thường gặp</li>
                            <li className="mb-2">Góp ý dịch vụ</li>
                        </ul>
                    </Col>

                    <Col lg={4} md={4}>
                        <h6 className="fw-bold text-dark mb-3">Liên hệ</h6>
                        <div className="small text-muted mb-2">
                            <FaMapMarkerAlt className="text-primary me-2" /> 371 Nguyễn Kiệm, Gò Vấp, TP.HCM
                        </div>
                        <div className="small text-muted mb-2">
                            <FaPhoneAlt className="text-primary me-2" /> 1900 1234 - (028) 3838 3838
                        </div>
                        <div className="small text-muted mb-2">
                            <FaEnvelope className="text-primary me-2" /> contact@phongkhamqh.com
                        </div>
                    </Col>
                </Row>
                
                <hr className="my-5 opacity-50" />
                
                <div className="d-flex flex-column flex-md-row justify-content-between align-items-center small text-muted">
                    <p className="mb-md-0">© 2025 QH Clinic Ecosystem. All rights reserved.</p>
                    <div className="d-flex gap-4">
                        <a href="#!" className="text-decoration-none text-muted">Điều khoản</a>
                        <a href="#!" className="text-decoration-none text-muted">Bảo mật</a>
                        <a href="#!" className="text-decoration-none text-muted">Cookies</a>
                    </div>
                </div>
            </Container>
            <style dangerouslySetInnerHTML={{ __html: `
                .hover-primary:hover {
                    background-color: var(--medical-blue) !important;
                    color: white !important;
                    transform: translateY(-3px);
                }
                .transition { transition: all 0.3s ease; }
            `}} />
        </footer>
    );
};

export default Footer;
