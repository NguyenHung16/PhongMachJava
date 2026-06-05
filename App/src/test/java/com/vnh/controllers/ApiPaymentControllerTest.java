package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.AppointmentDto;
import com.vnh.dto.BillDto;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Bills;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.Patients;
import com.vnh.pojo.Users;
import com.vnh.repositories.BillRepository;
import com.vnh.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiPaymentControllerTest {

    @Mock private PaymentService paymentService;
    @Mock private BillRepository billRepository;
    @Mock private Environment env;
    @InjectMocks private ApiPaymentController controller;

    @BeforeEach
    void setUp() {
        lenient().when(env.getProperty("vnp.tmn_code")).thenReturn("TESTCODE");
        lenient().when(env.getProperty("vnp.return_url")).thenReturn("http://localhost/return");
        lenient().when(env.getProperty("vnp.hash_secret")).thenReturn("secret");
        lenient().when(env.getProperty("vnp.pay_url")).thenReturn("http://sandbox.vnpay.vn/pay");
    }

    @Test
    void payAppointmentDepositReturnsConfirmedAppointment() {
        Users doctorUser = new Users();
        doctorUser.setFullName("Dr Test");
        Doctors doctor = new Doctors();
        doctor.setUserId(doctorUser);
        Users patientUser = new Users();
        patientUser.setFullName("Patient Test");
        Patients patient = new Patients();
        patient.setUserId(patientUser);
        Appointments appointment = new Appointments();
        appointment.setId(4);
        appointment.setDoctorId(doctor);
        appointment.setPatientId(patient);
        appointment.setStatus("CONFIRMED");

        when(paymentService.processAppointmentDeposit(4)).thenReturn(appointment);

        ResponseEntity<ApiResponse<AppointmentDto>> response = controller.payAppointmentDeposit(4);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("CONFIRMED", response.getBody().getData().getStatus());
    }

    @Test
    void payAppointmentDepositReturnsNotFoundWhenPaymentServiceReturnsNull() {
        when(paymentService.processAppointmentDeposit(404)).thenReturn(null);

        ResponseEntity<ApiResponse<AppointmentDto>> response = controller.payAppointmentDeposit(404);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void payBillReturnsPaidBill() {
        Bills bill = new Bills();
        bill.setId(8);
        bill.setPaymentStatus("PAID");
        bill.setTotalAmount(new BigDecimal("100000"));
        when(paymentService.processPayment(8, "MOCK")).thenReturn(true);
        when(billRepository.getBillById(8)).thenReturn(bill);

        ResponseEntity<ApiResponse<BillDto>> response = controller.payBill(8);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("PAID", response.getBody().getData().getPaymentStatus());
    }

    @Test
    void payBillReturnsNotFoundWhenPaymentFails() {
        when(paymentService.processPayment(9, "MOCK")).thenReturn(false);

        ResponseEntity<ApiResponse<BillDto>> response = controller.payBill(9);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void getVNPayUrlReturnsBadRequestWhenBillMissing() {
        when(billRepository.getBillById(10)).thenReturn(null);

        ResponseEntity<ApiResponse<String>> response = controller.getVNPayUrl(10, mock(HttpServletRequest.class));

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void getVNPayUrlReturnsPaymentUrlWhenBillExists() {
        Bills bill = new Bills();
        bill.setId(11);
        bill.setTotalAmount(new BigDecimal("120000"));
        when(billRepository.getBillById(11)).thenReturn(bill);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-FORWARDED-FOR")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        ResponseEntity<ApiResponse<String>> response = controller.getVNPayUrl(11, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody().getData());
        org.junit.jupiter.api.Assertions.assertTrue(response.getBody().getData().startsWith("http://sandbox.vnpay.vn/pay?"));
    }

    @Test
    void getVNPayUrlByAppointmentHandlesCreatedAndMissingBill() {
        Bills bill = new Bills();
        bill.setId(12);
        bill.setTotalAmount(new BigDecimal("100000"));
        when(paymentService.createBill(12)).thenReturn(bill);
        when(billRepository.getBillById(12)).thenReturn(bill);

        ResponseEntity<ApiResponse<String>> success = controller.getVNPayUrlByAppointment(12, mock(HttpServletRequest.class));

        assertEquals(200, success.getStatusCode().value());
        when(paymentService.createBill(13)).thenReturn(null);
        assertEquals(400, controller.getVNPayUrlByAppointment(13, mock(HttpServletRequest.class)).getStatusCode().value());
    }
}
