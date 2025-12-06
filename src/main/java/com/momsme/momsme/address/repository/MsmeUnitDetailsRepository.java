package com.momsme.momsme.address.repository;

import com.momsme.momsme.model.MsmeUnitDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MsmeUnitDetailsRepository extends JpaRepository<MsmeUnitDetails, Long> {

}
