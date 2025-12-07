package com.momsme.momsme.address.controller;

import com.momsme.momsme.address.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.management.LockInfo;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final VillageDetectionService villageService;

    @PostMapping("/identify-village")
    public ResponseEntity<?> identifyVillage(@RequestBody AddressDetectionRequest request) {

        String info = villageService.detectVillage(request);

        if (info == null) {
            return ResponseEntity.ok(
                    new AddressDetectionResponse(false, "Village not found in address", null)
            );
        }
        return ResponseEntity.ok(info);
    }

//    @GetMapping("/download")
//    public ResponseEntity<byte[]> downloadExcel(@RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "10") int end) {
//
//        byte[] excelFile = villageService.detectVillageExcel(start,end);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=msme.xlsx")
//                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                .body(excelFile);
//    }
}
