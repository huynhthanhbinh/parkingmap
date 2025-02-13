package com.bht.saigonparking.service.parkinglot.mapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.validation.constraints.NotNull;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.stereotype.Component;

import com.bht.saigonparking.api.grpc.parkinglot.ParkingLot;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLotInformation;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLotResult;
import com.bht.saigonparking.service.parkinglot.configuration.AppConfiguration;
import com.bht.saigonparking.service.parkinglot.entity.ParkingLotEntity;
import com.bht.saigonparking.service.parkinglot.entity.ParkingLotInformationEntity;
import com.bht.saigonparking.service.parkinglot.entity.ParkingLotLimitEntity;

/**
 *
 * Mapper class for parking-lot entities and its families
 * Mapper is used for mapping objects from different layers
 * For example here is: map Entity obj to DTO obj and vice versa
 *
 * @author bht
 */
@Component
@SuppressWarnings("UnmappedTargetProperties")
@Mapper(componentModel = "spring", implementationPackage = AppConfiguration.BASE_PACKAGE + ".mapper.impl",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {EnumMapper.class, CustomizedMapper.class})
public interface ParkingLotMapper {

    @Named("toParkingLotResultWithoutName")
    @Mapping(target = "id", expression = "java(parkingLotWithoutNameTuple.get(0, java.math.BigInteger.class).longValue())")
    @Mapping(target = "type", expression = "java(enumMapper.toParkingLotType(parkingLotWithoutNameTuple.get(1, java.math.BigInteger.class).longValue()))")
    @Mapping(target = "latitude", expression = "java(parkingLotWithoutNameTuple.get(2, Double.class))")
    @Mapping(target = "longitude", expression = "java(parkingLotWithoutNameTuple.get(3, Double.class))")
    @Mapping(target = "availableSlot", expression = "java(parkingLotWithoutNameTuple.get(4, Short.class))")
    @Mapping(target = "totalSlot", expression = "java(parkingLotWithoutNameTuple.get(5, Short.class))")
    ParkingLotResult toParkingLotResultWithoutName(@NotNull Tuple parkingLotWithoutNameTuple);

    @Named("toParkingLotResultWithName")
    @Mapping(target = "id", expression = "java(parkingLotWithNameTuple.get(0, java.math.BigInteger.class).longValue())")
    @Mapping(target = "name", expression = "java(parkingLotWithNameTuple.get(1, String.class))")
    @Mapping(target = "type", expression = "java(enumMapper.toParkingLotType(parkingLotWithNameTuple.get(2, java.math.BigInteger.class).longValue()))")
    @Mapping(target = "latitude", expression = "java(parkingLotWithNameTuple.get(3, Double.class))")
    @Mapping(target = "longitude", expression = "java(parkingLotWithNameTuple.get(4, Double.class))")
    @Mapping(target = "availableSlot", expression = "java(parkingLotWithNameTuple.get(5, Short.class))")
    @Mapping(target = "totalSlot", expression = "java(parkingLotWithNameTuple.get(6, Short.class))")
    ParkingLotResult toParkingLotResultWithName(@NotNull Tuple parkingLotWithNameTuple);

    @Named("toParkingLotInformation")
    @Mapping(target = "name", source = "name", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "address", source = "address", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "phone", source = "phone", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "imageData", source = "id", qualifiedByName = "toEncodedParkingLotImage", defaultExpression = "java(customizedMapper.DEFAULT_BYTE_STRING_VALUE)")
    @Mapping(target = "version", source = "version", defaultExpression = "java(customizedMapper.DEFAULT_LONG_VALUE)")
    ParkingLotInformation toParkingLotInformation(@NotNull ParkingLotInformationEntity parkingLotInformationEntity);

    @Named("toParkingLotInformationIgnoreImage")
    @Mapping(target = "name", source = "name", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "address", source = "address", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "phone", source = "phone", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "imageData", expression = "java(customizedMapper.DEFAULT_BYTE_STRING_VALUE)")
    @Mapping(target = "version", source = "version", defaultExpression = "java(customizedMapper.DEFAULT_LONG_VALUE)")
    ParkingLotInformation toParkingLotInformationIgnoreImage(@NotNull ParkingLotInformationEntity parkingLotInformationEntity);

