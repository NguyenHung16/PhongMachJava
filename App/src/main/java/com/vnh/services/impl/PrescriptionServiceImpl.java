package com.vnh.services.impl;

import com.vnh.dto.MedicineRequest;
import com.vnh.dto.PrescriptionRequest;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Bills;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.MedicalRecords;
import com.vnh.pojo.Medicines;
import com.vnh.pojo.PrescriptionDetails;
import com.vnh.pojo.PrescriptionDetailsPK;
import com.vnh.pojo.Prescriptions;
import com.vnh.pojo.Users;
import com.vnh.repositories.AppointmentRepository;
import com.vnh.repositories.BillRepository;
import com.vnh.repositories.MedicalRecordRepository;
import com.vnh.repositories.MedicineRepository;
import com.vnh.repositories.PrescriptionDetailsRepository;
import com.vnh.repositories.PrescriptionsRepository;
import com.vnh.repositories.UserRepository;
import com.vnh.services.PrescriptionService;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    @Autowired private PrescriptionsRepository prescriptionsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MedicalRecordRepository medicalRecordsRepository;
    @Autowired private PrescriptionDetailsRepository prescriptionDetailsRepository;
    @Autowired private MedicineRepository medicineRepository;
    @Autowired private BillRepository billRepository;
    @Autowired private AppointmentRepository appointmentRepository;

    @Override
    public Prescriptions createPrescription(PrescriptionRequest prescriptionRequest, Principal principal) {
        Users currentUser = userRepository.getUserByUsername(principal.getName());
        if (currentUser == null) throw new UsernameNotFoundException("Không tìm thấy người dùng");

        Doctors doctor = currentUser.getDoctors();
        if (doctor == null) throw new IllegalArgumentException("Người dùng không phải là bác sĩ.");

        MedicalRecords medicalRecord = medicalRecordsRepository.getMedicalRecordById(prescriptionRequest.getMedicalRecordId());
        if (medicalRecord == null) throw new IllegalArgumentException("Không tìm thấy hồ sơ bệnh án.");

        Prescriptions prescription = new Prescriptions();
        prescription.setMedicalRecordId(medicalRecord);
        prescription.setDoctorId(doctor);
        prescription.setPrescriptionDate(new Date());
        prescriptionsRepository.savePrescription(prescription);

        BigDecimal medicineTotal = BigDecimal.ZERO;
        for (MedicineRequest medicineRequest : prescriptionRequest.getMedicines()) {
            Medicines medicine = medicineRepository.getMedicineById(medicineRequest.getMedicineId());
            if (medicine == null) continue;

            int quantity = parseQuantity(medicineRequest.getDosage());
            PrescriptionDetailsPK detailsPK = new PrescriptionDetailsPK();
            detailsPK.setPrescriptionId(prescription.getId());
            detailsPK.setMedicineId(medicine.getId());

            PrescriptionDetails detail = new PrescriptionDetails();
            detail.setPrescriptionDetailsPK(detailsPK);
            detail.setPrescriptions(prescription);
            detail.setMedicines(medicine);
            detail.setQuantity(quantity);
            detail.setInstruction("Tần suất: " + medicineRequest.getFrequency() + ". " + medicineRequest.getInstructions());
            prescriptionDetailsRepository.SavePrescriptionDetails(detail);

            if (medicine.getPrice() != null && quantity > 0) {
                medicineTotal = medicineTotal.add(BigDecimal.valueOf(medicine.getPrice()).multiply(BigDecimal.valueOf(quantity)));
            }
        }

        completeAppointmentAndUpdateBill(medicalRecord, medicineTotal);
        return prescription;
    }

    @Override
    public Prescriptions addPrescription(Prescriptions p) {
        prescriptionsRepository.savePrescription(p);
        return p;
    }

    private int parseQuantity(String dosage) {
        try {
            return Integer.parseInt(dosage);
        } catch (Exception e) {
            return 0;
        }
    }

    private void completeAppointmentAndUpdateBill(MedicalRecords medicalRecord, BigDecimal medicineTotal) {
        Appointments appointment = medicalRecord.getAppointmentId();
        if (appointment == null || appointment.getId() == null) return;

        Bills bill = billRepository.getBillByAppointmentId(appointment.getId());
        if (bill != null) {
            billRepository.updateTotalAmount(bill.getId(), new BigDecimal("100000").add(medicineTotal));
        }
        appointment.setStatus("COMPLETED");
        appointmentRepository.saveAppointment(appointment);
    }
}
