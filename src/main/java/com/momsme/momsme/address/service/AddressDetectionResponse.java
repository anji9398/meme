package com.momsme.momsme.address.service;

public record AddressDetectionResponse(
        boolean status,
        String message,
        VillageInfo data
) {}

