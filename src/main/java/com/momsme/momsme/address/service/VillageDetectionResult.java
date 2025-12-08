package com.momsme.momsme.address.service;

import java.util.List;

public record VillageDetectionResult(
        String status,                     // SUCCESS | CONFLICT | NOT_FOUND | INVALID
        List<Match> matches                // 0, 1 or many matches
) {
    public record Match(
            Integer mandalId,
            Integer villageId,
            String mandalName,
            String villageName,
            String matchDetails            // <--- NEW FIELD
    ) {}
}
