package com.bht.saigonparking.service.parkinglot.service.grpc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import com.bht.saigonparking.api.grpc.parkinglot.AddEmployeeOfParkingLotRequest;
import com.bht.saigonparking.api.grpc.parkinglot.CountAllParkingLotGroupByTypeResponse;
import com.bht.saigonparking.api.grpc.parkinglot.CountAllParkingLotRequest;
import com.bht.saigonparking.api.grpc.parkinglot.DeleteMultiParkingLotByIdRequest;
import com.bht.saigonparking.api.grpc.parkinglot.GetAllParkingLotRequest;
import com.bht.saigonparking.api.grpc.parkinglot.GetAllParkingLotResponse;
import com.bht.saigonparking.api.grpc.parkinglot.GetEmployeeManageParkingLotIdListResponse;
import com.bht.saigonparking.api.grpc.parkinglot.MapToParkingLotNameMapRequest;
import com.bht.saigonparking.api.grpc.parkinglot.MapToParkingLotNameMapResponse;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLot;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLotIdList;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLotLimit;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLotResult;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLotResultList;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLotServiceGrpc.ParkingLotServiceImplBase;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLotType;
import com.bht.saigonparking.api.grpc.parkinglot.RemoveEmployeeOfParkingLotRequest;
import com.bht.saigonparking.api.grpc.parkinglot.ScanningByRadiusRequest;
import com.bht.saigonparking.api.grpc.parkinglot.UpdateParkingLotAvailabilityRequest;
import com.bht.saigonparking.common.interceptor.SaigonParkingServerInterceptor;
import com.bht.saigonparking.common.util.LoggingUtil;
import com.bht.saigonparking.service.parkinglot.entity.ParkingLotLimitEntity;
import com.bht.saigonparking.service.parkinglot.mapper.EnumMapper;
import com.bht.saigonparking.service.parkinglot.mapper.ParkingLotMapper;
import com.bht.saigonparking.service.parkinglot.mapper.ParkingLotMapperExt;
import com.bht.saigonparking.service.parkinglot.service.main.ParkingLotService;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;

/**
 *
 * this class implements all services of ParkingLotStub
 *
 * for clean code purpose,
 * using {@code @AllArgsConstructor} for Service class
 * it will {@code @Autowired} all attributes declared inside
 * hide {@code @Autowired} as much as possible in code
 * remember to mark all attributes as {@code private final}
 *
 * @author bht
 */
@GRpcService
@AllArgsConstructor(onConstructor = @__(@Autowired))
public final class ParkingLotServiceGrpcImpl extends ParkingLotServiceImplBase {

    private final ParkingLotService parkingLotService;
    private final ParkingLotMapper parkingLotMapper;
    private final ParkingLotMapperExt parkingLotMapperExt;
    private final EnumMapper enumMapper;
    private final SaigonParkingServerInterceptor serverInterceptor;

