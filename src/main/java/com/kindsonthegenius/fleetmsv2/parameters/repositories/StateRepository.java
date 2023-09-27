package com.kindsonthegenius.fleetmsv2.parameters.repositories;

import com.kindsonthegenius.fleetmsv2.parameters.models.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Integer> {

    // Create a method to find states by country ID
    List<State> findByCountryId(Integer countryId);
}
