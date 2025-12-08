package com.momsme.momsme.district_and_mandal.service;

import com.momsme.momsme.address.repository.DistrictRepository;
import com.momsme.momsme.address.repository.MandalRepository;
import com.momsme.momsme.address.repository.MsmeUnitDetailsRepository;
import com.momsme.momsme.address.repository.VillageRepository;
import com.momsme.momsme.address.service.DistrictResponse;
import com.momsme.momsme.address.service.MandalResponse;
import com.momsme.momsme.address.service.VillageResponse;
import com.momsme.momsme.model.MsmeUnitDetails;
import com.momsme.momsme.model.Village;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictAndMandalServiceAdapter implements DistrictAndMandalService {

    private final DistrictRepository districtRepository;
    private final MandalRepository mandalRepository;
    private final VillageRepository villageRepository;
    private final MsmeUnitDetailsRepository unitDetailsRepository;

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
    public Set<VillageResponse> getVillagesByMandal(Long mandalId) {
        return villageRepository.findByMandal_Id(mandalId)
                .stream()
                .map(v-> new VillageResponse(v.getId(), v.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<MsmeUnitDetails> getByVillage(String village,String mandal) {
        return unitDetailsRepository.findByVillageIgnoreCaseAndMandalIgnoreCase(village,mandal);
    }


}

