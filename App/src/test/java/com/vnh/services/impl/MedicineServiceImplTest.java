package com.vnh.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.vnh.pojo.Medicines;
import com.vnh.repositories.MedicineRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineServiceImplTest {

    @Mock private MedicineRepository mediRepo;
    @Mock private Cloudinary cloudinary;
    @InjectMocks private MedicineServiceImpl service;

    @Test
    void delegatesMedicineQueriesAndDelete() {
        Medicines medicine = new Medicines();
        Map<String, String> params = Map.of("kw", "para");
        when(mediRepo.getMedicines(params)).thenReturn(List.of(medicine));
        when(mediRepo.getMedicineById(1)).thenReturn(medicine);
        when(mediRepo.getExpiringMedicines(30)).thenReturn(List.of(medicine));
        when(mediRepo.getLowStockMedicines(10)).thenReturn(List.of(medicine));

        assertEquals(List.of(medicine), service.getMedicines(params));
        assertSame(medicine, service.getMedicineById(1));
        assertEquals(List.of(medicine), service.getExpiringMedicines(30));
        assertEquals(List.of(medicine), service.getLowStockMedicines(10));
        service.deleteMedicine(1);
        verify(mediRepo).deleteMedicine(1);
    }

    @Test
    void addOrUpdateMedicineUploadsImageWhenProvided() throws IOException {
        Medicines medicine = new Medicines();
        MultipartFile file = mock(MultipartFile.class);
        Uploader uploader = mock(Uploader.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn(new byte[]{1});
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("secure_url", "https://img.test/a.png"));
        when(mediRepo.addOrUpdateMedicine(medicine)).thenReturn(medicine);

        Medicines saved = service.addOrUpdateMedicine(medicine, file);

        assertSame(medicine, saved);
        assertEquals("https://img.test/a.png", medicine.getImageUrl());
        verify(mediRepo).addOrUpdateMedicine(medicine);
    }

    @Test
    void addOrUpdateMedicineStillSavesWhenUploadFailsOrFileEmpty() throws IOException {
        Medicines failedUpload = new Medicines();
        MultipartFile badFile = mock(MultipartFile.class);
        Uploader uploader = mock(Uploader.class);
        when(badFile.isEmpty()).thenReturn(false);
        when(badFile.getBytes()).thenThrow(new IOException("broken"));
        when(cloudinary.uploader()).thenReturn(uploader);
        when(mediRepo.addOrUpdateMedicine(failedUpload)).thenReturn(failedUpload);

        assertSame(failedUpload, service.addOrUpdateMedicine(failedUpload, badFile));
        assertNull(failedUpload.getImageUrl());

        Medicines noImage = new Medicines();
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        when(mediRepo.addOrUpdateMedicine(noImage)).thenReturn(noImage);
        assertSame(noImage, service.addOrUpdateMedicine(noImage, emptyFile));
    }
}
