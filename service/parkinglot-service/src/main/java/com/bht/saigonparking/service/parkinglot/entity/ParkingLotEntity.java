package com.bht.saigonparking.service.parkinglot.entity;

import java.sql.Time;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SelectBeforeUpdate;

import com.bht.saigonparking.common.base.BaseEntity;
import com.bht.saigonparking.service.parkinglot.annotation.TimeFlowValidation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author bht
 */
@Entity
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SelectBeforeUpdate
@TimeFlowValidation
@Table(name = "[PARKING_LOT]")
public final class ParkingLotEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "[PARKING_LOT_TYPE_ID]", referencedColumnName = "[ID]", nullable = false, updatable = false)
    private ParkingLotTypeEntity parkingLotTypeEntity;

    @Column(name = "[LATITUDE]", nullable = false)
    private Double latitude;

    @Column(name = "[LONGITUDE]", nullable = false)
    private Double longitude;

    @Column(name = "[OPENING_HOUR]", nullable = false)
    private Time openingHour;

    @Column(name = "[CLOSING_HOUR]", nullable = false)
    private Time closingHour;

    @ColumnDefault("true")
    @Column(name = "[IS_AVAILABLE]")
    private Boolean isAvailable;

    @OneToOne(mappedBy = "parkingLotEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private ParkingLotLimitEntity parkingLotLimitEntity;

    @OneToOne(mappedBy = "parkingLotEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private ParkingLotInformationEntity parkingLotInformationEntity;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "parkingLotEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParkingLotUnitEntity> parkingLotUnitEntitySet;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "parkingLotEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParkingLotEmployeeEntity> parkingLotEmployeeEntitySet;
}