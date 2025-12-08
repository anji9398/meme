package com.momsme.momsme.district_and_mandal.controller;

import com.momsme.momsme.address.service.MandalResponse;
import com.momsme.momsme.address.service.VillageResponse;
import com.momsme.momsme.district_and_mandal.service.DistrictAndMandalService;
import com.momsme.momsme.address.service.DistrictResponse;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import com.momsme.momsme.model.MsmeUnitDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping
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
    public Set<VillageResponse> getVillages(@PathVariable Long mandalId) {
        return districtAndMandalService.getVillagesByMandal(mandalId);
    }

    @GetMapping("/units")
    public ResponseEntity<List<MsmeUnitDetails>> getByVillage(@PathParam("village") String village,
                                                              @PathParam("mandal") String madal) {
        List<MsmeUnitDetails> details = districtAndMandalService.getByVillage(village,madal);
        if (details.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(details);
    }
}

