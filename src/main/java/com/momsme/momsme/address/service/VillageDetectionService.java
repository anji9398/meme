package com.momsme.momsme.address.service;

import com.momsme.momsme.address.repository.MsmeUnitDetailsRepository;
import com.momsme.momsme.address.repository.VillageRepository;
import com.momsme.momsme.excel.ExcelGenerator;
import com.momsme.momsme.model.DistrictVillages;
import com.momsme.momsme.model.MsmeUnitDetails;
import com.momsme.momsme.repository.DistrictVillagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VillageDetectionService {

    private final MsmeUnitDetailsRepository repository;
    private final DistrictVillagesRepository districtVillagesRepository;

    public String detectVillage(AddressDetectionRequest request) {

        // Extract district from request (not address)
        String districtName = request.district();

        Optional<DistrictVillages> districtVillagesDoc =
                districtVillagesRepository.findByDistrictNameIgnoreCase(districtName);

        if (districtVillagesDoc.isEmpty()) {
            return null;
        }

        // Load JSONB from DB
        String villagesJson = districtVillagesDoc.get().getVillagesJson();

        // Extract from JSON
        VillageExtractor extractor = new VillageExtractor(villagesJson);
        return extractor.extractVillageOpt1(request.address());
    }


    public byte[] detectVillageExcel(Integer page, Integer size) {

        Page<MsmeUnitDetails> all = repository.findAll(PageRequest.of(page, size));
        if (all.isEmpty()) return null;

        List<MsmeUnitDetails> content = all.getContent();

        for (MsmeUnitDetails obj : content) {

            String districtName = obj.getDistrict(); // ensure entity has district column
            Optional<DistrictVillages> districtVillagesDoc =
                    districtVillagesRepository.findByDistrictNameIgnoreCase(districtName);

            if (districtVillagesDoc.isPresent()) {
                VillageExtractor extractor =
                        new VillageExtractor(districtVillagesDoc.get().getVillagesJson());

                String v = extractor.extractVillageOpt1(obj.getUnitAddress());
                obj.setVillageId(v);
            } else {
                obj.setVillageId(null);
            }
        }

        ExcelGenerator excel = new ExcelGenerator();
        return excel.generateExcel(content);
    }
}

