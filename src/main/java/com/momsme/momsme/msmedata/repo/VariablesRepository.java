package com.momsme.momsme.msmedata.repo;

import com.momsme.momsme.model.Variables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariablesRepository extends JpaRepository<Variables,Long> {
}
