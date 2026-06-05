import React from 'react';
import { Container, Row, Col, Card, Button, Badge } from 'react-bootstrap';
import { FaUserMd, FaCalendarCheck, FaNotesMedical, FaRegMoneyBillAlt, FaArrowRight, FaShieldAlt, FaClock, FaStethoscope } from 'react-icons/fa';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="home-page">
            {/* Hero Section */}
            <section className="hero-section py-5 mb-5 text-white" style={{ background: 'linear-gradient(135deg, #00a8cc 0%, #005073 100%)', borderRadius: '0 0 50px 50px', margin: '0 -15px' }}>
                <Container>
                    <Row className="align-items-center py-5">
                        <Col lg={6} className="text-center text-lg-start">
                            <Badge bg="info" className="mb-3 px-3 py-2 rounded-pill">Chào mừng đến với QH Clinic</Badge>
                            <h1 className="display-3 fw-bold mb-4">Sức khỏe của bạn là sứ mệnh của chúng tôi</h1>
                            <p className="lead mb-5 opacity-90">
                                Hệ thống quản lý phòng khám hiện đại, giúp bạn đặt lịch hẹn, theo dõi hồ sơ bệnh án và kết nối với bác sĩ chuyên khoa hàng đầu một cách nhanh chóng.
                            </p>
                            <div className="d-flex gap-3 justify-content-center justify-content-lg-start">
                                <Link to="/booking">
                                    <Button variant="light" size="lg" className="text-primary fw-bold px-4 rounded-pill shadow">
                                        Đặt lịch ngay <FaArrowRight className="ms-2" />
                                    </Button>
                                </Link>
                                <Link to="/register">
                                    <Button variant="outline-light" size="lg" className="px-4 rounded-pill">
                                        Tìm hiểu thêm
                                    </Button>
                                </Link>
                            </div>
                        </Col>
                        <Col lg={6} className="d-none d-lg-block text-center">
                            <FaStethoscope size={300} className="opacity-25" style={{ transform: 'rotate(-15deg)' }} />
                        </Col>
                    </Row>
                </Container>
            </section>

            {/* Stats/Quick Info */}
            <Container className="mt-n5 mb-5">
                <Row className="g-4">
                    {[
                        { icon: <FaClock />, title: "24/7 Phục vụ", text: "Hỗ trợ khẩn cấp" },
                        { icon: <FaUserMd />, title: "50+ Bác sĩ", text: "Chuyên gia hàng đầu" },
                        { icon: <FaShieldAlt />, title: "An toàn", text: "Bảo mật thông tin" },
                    ].map((item, idx) => (
                        <Col md={4} key={idx}>
                            <Card className="border-0 shadow-sm text-center py-3">
                                <Card.Body>
                                    <div className="text-primary mb-2 fs-2">{item.icon}</div>
                                    <h5 className="fw-bold mb-1">{item.title}</h5>
                                    <p className="text-muted mb-0">{item.text}</p>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            </Container>

            {/* Services Section */}
            <Container className="py-5">
                <div className="text-center mb-5">
                    <h2 className="section-title d-inline-block">Dịch vụ tiêu biểu</h2>
                    <p className="text-muted mt-3 mx-auto" style={{ maxWidth: '600px' }}>
                        Chúng tôi cung cấp các giải pháp y tế toàn diện với công nghệ hiện đại nhất.
                    </p>
                </div>

                <Row className="g-4">
                    <Col md={6} lg={3}>
                        <Card className="h-100 border-0 shadow-sm hover-up">
                            <Card.Body className="p-4 text-center">
                                <div className="bg-light-blue p-3 rounded-circle d-inline-block mb-3">
                                    <FaCalendarCheck size={40} className="text-primary" />
                                </div>
                                <h4 className="fw-bold">Đặt lịch trực tuyến</h4>
                                <Card.Text className="text-muted">
                                    Chọn bác sĩ và giờ khám phù hợp với lịch trình của bạn chỉ trong vài click.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={6} lg={3}>
                        <Card className="h-100 border-0 shadow-sm hover-up">
                            <Card.Body className="p-4 text-center">
                                <div className="bg-light-green p-3 rounded-circle d-inline-block mb-3">
                                    <FaNotesMedical size={40} className="text-success" />
                                </div>
                                <h4 className="fw-bold">Hồ sơ điện tử</h4>
                                <Card.Text className="text-muted">
                                    Xem lại lịch sử khám bệnh và đơn thuốc mọi lúc mọi nơi qua tài khoản cá nhân.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={6} lg={3}>
                        <Card className="h-100 border-0 shadow-sm hover-up">
                            <Card.Body className="p-4 text-center">
                                <div className="bg-light-info p-3 rounded-circle d-inline-block mb-3">
                                    <FaUserMd size={40} className="text-info" />
                                </div>
                                <h4 className="fw-bold">Bác sĩ chuyên khoa</h4>
                                <Card.Text className="text-muted">
                                    Đội ngũ bác sĩ giàu kinh nghiệm từ các bệnh viện lớn trên toàn quốc.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={6} lg={3}>
                        <Card className="h-100 border-0 shadow-sm hover-up">
                            <Card.Body className="p-4 text-center">
                                <div className="bg-light-warning p-3 rounded-circle d-inline-block mb-3">
                                    <FaRegMoneyBillAlt size={40} className="text-warning" />
                                </div>
                                <h4 className="fw-bold">Thanh toán linh hoạt</h4>
                                <Card.Text className="text-muted">
                                    Hỗ trợ nhiều phương thức thanh toán an toàn, minh bạch và nhanh chóng.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>

            <style dangerouslySetInnerHTML={{ __html: `
                .mt-n5 { margin-top: -3rem !important; }
                .hover-up { transition: all 0.3s ease; }
                .hover-up:hover { transform: translateY(-10px); }
                .bg-light-blue { background: #e3f2fd; }
                .bg-light-green { background: #e8f5e9; }
                .bg-light-info { background: #e0f7fa; }
                .bg-light-warning { background: #fffde7; }
                .opacity-90 { opacity: 0.9; }
            `}} />
        </div>
    );
};

export default Home;
