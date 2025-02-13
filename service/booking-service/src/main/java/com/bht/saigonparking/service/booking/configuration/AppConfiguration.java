package com.bht.saigonparking.service.booking.configuration;

import javax.persistence.EntityNotFoundException;

import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bht.saigonparking.common.annotation.InheritedComponent;
import com.bht.saigonparking.common.base.BaseSaigonParkingAppConfiguration;
import com.bht.saigonparking.common.exception.BookingAlreadyFinishedException;
import com.bht.saigonparking.common.exception.BookingAlreadyRatedException;
import com.bht.saigonparking.common.exception.BookingNotYetAcceptedException;
import com.bht.saigonparking.common.exception.BookingNotYetFinishedException;
import com.bht.saigonparking.common.exception.BookingNotYetRatedException;
import com.bht.saigonparking.common.exception.CustomerHasOnGoingBookingException;
import com.bht.saigonparking.common.exception.PermissionDeniedException;
import com.bht.saigonparking.common.interceptor.SaigonParkingClientInterceptor;
import com.bht.saigonparking.common.interceptor.SaigonParkingServerInterceptor;
import com.bht.saigonparking.common.spring.SpringBeanLifeCycle;
import com.google.common.collect.ImmutableMap;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 *
 * @author bht
 */
@EnableAsync
@Configuration
@EnableTransactionManagement
@Import({ChannelConfiguration.class, MessageQueueConfiguration.class})
@ComponentScan(basePackages = AppConfiguration.BASE_PACKAGE, includeFilters = @ComponentScan.Filter(InheritedComponent.class))
public class AppConfiguration extends BaseSaigonParkingAppConfiguration {

    public static final String BASE_PACKAGE = "com.bht.saigonparking.service.booking";

    @Bean
    public QRCodeWriter qrCodeWriter() {
        return new QRCodeWriter();
    }

    @Bean
    public SpringBeanLifeCycle springBeanLifeCycle() {
        return new SpringBeanLifeCycle(BASE_PACKAGE);
    }

    @Bean
    public SaigonParkingClientInterceptor saigonParkingClientInterceptor() {
        return new SaigonParkingClientInterceptor(SaigonParkingClientInterceptor.INTERNAL_CODE_BOOKING_SERVICE);
    }

    @Bean
    @GRpcGlobalInterceptor
    public SaigonParkingServerInterceptor saigonParkingServerInterceptor() {
        return new SaigonParkingServerInterceptor(
                new ImmutableMap.Builder<Class<? extends Throwable>, String>()
                        .put(EntityNotFoundException.class, "SPE#00008")
                        .put(DataIntegrityViolationException.class, "SPE#00009")
                        .put(PermissionDeniedException.class, "SPE#00015")
                        .put(ObjectOptimisticLockingFailureException.class, "SPE#00016")
                        .put(EmptyResultDataAccessException.class, "SPE#00018")
                        .put(BookingAlreadyFinishedException.class, "SPE#00019")
                        .put(CustomerHasOnGoingBookingException.class, "SPE#00020")
                        .put(BookingNotYetAcceptedException.class, "SPE#00021")
                        .put(BookingAlreadyRatedException.class, "SPE#00022")
                        .put(BookingNotYetRatedException.class, "SPE#00023")
                        .put(BookingNotYetFinishedException.class, "SPE#00024")
                        .build());
    }
}