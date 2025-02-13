package com.bht.saigonparking.service.parkinglot.repository.core;

import java.util.Optional;
import java.util.Set;

import javax.persistence.Tuple;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bht.saigonparking.service.parkinglot.entity.ParkingLotInformationEntity;

/**
 *
 * @author bht
 */
@Repository
public interface ParkingLotInformationRepository extends JpaRepository<ParkingLotInformationEntity, Long> {

    /**
     *
     * self-implement getById method
     * in order to prevent N+1 problem
     */
    @Query("SELECT PLI " +
            "FROM ParkingLotInformationEntity PLI " +
            "JOIN FETCH PLI.parkingLotEntity PL " +
            "JOIN FETCH PL.parkingLotTypeEntity PLT " +
            "JOIN FETCH PL.parkingLotLimitEntity PLL " +
            "WHERE PLI.id = ?1")
    ParkingLotInformationEntity getById(@NotNull Long id);

    /**
     *
     * self-implement getParkingLotNameByParkingLotId method
     */
    @Query("SELECT PLI.name FROM ParkingLotInformationEntity PLI WHERE PLI.parkingLotEntity.id = ?1")
    Optional<String> getParkingLotName(@NotNull Long parkingLotId);

    /**
     *
     * self-implement mapParkingLotNameWithId method
     * in order to prevent N+1 problem
     *
     * each tuple will contains 2 fields: parkingLotId, parkingLotName
     * tuple set will then be map into a map of <parkingLotId, parkingLotName>
     */
    @Query("SELECT PLI.parkingLotEntity.id, PLI.name " +
            "FROM ParkingLotInformationEntity PLI " +
            "WHERE PLI.parkingLotEntity.id IN ?1")
    Set<Tuple> mapParkingLotNameWithId(@NotNull Set<Long> parkingLotIdSet);
}