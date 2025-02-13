package com.bht.saigonparking.service.parkinglot.repository.core;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bht.saigonparking.service.parkinglot.entity.ParkingLotEmployeeEntity;
import com.bht.saigonparking.service.parkinglot.repository.custom.ParkingLotEmployeeRepositoryCustom;

/**
 *
 * @author bht
 */
@Repository
public interface ParkingLotEmployeeRepository extends JpaRepository<ParkingLotEmployeeEntity, Long>, ParkingLotEmployeeRepositoryCustom {

    /**
     *
     * self-implement countByUsername method
     * in order to prevent N+1 problem
     */
    @Query("SELECT E " +
            "FROM ParkingLotEmployeeEntity E " +
            "JOIN FETCH E.parkingLotEntity P " +
            "JOIN FETCH P.parkingLotTypeEntity " +
            "JOIN FETCH P.parkingLotLimitEntity " +
            "JOIN FETCH P.parkingLotInformationEntity " +
            "WHERE E.userId = ?1")
    Optional<ParkingLotEmployeeEntity> getByEmployeeId(@NotNull Long employeeId);

    /**
     *
     * self-implement countByUsername method
     * using to check if username already exist
     */
    @Query("SELECT COUNT (E.id) " +
            "FROM ParkingLotEmployeeEntity E " +
            "WHERE E.userId = ?1")
    Long countByUserId(@NotNull Long userId);

    /**
     *
     * self-implement getEmployeeManageParkingLotIdList method
     * using to get all employees who manage this parking-lot
     */
    @Query("SELECT E.userId " +
            "FROM ParkingLotEmployeeEntity E " +
            "WHERE E.parkingLotEntity.id = ?1")
    List<Long> getEmployeeManageParkingLotIdList(@NotNull Long parkingLotId);
}