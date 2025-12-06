package com.momsme.momsme.msmedata.repo;

import com.momsme.momsme.model.MsmeDataPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MsmeDataPointsRepo extends JpaRepository<MsmeDataPoints,Long> {

    List<MsmeDataPoints> findByEnterpriseEnterpriseId(Long enterpriseId);
}
