package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.MedicineDto;
import com.vnh.pojo.Medicines;
import com.vnh.services.MedicineServices;
import com.vnh.utils.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicines")
public class ApiMedicineController {

    @Autowired private MedicineServices medicineService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicineDto>>> getMedicines(@RequestParam Map<String, String> params) {
        List<Medicines> list = medicineService.getMedicines(params);
        List<MedicineDto> dtos = list.stream().map(DtoMapper::toMedicineDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicineDto>> getById(@PathVariable int id) {
        Medicines m = medicineService.getMedicineById(id);
        if (m != null) {
            return ResponseEntity.ok(ApiResponse.success(DtoMapper.toMedicineDto(m)));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(404, "Thuốc không tồn tại"));
    }
}
