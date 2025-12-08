package com.momsme.momsme.district_and_mandal.service;

import com.momsme.momsme.address.repository.VillageRepository;
import com.momsme.momsme.address.repository.MandalRepository;
import com.momsme.momsme.address.repository.DistrictRepository;
import com.momsme.momsme.address.service.DistrictResponse;
import com.momsme.momsme.address.service.MandalResponse;
import com.momsme.momsme.address.service.VillageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictAndMandalServiceAdapter implements DistrictAndMandalService {

    private final DistrictRepository districtRepository;
    private final MandalRepository mandalRepository;
    private final VillageRepository villageRepository;

    @Override
    public List<DistrictResponse> getAllDistricts() {
        return districtRepository.findAll()
                .stream()
                .map(d -> new DistrictResponse(d.getId(), d.getName()))
                .toList();
    }

    @Override
    public List<MandalResponse> getMandalsByDistrict(Long districtId) {
        return mandalRepository.findByDistrict_Id(districtId)
                .stream()
                .map(m -> new MandalResponse(m.getId(), m.getName()))
                .toList();
    }

    @Override
    public List<VillageResponse> getVillagesByMandal(Long mandalId) {
        return villageRepository.findByMandal_Id(mandalId)
                .stream()
                .map(v -> new VillageResponse(v.getId(), v.getName()))
                .toList();
    }
}

