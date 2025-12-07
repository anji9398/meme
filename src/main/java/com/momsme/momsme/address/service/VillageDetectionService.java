package com.momsme.momsme.address.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.momsme.momsme.address.repository.MsmeUnitDetailsRepository;
import com.momsme.momsme.excel.ExcelGenerator;
import com.momsme.momsme.model.DistrictVillages;
import com.momsme.momsme.model.MsmeUnitDetails;
import com.momsme.momsme.repository.DistrictVillagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VillageDetectionService {

    private final MsmeUnitDetailsRepository repository;
    private final DistrictVillagesRepository districtVillagesRepository;


    public String detectVillage(AddressDetectionRequest request) {

        if (request == null || request.address() == null || request.district() == null) {
            return null;
        }

        // 1. Detect mandal first (validates mandal belongs to district)
        String mandal = detectMandal(request);
        if (mandal == null || mandal.startsWith("Invalid")) {
            return null; // Mandal not found
        }

        // 2. Fetch district JSON
        Optional<DistrictVillages> districtVillagesDoc =
                districtVillagesRepository.findByDistrictNameIgnoreCase(request.district());

        if (districtVillagesDoc.isEmpty()) {
            return null;
        }

        String districtJson = districtVillagesDoc.get().getVillagesJson();
        VillageExtractor extractor = new VillageExtractor(districtJson);


        String village = extractor.extractVillageWithinMandal(request.address());
        if (village == null) {
            return null;
        }

        return village;
    }


    /* ----------------------------------------------------------------
     🔥 MANDAL DETECTION (ENTRY POINT)
     ---------------------------------------------------------------- */
    public String detectMandal(AddressDetectionRequest request) {
        if (request == null || request.address() == null || request.district() == null) return null;
        if (districtVillagesRepository == null) return null; // safety

        Optional<DistrictVillages> docOpt =
                districtVillagesRepository.findByDistrictNameIgnoreCase(request.district());

        if (docOpt.isEmpty()) return null;

        String districtJson = docOpt.get().getVillagesJson();
        VillageExtractor extractor = new VillageExtractor(districtJson);

        String detected = extractor.extractMandalOpt1(request.address());
        if (detected == null) return null;

        // final validation: ensure the detected mandal actually exists in the district JSON
        if (extractor.getVillagesByMandal(detected).isEmpty())
            return "Invalid mandal detected for district: " + detected;

        return detected;
    }





//    public byte[] detectVillageExcel(Integer page, Integer size) {
//
//        Page<MsmeUnitDetails> all = repository.findAll(PageRequest.of(page, size));
//        if (all.isEmpty()) return null;
//
//        List<MsmeUnitDetails> content = all.getContent();
//
//        for (MsmeUnitDetails obj : content) {
//
//            String districtName = obj.getDistrict(); // ensure entity has district column
//            Optional<DistrictVillages> districtVillagesDoc =
//                    districtVillagesRepository.findByDistrictNameIgnoreCase(districtName);
//
//            if (districtVillagesDoc.isPresent()) {
//                VillageExtractor extractor =
//                        new VillageExtractor(districtVillagesDoc.get().getVillagesJson());
//
//                String v = extractor.extractVillageOpt1(obj.getUnitAddress());
//                obj.setVillageId(v);
//            } else {
//                obj.setVillageId(null);
//            }
//        }
//
//        ExcelGenerator excel = new ExcelGenerator();
//        return excel.generateExcel(content);
//    }
}
