package com.momsme.momsme.address.repository;

import com.momsme.momsme.model.Mandal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MandalRepository extends JpaRepository<Mandal,Long> {
    Mandal findByNameIgnoreCase(String w);
    List<Mandal> findByDistrict_Id(Long districtId);
}
