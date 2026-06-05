import React, { useContext, memo } from 'react';
import { Navbar, Nav, Container, Image, NavDropdown, Button } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import {
    FaSignOutAlt, FaNotesMedical, FaCalendarAlt,
    FaCalendarCheck, FaUserInjured, FaPills,
    FaFileMedical, FaUserCircle, FaStethoscope, FaFileInvoiceDollar
} from 'react-icons/fa';
import cookie from 'react-cookies';
import { useNavigate } from 'react-router-dom';
import { MyUserContext } from '../../config/MyContexts';
import { getImageUrl } from '../../config/Apis';

const Header = () => {
    const [user, dispatch] = useContext(MyUserContext);
    const nav = useNavigate();

    const handleLogout = () => {
        cookie.remove('token');
        cookie.remove('user');
        cookie.remove('patientId');
        cookie.remove('doctorId');
        dispatch({ "type": "logout" });
        nav("/login");
    };

    const avatarPath = getImageUrl(user?.avatar);

    const patientLinks = (
        <>
            <LinkContainer to="/booking"><Nav.Link><FaCalendarAlt className="me-1 text-primary" /> Đặt lịch khám</Nav.Link></LinkContainer>
            <LinkContainer to="/medical-records"><Nav.Link><FaNotesMedical className="me-1 text-primary" /> Hồ sơ bệnh án</Nav.Link></LinkContainer>
            <LinkContainer to="/bills"><Nav.Link><FaFileInvoiceDollar className="me-1 text-primary" /> Hóa đơn</Nav.Link></LinkContainer>
        </>
    );

    const doctorLinks = (
        <>
            <LinkContainer to="/doctors/my-appointments"><Nav.Link><FaCalendarCheck className="me-1 text-primary" /> Lịch làm việc</Nav.Link></LinkContainer>
            <LinkContainer to="/patients"><Nav.Link><FaUserInjured className="me-1 text-primary" /> Bệnh nhân</Nav.Link></LinkContainer>
            <NavDropdown title={<span className="text-primary"><FaFileMedical className="me-1" /> Nghiệp vụ</span>} id="doctor-nav-dropdown">
                <LinkContainer to="/prescriptions"><NavDropdown.Item><FaPills className="me-2 text-primary" /> Kê đơn thuốc</NavDropdown.Item></LinkContainer>
                <LinkContainer to="/doctor/create-medical-record"><NavDropdown.Item><FaFileMedical className="me-2 text-primary" /> Tạo hồ sơ khám</NavDropdown.Item></LinkContainer>
            </NavDropdown>
        </>
    );

    return (
        <Navbar bg="white" expand="lg" sticky="top" className="main-navbar border-bottom shadow-sm py-2">
            <Container>
                <LinkContainer to="/">
                    <Navbar.Brand className="d-flex align-items-center">
                        <FaStethoscope className="text-primary me-2" size={30} />
                        <span className="fw-bold text-primary" style={{ letterSpacing: '1px' }}>PHÒNG KHÁM QH</span>
                    </Navbar.Brand>
                </LinkContainer>
                
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ms-auto align-items-center">
                        <LinkContainer to="/">
                            <Nav.Link className="fw-500">Trang chủ</Nav.Link>
                        </LinkContainer>

                        {user ? (
                            <>
                                {user.role === 'DOCTOR' ? doctorLinks : patientLinks}
                                
                                <NavDropdown 
                                    title={
                                        <div className="d-inline-flex align-items-center border rounded-pill ps-2 pe-3 py-1 bg-light">
                                            <Image
                                                src={avatarPath}
                                                roundedCircle
                                                crossOrigin="anonymous"
                                                style={{ width: '28px', height: '28px', border: '1px solid #ddd', objectFit: 'cover' }}
                                                className="me-2"
                                            />
                                            <span className="small fw-bold text-dark">{user.fullName || user.username}</span>
                                        </div>
                                    } 
                                    id="user-dropdown"
                                    align="end"
                                    className="ms-lg-3 no-caret"
                                >
                                    <LinkContainer to="/profile"><NavDropdown.Item><FaUserCircle className="me-2 text-primary" /> Trang cá nhân</NavDropdown.Item></LinkContainer>
                                    <LinkContainer to="/profile/edit"><NavDropdown.Item>Chỉnh sửa thông tin</NavDropdown.Item></LinkContainer>
                                    <NavDropdown.Divider />
                                    <NavDropdown.Item onClick={handleLogout} className="text-danger"><FaSignOutAlt className="me-2" /> Đăng xuất</NavDropdown.Item>
                                </NavDropdown>
                            </>
                        ) : (
                            <div className="d-flex gap-2 ms-lg-3">
                                <LinkContainer to="/login"><Button variant="outline-primary" size="sm" className="rounded-pill px-4 fw-bold">Đăng nhập</Button></LinkContainer>
                                <LinkContainer to="/register"><Button variant="primary" size="sm" className="rounded-pill px-4 fw-bold shadow-sm">Đăng ký</Button></LinkContainer>
                            </div>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
            <style dangerouslySetInnerHTML={{ __html: `.no-caret::after { display: none !important; }` }} />
        </Navbar>
    );
};

export default memo(Header);
