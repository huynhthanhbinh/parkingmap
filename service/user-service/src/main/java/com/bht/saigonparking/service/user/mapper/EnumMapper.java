package com.bht.saigonparking.service.user.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bht.saigonparking.api.grpc.user.UserRole;
import com.bht.saigonparking.common.base.BaseBean;
import com.bht.saigonparking.service.user.configuration.AppConfiguration;
import com.bht.saigonparking.service.user.entity.UserRoleEntity;
import com.bht.saigonparking.service.user.repository.core.UserRoleRepository;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.Setter;

/**
 *
 * this class is self-customized mapper for all enums, include:
 *      + UserRole:       4 role --> ADMIN, CUSTOMER, PARKING_LOT_EMPLOYEE, GOVERNMENT_EMPLOYEE
 *
 * for using repository inside Component class,
 * we need to {@code @Autowired} it by Spring Dependency Injection
 * we can achieve that easily
 * by using {@code @Setter(onMethod = @__(@Autowired)} for class level like below
 *
 * we cannot use {@code @AllArgsConstructor} for class level,
 * because these repository/injected fields are optional,
 * and it will conflict with {@code @Mapper @Component} bean
 * which will be initialized by NonArgsConstructor !!!!!!!!!
 *
 * @author bht
 */
@Component
@Setter(onMethod = @__(@Autowired))
@Mapper(componentModel = "spring",
        implementationPackage = AppConfiguration.BASE_PACKAGE + ".mapper.impl",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public abstract class EnumMapper implements BaseBean {

    private UserRoleRepository userRoleRepository;
    private static final BiMap<UserRoleEntity, UserRole> USER_ROLE_BI_MAP = HashBiMap.create();
    private static final Map<Long, Long> USER_ROLE_VALUE_MAP = new HashMap<>();

    @Override
    public void initialize() {
        initUserRoleBiMap();
        initUserRoleValueMap();
    }

    @Named("toUserRole")
    public UserRole toUserRole(@NotNull UserRoleEntity userRoleEntity) {
        return USER_ROLE_BI_MAP.get(userRoleEntity);
    }

    @Named("toUserRoleEntity")
    public UserRoleEntity toUserRoleEntity(@NotNull UserRole userRole) {
        return USER_ROLE_BI_MAP.inverse().get(userRole);
    }

    @Named("toUserRoleValue")
    public Long toUserRoleValue(Long userRoleId) {
        return USER_ROLE_VALUE_MAP.get(userRoleId);
    }

    private void initUserRoleBiMap() {
        USER_ROLE_BI_MAP.put(getUserRoleByRoleName("ADMIN"), UserRole.ADMIN);
        USER_ROLE_BI_MAP.put(getUserRoleByRoleName("CUSTOMER"), UserRole.CUSTOMER);
        USER_ROLE_BI_MAP.put(getUserRoleByRoleName("PARKING_LOT_EMPLOYEE"), UserRole.PARKING_LOT_EMPLOYEE);
        USER_ROLE_BI_MAP.put(getUserRoleByRoleName("GOVERNMENT"), UserRole.GOVERNMENT_EMPLOYEE);
    }

    private void initUserRoleValueMap() {
        USER_ROLE_VALUE_MAP.putAll(USER_ROLE_BI_MAP.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getId(), entry -> (long) entry.getValue().getNumber())));
    }

    private UserRoleEntity getUserRoleByRoleName(@NotEmpty String role) {
        return userRoleRepository.findByRole(role).orElseThrow(EntityNotFoundException::new);
    }
}