package com.momsme.momsme.address.service;

import com.momsme.momsme.address.repository.MsmeUnitDetailsRepository;
import com.momsme.momsme.model.DistrictVillages;
import com.momsme.momsme.model.MsmeUnitDetails;
import com.momsme.momsme.repository.DistrictVillagesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VillageDetectionService {

    private final MsmeUnitDetailsRepository repository;
    private final DistrictVillagesRepository districtVillagesRepository;

    private final Map<String, VillageExtractor> extractorCache = new HashMap<>();

    /**
     * 🔥 Load JSON extractors only once on app startup
     */
    @PostConstruct
    public void init() {
        List<DistrictVillages> docs = districtVillagesRepository.findAll();
        for (DistrictVillages d : docs) {
            extractorCache.put(d.getDistrictName().toLowerCase(), new VillageExtractor(d.getVillagesJson()));
        }
        System.out.println("🔥 VillageExtractor cache initialized — " + extractorCache.size() + " districts loaded");
    }

    /**
     * Detect village + mandal based on cached extractor
     */
    public VillageDetectionResult detectVillage(AddressDetectionRequest request) {

        if (request == null || request.address() == null || request.district() == null) {
            return new VillageDetectionResult("INVALID", List.of());
        }

        VillageExtractor extractor = extractorCache.get(request.district().toLowerCase());
        if (extractor == null) {
            return new VillageDetectionResult("DISTRICT_NOT_FOUND", List.of());
        }

        return extractor.detect(request.address());
    }


    /**
     * 🔥 Update only first 200 units
     */
    @Transactional
    public int updateAllUnitsVillage() {

        Page<MsmeUnitDetails> page = repository.findAll(PageRequest.of(0, 26228));
        List<MsmeUnitDetails> units = page.getContent();

        List<MsmeUnitDetails> updatedUnits = new ArrayList<>(); // only changed rows go here
        int countUpdated = 0;

        for (MsmeUnitDetails unit : units) {

            if (unit.getUnitAddress() == null)
                continue;

            VillageDetectionResult result = detectVillage(
                    new AddressDetectionRequest(unit.getUnitAddress(), "Adilabad")
            );

            if (result == null) continue;

            switch (result.status()) {

                case "SUCCESS" -> {
                    VillageDetectionResult.Match m = result.matches().get(0);
                    unit.setMandalId(m.mandalId());
                    unit.setVillageId(m.villageId());
                    unit.setVillage(m.villageName());
                    unit.setMandal(m.mandalName());
                    unit.setIteration("Iteration-1");
                    unit.setMatchedDetails("SUCCESS: " + m.villageName() + " - " + m.mandalName());
                    updatedUnits.add(unit);
                    countUpdated++;
                }

                case "CONFLICT" -> {
                    String conflicts = result.matches().stream()
                            .map(mt -> mt.villageName() + " - " + mt.mandalName())
                            .collect(Collectors.joining(" , "));
                    unit.setVillage("CONFLICT");
                    unit.setMandal("CONFLICT");
                    unit.setIteration("Iteration-1");
                    unit.setMatchedDetails(conflicts);
                    updatedUnits.add(unit);
                }

                case "NOT_FOUND" -> {
                    unit.setVillage("NOT_FOUND");
                    unit.setMandal("NOT_FOUND");
                    unit.setIteration("Iteration-1");

                    if (!result.matches().isEmpty()) {
                        String matches = result.matches().stream()
                                .map(mt -> mt.villageName() + " - " + mt.mandalName())
                                .collect(Collectors.joining(" , "));
                        unit.setMatchedDetails(matches);
                    } else {
                        unit.setMatchedDetails("NO_MATCHES_FOUND");
                    }
                    updatedUnits.add(unit);
                }

                case "INVALID", "DISTRICT_NOT_FOUND" -> {
                    unit.setVillage(result.status());
                    unit.setMandal(result.status());
                    unit.setIteration("Iteration-1");
                    unit.setMatchedDetails(result.status());
                    updatedUnits.add(unit);
                }
            }
        }

        // 🔥 save only modified units (NOT all 200)
        if (!updatedUnits.isEmpty()) {
            repository.saveAll(updatedUnits);
        }

        return countUpdated;
    }
}