    @Named("toParkingLot")
    @Mapping(target = "id", source = "id", defaultExpression = "java(customizedMapper.DEFAULT_LONG_VALUE)")
    @Mapping(target = "information", source = "parkingLotInformationEntity", qualifiedByName = "toParkingLotInformation", defaultExpression = "java(customizedMapper.DEFAULT_PARKING_LOT_INFORMATION)")
    @Mapping(target = "type", source = "parkingLotTypeEntity", qualifiedByName = "toParkingLotType", defaultExpression = "java(customizedMapper.DEFAULT_PARKING_LOT_TYPE)")
    @Mapping(target = "latitude", source = "latitude", defaultExpression = "java(customizedMapper.DEFAULT_DOUBLE_VALUE)")
    @Mapping(target = "longitude", source = "longitude", defaultExpression = "java(customizedMapper.DEFAULT_DOUBLE_VALUE)")
    @Mapping(target = "openingHour", source = "openingHour", qualifiedByName = "toTimeString", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "closingHour", source = "closingHour", qualifiedByName = "toTimeString", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "availableSlot", source = "parkingLotLimitEntity.availableSlot", defaultExpression = "java(customizedMapper.DEFAULT_SHORT_VALUE)")
    @Mapping(target = "totalSlot", source = "parkingLotLimitEntity.totalSlot", defaultExpression = "java(customizedMapper.DEFAULT_SHORT_VALUE)")
    @Mapping(target = "version", source = "version", defaultExpression = "java(customizedMapper.DEFAULT_LONG_VALUE)")
    ParkingLot toParkingLot(@NotNull ParkingLotEntity parkingLotEntity);

    @Named("toParkingLotIgnoreImage")
    @Mapping(target = "id", source = "id", defaultExpression = "java(customizedMapper.DEFAULT_LONG_VALUE)")
    @Mapping(target = "information", source = "parkingLotInformationEntity", qualifiedByName = "toParkingLotInformationIgnoreImage", defaultExpression = "java(customizedMapper.DEFAULT_PARKING_LOT_INFORMATION)")
    @Mapping(target = "type", source = "parkingLotTypeEntity", qualifiedByName = "toParkingLotType", defaultExpression = "java(customizedMapper.DEFAULT_PARKING_LOT_TYPE)")
    @Mapping(target = "latitude", source = "latitude", defaultExpression = "java(customizedMapper.DEFAULT_DOUBLE_VALUE)")
    @Mapping(target = "longitude", source = "longitude", defaultExpression = "java(customizedMapper.DEFAULT_DOUBLE_VALUE)")
    @Mapping(target = "openingHour", source = "openingHour", qualifiedByName = "toTimeString", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "closingHour", source = "closingHour", qualifiedByName = "toTimeString", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "availableSlot", source = "parkingLotLimitEntity.availableSlot", defaultExpression = "java(customizedMapper.DEFAULT_SHORT_VALUE)")
    @Mapping(target = "totalSlot", source = "parkingLotLimitEntity.totalSlot", defaultExpression = "java(customizedMapper.DEFAULT_SHORT_VALUE)")
    @Mapping(target = "version", source = "version", defaultExpression = "java(customizedMapper.DEFAULT_LONG_VALUE)")
    ParkingLot toParkingLotIgnoreImage(@NotNull ParkingLotEntity parkingLotEntity);

    @Named("toParkingLotLimitEntityIgnoreParkingLotEntity")
    @Mapping(target = "parkingLotEntity", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "totalSlot", source = "totalSlot", defaultExpression = "java(customizedMapper.DEFAULT_SHORT_VALUE)")
    @Mapping(target = "availableSlot", source = "availableSlot", defaultExpression = "java(customizedMapper.DEFAULT_SHORT_VALUE)")
    ParkingLotLimitEntity toParkingLotLimitEntityIgnoreParkingLotEntity(@NotNull ParkingLot parkingLot);

    @Named("toParkingLotInformationEntityIgnoreParkingLotEntity")
    @Mapping(target = "parkingLotEntity", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "name", source = "information.name", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "phone", source = "information.phone", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    @Mapping(target = "address", source = "information.address", defaultExpression = "java(customizedMapper.DEFAULT_STRING_VALUE)")
    ParkingLotInformationEntity toParkingLotInformationEntityIgnoreParkingLotEntity(@NotNull ParkingLot parkingLot);

    @Named("toParkingLotResultListWithoutName")
    default List<ParkingLotResult> toParkingLotResultListWithoutName(@NotNull List<Tuple> parkingLotWithoutNameTupleList) {
        return parkingLotWithoutNameTupleList.stream().map(this::toParkingLotResultWithoutName).collect(Collectors.toList());
    }

    @Named("toParkingLotResultListWithName")
    default List<ParkingLotResult> toParkingLotResultListWithName(@NotNull List<Tuple> parkingLotWithNameTupleList) {
        return parkingLotWithNameTupleList.stream().map(this::toParkingLotResultWithName).collect(Collectors.toList());
    }

    @Named("toParkingLotList")
    default List<ParkingLot> toParkingLotList(@NotNull List<ParkingLotEntity> parkingLotEntityList) {
        return parkingLotEntityList.stream().map(this::toParkingLotIgnoreImage).collect(Collectors.toList());
    }
}