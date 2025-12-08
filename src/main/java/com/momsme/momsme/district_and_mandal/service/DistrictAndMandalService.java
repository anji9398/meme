package com.momsme.momsme.district_and_mandal.service;

import com.momsme.momsme.address.service.DistrictResponse;
import com.momsme.momsme.address.service.MandalResponse;
import com.momsme.momsme.address.service.VillageResponse;

import java.util.List;

public interface DistrictAndMandalService {
    List<DistrictResponse> getAllDistricts();
    List<MandalResponse> getMandalsByDistrict(Long districtId);
    List<VillageResponse> getVillagesByMandal(Long mandalId);
}
