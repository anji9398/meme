package com.momsme.momsme.district_and_mandal.controller;

import com.momsme.momsme.address.service.MandalResponse;
import com.momsme.momsme.address.service.VillageResponse;
import com.momsme.momsme.district_and_mandal.service.DistrictAndMandalService;
import com.momsme.momsme.address.service.DistrictResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class DistrictAndMandalController {

    private final DistrictAndMandalService districtAndMandalService;

    @GetMapping("/districts")
    public List<DistrictResponse> getDistricts() {
        return districtAndMandalService.getAllDistricts();
    }

    @GetMapping("/mandals/{districtId}")
    public List<MandalResponse> getMandals(@PathVariable Long districtId) {
        return districtAndMandalService.getMandalsByDistrict(districtId);
    }

    @GetMapping("/villages/{mandalId}")
    public List<VillageResponse> getVillages(@PathVariable Long mandalId) {
        return districtAndMandalService.getVillagesByMandal(mandalId);
    }
}

