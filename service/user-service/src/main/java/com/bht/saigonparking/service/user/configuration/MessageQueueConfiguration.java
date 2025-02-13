package com.bht.saigonparking.service.user.configuration;

import static com.bht.saigonparking.common.constant.SaigonParkingMessageQueue.PARKING_LOT_QUEUE_NAME;
import static com.bht.saigonparking.common.constant.SaigonParkingMessageQueue.USER_QUEUE_NAME;

import java.util.HashSet;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.logging.log4j.Level;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bht.saigonparking.api.grpc.parkinglot.DeleteParkingLotNotification;
import com.bht.saigonparking.api.grpc.parkinglot.ParkingLotEmployeeInfo;
import com.bht.saigonparking.api.grpc.user.UpdateUserLastSignInRequest;
import com.bht.saigonparking.common.util.LoggingUtil;
import com.bht.saigonparking.service.user.service.main.UserService;

import lombok.AllArgsConstructor;

/**
 *
 * @author bht
 */
@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MessageQueueConfiguration {

    private final UserService userService;

    @RabbitListener(queues = {USER_QUEUE_NAME})
    public void consumeMessageFromUserTopic(UpdateUserLastSignInRequest request) {
        try {
            userService.updateUserLastSignIn(request.getUserId(), request.getTimeInMillis());
            LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                    String.format("updateUserLastSignIn(%d)", request.getUserId()));

        } catch (Exception exception) {

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("updateUserLastSignIn(%d)", request.getUserId()));
        }
    }

    @RabbitListener(queues = {PARKING_LOT_QUEUE_NAME})
    public void consumeMessageFromParkingLotTopic(DeleteParkingLotNotification notification) {
        try {
            notification.getInfoList().forEach(info -> {
                userService.deleteMultiUserById(new HashSet<>(info.getEmployeeIdList()));
                LoggingUtil.log(Level.INFO, "SERVICE", "Success",
                        String.format("deleteParkingLotEmployeesByParkingLotId(%d)", info.getParkingLotId()));
            });
        } catch (Exception exception) {

            LoggingUtil.log(Level.ERROR, "SERVICE", "Exception", exception.getClass().getSimpleName());
            LoggingUtil.log(Level.WARN, "SERVICE", "Session FAIL",
                    String.format("deleteParkingLotEmployeesByParkingLotId(%s)",
                            notification.getInfoList().stream().map(ParkingLotEmployeeInfo::getParkingLotId).collect(Collectors.toList())));
        }
    }
}