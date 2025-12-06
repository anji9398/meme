package com.momsme.momsme.controller;

import com.momsme.momsme.repository.DistrictVillagesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/district-villages")
public class DistrictVillagesController {

    private final DistrictVillagesRepository repository;

    public DistrictVillagesController(DistrictVillagesRepository repository) {
        this.repository = repository;
    }

    // GET : /api/district-villages/Adilabad
    @GetMapping("/{districtName}")
    public ResponseEntity<?> getDistrictVillages(@PathVariable String districtName) {
        return repository.findByDistrictNameIgnoreCase(districtName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

