package com.momsme.momsme.repository;

import com.momsme.momsme.model.DistrictVillages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistrictVillagesRepository extends JpaRepository<DistrictVillages, Long> {

    Optional<DistrictVillages> findByDistrictNameIgnoreCase(String districtName);

}

