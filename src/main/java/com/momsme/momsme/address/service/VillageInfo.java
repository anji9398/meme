package com.momsme.momsme.address.service;

import lombok.*;

@Data
@NoArgsConstructor
public class VillageInfo {

    private String district;
    private String mandal;
    private Long mandalId;
    private String village;

    // REQUIRED for JPQL new()
    public VillageInfo(String district, String mandal, Long mandalId, String village) {
        this.district = district;
        this.mandal = mandal;
        this.mandalId = mandalId;
        this.village = village;
    }
}