    @Override
    public void getParkingLotIdByAuthorizationHeader(Empty request, StreamObserver<Int64Value> responseObserver) {
        try {
            serverInterceptor.validateUserRole("PARKING_LOT_EMPLOYEE");

            long employeeId = serverInterceptor.getUserIdContext().get();
            long parkingLotId = parkingLotService.getParkingLotIdByParkingLotEmployeeId(employeeId);

            responseObserver.onNext(Int64Value.of(parkingLotId));
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("getParkingLotIdByAuthorizationHeader(%d): %d", employeeId, parkingLotId));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL", "getParkingLotIdByAuthorizationHeader()");
        }
    }

    @Override
    public void getParkingLotIdByParkingLotEmployeeId(Int64Value request, StreamObserver<Int64Value> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            long employeeId = request.getValue();
            long parkingLotId = parkingLotService.getParkingLotIdByParkingLotEmployeeId(employeeId);

            responseObserver.onNext(Int64Value.of(parkingLotId));
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("getParkingLotIdByParkingLotEmployeeId(%d): %d", employeeId, parkingLotId));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("getParkingLotIdByParkingLotEmployeeId(%d)", request.getValue()));
        }
    }

    @Override
    public void countAllParkingLot(CountAllParkingLotRequest request, StreamObserver<Int64Value> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            Long count;

            if (request.getParkingLotType().equals(ParkingLotType.ALL)) {
                count = parkingLotService.countAll(request.getKeyword(), request.getAvailableOnly());

            } else {
                count = parkingLotService.countAll(request.getKeyword(), request.getAvailableOnly(),
                        enumMapper.toParkingLotTypeEntity(request.getParkingLotType()));
            }

            responseObserver.onNext(Int64Value.of(count));
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("countAllParkingLot(%s, %b, %s): %d",
                            request.getKeyword(), request.getAvailableOnly(), request.getParkingLotType(), count));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("countAllParkingLot(%s, %b, %s)",
                            request.getKeyword(), request.getAvailableOnly(), request.getParkingLotType()));
        }
    }

    @Override
    public void getAllParkingLot(GetAllParkingLotRequest request, StreamObserver<GetAllParkingLotResponse> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            List<ParkingLot> parkingLotList;

            if (request.getParkingLotType().equals(ParkingLotType.ALL)) {
                parkingLotList = parkingLotMapper.toParkingLotList(parkingLotService
                        .getAll(request.getNRow(), request.getPageNumber(), request.getKeyword(), request.getAvailableOnly()));

            } else {
                parkingLotList = parkingLotMapper.toParkingLotList(parkingLotService
                        .getAll(request.getNRow(), request.getPageNumber(), request.getKeyword(), request.getAvailableOnly(),
                                enumMapper.toParkingLotTypeEntity(request.getParkingLotType())));
            }

            responseObserver.onNext(GetAllParkingLotResponse.newBuilder().addAllParkingLot(parkingLotList).build());
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("getAllParkingLot(%s, %b, %s, %d, %d)",
                            request.getKeyword(), request.getAvailableOnly(), request.getParkingLotType(), request.getNRow(), request.getPageNumber()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("getAllParkingLot(%s, %b, %s, %d, %d)",
                            request.getKeyword(), request.getAvailableOnly(), request.getParkingLotType(), request.getNRow(), request.getPageNumber()));
        }
    }

    @Override
    public void getParkingLotById(Int64Value request, StreamObserver<ParkingLot> responseObserver) {
        try {
            ParkingLot parkingLot = parkingLotMapper.toParkingLot(
                    parkingLotService.getParkingLotById(request.getValue()));

            responseObserver.onNext(parkingLot);
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("getParkingLotById(%d)", request.getValue()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("getParkingLotById(%d)", request.getValue()));
        }
    }

    @Override
    public void checkLimit(Int64Value request, StreamObserver<ParkingLotLimit> responseObserver) {
        try {
            ParkingLotLimitEntity parkingLotLimitEntity = parkingLotService.getParkingLotLimitById(request.getValue());
            ParkingLotLimit parkingLotLimit = ParkingLotLimit.newBuilder()
                    .setAvailableSlot(parkingLotLimitEntity.getAvailableSlot())
                    .setTotalSlot(parkingLotLimitEntity.getTotalSlot())
                    .build();

            responseObserver.onNext(parkingLotLimit);
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("checkLimit(%d): %d/%d",
                            request.getValue(), parkingLotLimitEntity.getAvailableSlot(), parkingLotLimitEntity.getTotalSlot()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("checkLimit(%d)", request.getValue()));
        }
    }

    @Override
    public void checkAvailability(Int64Value request, StreamObserver<BoolValue> responseObserver) {
        try {
            BoolValue boolValue = BoolValue.newBuilder()
                    .setValue(parkingLotService.checkAvailability(request.getValue()))
                    .build();

            responseObserver.onNext(boolValue);
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("checkAvailability(%d): %s", request.getValue(), boolValue.getValue()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("checkAvailability(%d)", request.getValue()));
        }
    }

    @Override
    public void checkUnavailability(ParkingLotIdList request, StreamObserver<ParkingLotIdList> responseObserver) {
        try {
            ParkingLotIdList parkingLotIdList = ParkingLotIdList.newBuilder()
                    .addAllParkingLotId(parkingLotService.checkUnavailability(request.getParkingLotIdList()))
                    .build();

            responseObserver.onNext(parkingLotIdList);
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("checkUnavailability of %d parking-lot", request.getParkingLotIdCount()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("checkUnavailability of %d parking-lot", request.getParkingLotIdCount()));
        }
    }

    @Override
    public void getTopParkingLotInRegionOrderByDistanceWithName(ScanningByRadiusRequest request, StreamObserver<ParkingLotResultList> responseObserver) {
        try {
            List<ParkingLotResult> parkingLotResultList = parkingLotMapper.toParkingLotResultListWithName(
                    parkingLotService.getTopParkingLotInRegionOrderByDistanceWithName(
                            request.getLatitude(),
                            request.getLongitude(),
                            request.getRadiusToScan(),
                            request.getNResult()));

            responseObserver.onNext(ParkingLotResultList.newBuilder().addAllParkingLotResult(parkingLotResultList).build());
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("getTopParkingLotInRegionOrderByDistanceWithName(%f, %f, %d, %d)",
                            request.getLatitude(), request.getLongitude(), request.getRadiusToScan(), request.getNResult()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("getTopParkingLotInRegionOrderByDistanceWithName(%f, %f, %d, %d)",
                            request.getLatitude(), request.getLongitude(), request.getRadiusToScan(), request.getNResult()));
        }
    }

    @Override
    public void getTopParkingLotInRegionOrderByDistanceWithoutName(ScanningByRadiusRequest request, StreamObserver<ParkingLotResultList> responseObserver) {
        try {
            List<ParkingLotResult> parkingLotResultList = parkingLotMapper.toParkingLotResultListWithoutName(
                    parkingLotService.getTopParkingLotInRegionOrderByDistanceWithoutName(
                            request.getLatitude(),
                            request.getLongitude(),
                            request.getRadiusToScan(),
                            request.getNResult()));

            responseObserver.onNext(ParkingLotResultList.newBuilder().addAllParkingLotResult(parkingLotResultList).build());
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("getTopParkingLotInRegionOrderByDistanceWithoutName(%f, %f, %d, %d)",
                            request.getLatitude(), request.getLongitude(), request.getRadiusToScan(), request.getNResult()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("getTopParkingLotInRegionOrderByDistanceWithoutName(%f, %f, %d, %d)",
                            request.getLatitude(), request.getLongitude(), request.getRadiusToScan(), request.getNResult()));
        }
    }

    @Override
    public void deleteParkingLotById(Int64Value request, StreamObserver<Empty> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            parkingLotService.deleteParkingLotById(request.getValue());

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("deleteParkingLotById(%d)", request.getValue()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("deleteParkingLotById(%d)", request.getValue()));
        }
    }

    @Override
    public void deleteMultiParkingLotById(DeleteMultiParkingLotByIdRequest request, StreamObserver<Empty> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            parkingLotService.deleteMultiParkingLotById(new HashSet<>(request.getParkingLotIdList()));

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("deleteMultiParkingLotById(%s)", request.getParkingLotIdList()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("deleteMultiParkingLotById(%s)", request.getParkingLotIdList()));
        }
    }

    @Override
    public void updateParkingLotAvailability(UpdateParkingLotAvailabilityRequest request, StreamObserver<Empty> responseObserver) {
        try {
            serverInterceptor.validateUserRole(Arrays.asList("PARKING_LOT_EMPLOYEE", "ADMIN"));

            parkingLotService.updateAvailability((short) request.getNewAvailability(), request.getParkingLotId());

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("updateParkingLotAvailability(%d): %d", request.getParkingLotId(), request.getNewAvailability()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("updateParkingLotAvailability(%d): %d", request.getParkingLotId(), request.getNewAvailability()));
        }
    }

    @Override
    public void mapToParkingLotNameMap(MapToParkingLotNameMapRequest request, StreamObserver<MapToParkingLotNameMapResponse> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            MapToParkingLotNameMapResponse mapToParkingLotNameMapResponse = MapToParkingLotNameMapResponse.newBuilder()
                    .putAllParkingLotName(parkingLotService.mapToParkingLotNameMap(new HashSet<>(request.getParkingLotIdList())))
                    .build();

            responseObserver.onNext(mapToParkingLotNameMapResponse);
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success", "mapToParkingLotNameMap()");

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL", "mapToParkingLotNameMap()");
        }
    }

    @Override
    public void getParkingLotNameByParkingLotId(Int64Value request, StreamObserver<StringValue> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            String parkingLotName = parkingLotService.getParkingLotNameByParkingLotId(request.getValue());

            responseObserver.onNext(StringValue.of(parkingLotName));
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("getParkingLotNameByParkingLotId(%d): %s", request.getValue(), parkingLotName));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("getParkingLotNameByParkingLotId(%d)", request.getValue()));
        }
    }

    @Override
    public void countAllParkingLotGroupByType(Empty request, StreamObserver<CountAllParkingLotGroupByTypeResponse> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            CountAllParkingLotGroupByTypeResponse response = CountAllParkingLotGroupByTypeResponse.newBuilder()
                    .putAllTypeCount(parkingLotService.countAllParkingLotGroupByType())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success", "countAllParkingLotGroupByType()");

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL", "countAllParkingLotGroupByType()");
        }
    }

    @Override
    public void addEmployeeOfParkingLot(AddEmployeeOfParkingLotRequest request, StreamObserver<Empty> responseObserver) {
        try {
            serverInterceptor.validateAdmin();
            parkingLotService.addEmployeeOfParkingLot(request.getEmployeeId(), request.getParkingLotId());

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("addEmployeeOfParkingLot(%d, %d)", request.getEmployeeId(), request.getParkingLotId()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("addEmployeeOfParkingLot(%d, %d)", request.getEmployeeId(), request.getParkingLotId()));
        }
    }

    @Override
    public void removeEmployeeOfParkingLot(RemoveEmployeeOfParkingLotRequest request, StreamObserver<Empty> responseObserver) {
        try {
            serverInterceptor.validateAdmin();
            parkingLotService.removeEmployeeOfParkingLot(request.getEmployeeId(), request.getParkingLotId(), request.getDeleteEmployee());

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("removeEmployeeOfParkingLot(%d, %d, %b)",
                            request.getEmployeeId(), request.getParkingLotId(), request.getDeleteEmployee()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("removeEmployeeOfParkingLot(%d, %d, %b)",
                            request.getEmployeeId(), request.getParkingLotId(), request.getDeleteEmployee()));
        }
    }

    @Override
    public void createNewParkingLot(ParkingLot request, StreamObserver<Int64Value> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            Long newParkingLotId = parkingLotService.createNewParkingLot(
                    parkingLotMapperExt.toParkingLotEntity(request, true),
                    parkingLotMapper.toParkingLotLimitEntityIgnoreParkingLotEntity(request),
                    parkingLotMapper.toParkingLotInformationEntityIgnoreParkingLotEntity(request));

            responseObserver.onNext(Int64Value.of(newParkingLotId));
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("createNewParkingLot(%s)", request.getInformation().getName()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("createNewParkingLot(%s)", request.getInformation().getName()));
        }
    }

    @Override
    public void checkEmployeeAlreadyManageParkingLot(Int64Value request, StreamObserver<BoolValue> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            boolean isEmployeeAlreadyManageParkingLot = parkingLotService.checkEmployeeAlreadyManageParkingLot(request.getValue());

            responseObserver.onNext(BoolValue.of(isEmployeeAlreadyManageParkingLot));
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("checkEmployeeAlreadyManageParkingLot(%d): %b", request.getValue(), isEmployeeAlreadyManageParkingLot));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("checkEmployeeAlreadyManageParkingLot(%d)", request.getValue()));
        }
    }

    @Override
    public void getEmployeeManageParkingLotIdList(Int64Value request, StreamObserver<GetEmployeeManageParkingLotIdListResponse> responseObserver) {
        try {
            serverInterceptor.validateAdmin();

            List<Long> employeeIdList = parkingLotService.getEmployeeManageParkingLotIdList(request.getValue());
            GetEmployeeManageParkingLotIdListResponse response = GetEmployeeManageParkingLotIdListResponse.newBuilder()
                    .addAllEmployeeId(employeeIdList)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("getEmployeeManageParkingLotIdList(%d): %s", request.getValue(), employeeIdList));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("getEmployeeManageParkingLotIdList(%d)", request.getValue()));
        }
    }

    @Override
    public void getParkingLotManagedByEmployee(Int64Value request, StreamObserver<ParkingLot> responseObserver) {
        try {
            serverInterceptor.validateUserRole(Arrays.asList("PARKING_LOT_EMPLOYEE", "ADMIN"));

            ParkingLot parkingLot = parkingLotMapper.toParkingLot(parkingLotService.getParkingLotByEmployeeId(request.getValue()));

            responseObserver.onNext(parkingLot);
            responseObserver.onCompleted();

            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("getParkingLotManagedByEmployee(%d): %s", request.getValue(), parkingLot.getInformation().getName()));

        } catch (Exception exception) {

            responseObserver.onError(exception);

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("getParkingLotManagedByEmployee(%d)", request.getValue()));
        }
    }
}