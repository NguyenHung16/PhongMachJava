package com.vnh.utils;

import com.vnh.dto.AppointmentDto;
import com.vnh.dto.BillDto;
import com.vnh.dto.DoctorDto;
import com.vnh.dto.MedicineDto;
import com.vnh.dto.PatientDto;
import com.vnh.dto.SpecialtyDto;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Bills;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.Medicines;
import com.vnh.pojo.Patients;
import com.vnh.pojo.Specialties;
import com.vnh.pojo.Users;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DtoMapperTest {

    @Test
    void mapsNullInputsToNull() {
        assertNull(DtoMapper.toSpecialtyDto(null));
        assertNull(DtoMapper.toDoctorDto(null));
        assertNull(DtoMapper.toPatientDto(null));
        assertNull(DtoMapper.toMedicineDto(null));
        assertNull(DtoMapper.toAppointmentDto(null));
        assertNull(DtoMapper.toBillDto(null));
    }

    @Test
    void mapsSpecialtyDoctorPatientAndMedicine() {
        Specialties specialty = new Specialties();
        specialty.setId(1);
        specialty.setName("Tim mạch");
        specialty.setDescription("Khám tim");
        SpecialtyDto specialtyDto = DtoMapper.toSpecialtyDto(specialty);
        assertEquals(1, specialtyDto.getId());
        assertEquals("Tim mạch", specialtyDto.getName());

        Users doctorUser = new Users();
        doctorUser.setFullName("Bác sĩ A");
        doctorUser.setUsername("doctor");
        doctorUser.setEmail("doctor@test.com");
        doctorUser.setPhoneNumber("0909");
        doctorUser.setAvatar("avatar.png");
        Doctors doctor = new Doctors();
        doctor.setId(2);
        doctor.setDoctorCode("DOC2");
        doctor.setQualification("Thạc sĩ");
        doctor.setBiography("Kinh nghiệm");
        doctor.setUserId(doctorUser);
        doctor.setSpecialtyId(specialty);
        DoctorDto doctorDto = DtoMapper.toDoctorDto(doctor);
        assertEquals("DOC2", doctorDto.getDoctorCode());
        assertEquals("Bác sĩ A", doctorDto.getFullName());
        assertEquals("Tim mạch", doctorDto.getSpecialty().getName());

        Users patientUser = new Users();
        patientUser.setFullName("Nguyễn Văn B");
        patientUser.setUsername("patient");
        patientUser.setEmail("patient@test.com");
        patientUser.setPhoneNumber("0888");
        patientUser.setAvatar("p.png");
        Patients patient = new Patients();
        patient.setId(3);
        patient.setPatientCode("P3");
        patient.setUserId(patientUser);
        PatientDto patientDto = DtoMapper.toPatientDto(patient);
        assertEquals("P3", patientDto.getPatientCode());
        assertEquals("Nguyễn Văn B", patientDto.getFullName());

        Medicines medicine = new Medicines();
        medicine.setId(4);
        medicine.setName("Paracetamol");
        medicine.setDosageForm("Viên");
        medicine.setManufacturer("VN");
        medicine.setStockQuantity(20);
        medicine.setUnit("viên");
        medicine.setExpiryDate(new Date());
        medicine.setPrice(5000.0);
        medicine.setImageUrl("m.png");
        MedicineDto medicineDto = DtoMapper.toMedicineDto(medicine);
        assertEquals("Paracetamol", medicineDto.getName());
        assertEquals(5000.0, medicineDto.getPrice());
    }

    @Test
    void mapsAppointmentAndBillWithFallbacks() {
        Appointments missingNames = new Appointments();
        missingNames.setId(5);
        AppointmentDto fallbackAppointment = DtoMapper.toAppointmentDto(missingNames);
        assertEquals("N/A", fallbackAppointment.getPatientName());
        assertEquals("N/A", fallbackAppointment.getDoctorName());
        assertEquals("N/A", fallbackAppointment.getSpecialtyName());

        Users patientUser = new Users();
        patientUser.setFullName("Bệnh nhân");
        Patients patient = new Patients();
        patient.setId(6);
        patient.setUserId(patientUser);
        Users doctorUser = new Users();
        doctorUser.setFullName("Bác sĩ");
        Specialties specialty = new Specialties();
        specialty.setName("Nhi khoa");
        Doctors doctor = new Doctors();
        doctor.setUserId(doctorUser);
        doctor.setSpecialtyId(specialty);

        Appointments appointment = new Appointments();
        appointment.setId(7);
        appointment.setPatientId(patient);
        appointment.setDoctorId(doctor);
        appointment.setReason("Khám tổng quát");
        appointment.setStatus("CONFIRMED");
        AppointmentDto appointmentDto = DtoMapper.toAppointmentDto(appointment);
        assertEquals(6, appointmentDto.getPatientId());
        assertEquals("Bệnh nhân", appointmentDto.getPatientName());
        assertEquals("Bác sĩ", appointmentDto.getDoctorName());
        assertEquals("Nhi khoa", appointmentDto.getSpecialtyName());

        Bills bill = new Bills();
        bill.setId(8);
        bill.setTotalAmount(new BigDecimal("150000"));
        bill.setPaymentStatus("PAID");
        bill.setAppointmentId(appointment);
        bill.setPatientId(patient);
        BillDto billDto = DtoMapper.toBillDto(bill);
        assertEquals(8, billDto.getId());
        assertEquals(7, billDto.getAppointmentId());
        assertEquals("Bệnh nhân", billDto.getPatientName());
        assertEquals(new BigDecimal("150000"), billDto.getTotalAmount());
    }
}
