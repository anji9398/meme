package com.momsme.momsme.address.repository;

import com.momsme.momsme.address.service.VillageInfo;
import com.momsme.momsme.model.Village;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {

    // 1. Basic exact match
    @Query("""
        SELECT new com.momsme.momsme.address.service.VillageInfo(
            d.name, m.name, m.id, v.name
        )
        FROM Village v
        JOIN v.mandal m
        JOIN m.district d
        WHERE LOWER(v.name) = LOWER(:village)
    """)
    List<VillageInfo> findByVillageName(String village);

    // 2. Exact village + mandal (used when mandal detected)
    @Query("""
        SELECT new com.momsme.momsme.address.service.VillageInfo(
            d.name, m.name, m.id, v.name
        )
        FROM Village v
        JOIN v.mandal m
        JOIN m.district d
        WHERE LOWER(v.name) = LOWER(:village)
          AND LOWER(m.name) = LOWER(:mandal)
    """)
    List<VillageInfo> findByVillageAndMandal(String village, String mandal);

    Village findByNameIgnoreCase(String word);
    List<Village> findByMandal_Id(Long mandalId);
}