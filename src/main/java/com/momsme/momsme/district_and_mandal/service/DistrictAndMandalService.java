package com.momsme.momsme.district_and_mandal.service;
import com.momsme.momsme.model.MsmeUnitDetails;
import com.momsme.momsme.address.service.DistrictResponse;
import com.momsme.momsme.address.service.MandalResponse;
import com.momsme.momsme.address.service.VillageResponse;

import java.util.List;
import java.util.Set;

public interface DistrictAndMandalService {
    List<DistrictResponse> getAllDistricts();
    List<MandalResponse> getMandalsByDistrict(Long districtId);
    Set<VillageResponse> getVillagesByMandal(Long mandalId);
    List<MsmeUnitDetails> getByVillage(String village,String mandal);
}
