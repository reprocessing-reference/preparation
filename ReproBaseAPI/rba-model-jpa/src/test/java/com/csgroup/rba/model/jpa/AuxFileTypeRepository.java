package com.csgroup.rba.model.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuxFileTypeRepository extends JpaRepository<AuxFileTypeJPA, String> {

}
