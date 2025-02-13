package com.bht.saigonparking.service.user.repository.custom.impl;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;

import com.bht.saigonparking.common.base.BaseEntity_;
import com.bht.saigonparking.common.base.BaseRepositoryCustom;
import com.bht.saigonparking.service.user.entity.UserEntity;
import com.bht.saigonparking.service.user.entity.UserEntity_;
import com.bht.saigonparking.service.user.entity.UserRoleEntity;
import com.bht.saigonparking.service.user.repository.custom.UserRepositoryCustom;

/**
 *
 * @author bht
 */
@Repository
@SuppressWarnings("unchecked")
public class UserRepositoryCustomImpl extends BaseRepositoryCustom implements UserRepositoryCustom {

    @Override
    public List<Tuple> countAllUserGroupByRole() {
        String getCountGroupByQuery = "SELECT U.userRoleEntity.id, COUNT(U.id) " +
                "FROM UserEntity U " +
                "GROUP BY U.userRoleEntity.id ";

        return entityManager.createQuery(getCountGroupByQuery, Tuple.class)
                .getResultList();
    }

    @Override
    public Long countAll() {

        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        return entityManager.createQuery(query
                .select(criteriaBuilder.count(root)))
                .getSingleResult();
    }

    @Override
    public Long countAll(@NotNull Boolean isActivated) {

        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        return entityManager.createQuery(query
                .select(criteriaBuilder.count(root))
                .where(criteriaBuilder.equal(root.get(UserEntity_.isActivated), isActivated)))
                .getSingleResult();
    }

    @Override
    public Long countAll(@NotEmpty String keyword) {

        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        return entityManager.createQuery(query
                .select(criteriaBuilder.count(root))
                .where(criteriaBuilder.or(
                        criteriaBuilder.like(root.get(UserEntity_.username), convertKeyword(keyword)),
                        criteriaBuilder.like(root.get(UserEntity_.email), convertKeyword(keyword)))))
                .getSingleResult();
    }

    @Override
    public Long countAll(@NotNull UserRoleEntity userRoleEntity) {

        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        Join<UserEntity, UserRoleEntity> userRoleEntityJoin = root
                .join(UserEntity_.userRoleEntity, JoinType.LEFT);

        return entityManager.createQuery(query
                .select(criteriaBuilder.count(root))
                .where(criteriaBuilder.equal(userRoleEntityJoin.get(BaseEntity_.id), userRoleEntity.getId())))
                .getSingleResult();
    }

    @Override
    public Long countAll(@NotEmpty String keyword, @NotNull Boolean isActivated) {

        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        return entityManager.createQuery(query
                .select(criteriaBuilder.count(root))
                .where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(UserEntity_.isActivated), isActivated),
                        criteriaBuilder.or(
                                criteriaBuilder.like(root.get(UserEntity_.username), convertKeyword(keyword)),
                                criteriaBuilder.like(root.get(UserEntity_.email), convertKeyword(keyword))))))
                .getSingleResult();
    }

    @Override
    public Long countAll(@NotNull UserRoleEntity userRoleEntity, @NotNull Boolean isActivated) {

        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        Join<UserEntity, UserRoleEntity> userRoleEntityJoin = root
                .join(UserEntity_.userRoleEntity, JoinType.LEFT);

        return entityManager.createQuery(query
                .select(criteriaBuilder.count(root))
                .where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(UserEntity_.isActivated), isActivated),
                        criteriaBuilder.equal(userRoleEntityJoin.get(BaseEntity_.id), userRoleEntity.getId()))))
                .getSingleResult();
    }

    @Override
    public Long countAll(@NotEmpty String keyword, @NotNull UserRoleEntity userRoleEntity) {

        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        Join<UserEntity, UserRoleEntity> userRoleEntityJoin = root
                .join(UserEntity_.userRoleEntity, JoinType.LEFT);

        return entityManager.createQuery(query
                .select(criteriaBuilder.count(root))
                .where(criteriaBuilder.and(
                        criteriaBuilder.equal(userRoleEntityJoin.get(BaseEntity_.id), userRoleEntity.getId()),
                        criteriaBuilder.or(
                                criteriaBuilder.like(root.get(UserEntity_.username), convertKeyword(keyword)),
                                criteriaBuilder.like(root.get(UserEntity_.email), convertKeyword(keyword))))))
                .getSingleResult();
    }

    @Override
    public Long countAll(@NotEmpty String keyword, @NotNull UserRoleEntity userRoleEntity, @NotNull Boolean isActivated) {

        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        Join<UserEntity, UserRoleEntity> userRoleEntityJoin = root
                .join(UserEntity_.userRoleEntity, JoinType.LEFT);

        return entityManager.createQuery(query
                .select(criteriaBuilder.count(root))
                .where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(UserEntity_.isActivated), isActivated),
                        criteriaBuilder.equal(userRoleEntityJoin.get(BaseEntity_.id), userRoleEntity.getId()),
                        criteriaBuilder.or(
                                criteriaBuilder.like(root.get(UserEntity_.username), convertKeyword(keyword)),
                                criteriaBuilder.like(root.get(UserEntity_.email), convertKeyword(keyword))))))
                .getSingleResult();
    }

    @Override
    public List<UserEntity> getAll(@NotNull @Max(20L) Integer nRow, @NotNull Integer pageNumber) {

        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        root.fetch(UserEntity_.userRoleEntity);

        TypedQuery<UserEntity> getAllQuery = entityManager
                .createQuery(query
                        .select(root)
                        .orderBy(criteriaBuilder.asc(root)));

        getAllQuery.setMaxResults(nRow);
        getAllQuery.setFirstResult(nRow * (pageNumber - 1));

        return getAllQuery.getResultList();
    }

    @Override
    public List<UserEntity> getAll(@NotNull @Max(20L) Integer nRow, @NotNull Integer pageNumber, @NotNull Boolean isActivated) {

        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        root.fetch(UserEntity_.userRoleEntity);

        TypedQuery<UserEntity> getAllQuery = entityManager
                .createQuery(query
                        .select(root)
                        .where(criteriaBuilder.equal(root.get(UserEntity_.isActivated), isActivated))
                        .orderBy(criteriaBuilder.asc(root)));

        getAllQuery.setMaxResults(nRow);
        getAllQuery.setFirstResult(nRow * (pageNumber - 1));

        return getAllQuery.getResultList();
    }

    @Override
    public List<UserEntity> getAll(@NotNull @Max(20L) Integer nRow, @NotNull Integer pageNumber, @NotEmpty String keyword) {

        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        root.fetch(UserEntity_.userRoleEntity);

        TypedQuery<UserEntity> getAllQuery = entityManager
                .createQuery(query
                        .select(root)
                        .where(criteriaBuilder.or(
                                criteriaBuilder.like(root.get(UserEntity_.username), convertKeyword(keyword)),
                                criteriaBuilder.like(root.get(UserEntity_.email), convertKeyword(keyword))))
                        .orderBy(criteriaBuilder.asc(root)));

        getAllQuery.setMaxResults(nRow);
        getAllQuery.setFirstResult(nRow * (pageNumber - 1));

        return getAllQuery.getResultList();
    }

    @Override
    public List<UserEntity> getAll(@NotNull @Max(20L) Integer nRow, @NotNull Integer pageNumber, @NotNull UserRoleEntity userRoleEntity) {

        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        Fetch<UserEntity, UserRoleEntity> userRoleEntityFetch = root
                .fetch(UserEntity_.userRoleEntity);
        Join<UserEntity, UserRoleEntity> userRoleEntityJoin =
                (Join<UserEntity, UserRoleEntity>) userRoleEntityFetch;

        TypedQuery<UserEntity> getAllQuery = entityManager
                .createQuery(query
                        .select(root)
                        .where(criteriaBuilder.equal(userRoleEntityJoin.get(BaseEntity_.id), userRoleEntity.getId()))
                        .orderBy(criteriaBuilder.asc(root)));

        getAllQuery.setMaxResults(nRow);
        getAllQuery.setFirstResult(nRow * (pageNumber - 1));

        return getAllQuery.getResultList();
    }

    @Override
    public List<UserEntity> getAll(@NotNull @Max(20L) Integer nRow, @NotNull Integer pageNumber, @NotEmpty String keyword, @NotNull Boolean isActivated) {

        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        root.fetch(UserEntity_.userRoleEntity);

        TypedQuery<UserEntity> getAllQuery = entityManager
                .createQuery(query
                        .select(root)
                        .where(criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(UserEntity_.isActivated), isActivated),
                                criteriaBuilder.or(
                                        criteriaBuilder.like(root.get(UserEntity_.username), convertKeyword(keyword)),
                                        criteriaBuilder.like(root.get(UserEntity_.email), convertKeyword(keyword)))))
                        .orderBy(criteriaBuilder.asc(root)));

        getAllQuery.setMaxResults(nRow);
        getAllQuery.setFirstResult(nRow * (pageNumber - 1));

        return getAllQuery.getResultList();
    }

    @Override
    public List<UserEntity> getAll(@NotNull @Max(20L) Integer nRow, @NotNull Integer pageNumber, @NotNull UserRoleEntity userRoleEntity, @NotNull Boolean isActivated) {

        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        Fetch<UserEntity, UserRoleEntity> userRoleEntityFetch = root
                .fetch(UserEntity_.userRoleEntity);
        Join<UserEntity, UserRoleEntity> userRoleEntityJoin =
                (Join<UserEntity, UserRoleEntity>) userRoleEntityFetch;

        TypedQuery<UserEntity> getAllQuery = entityManager
                .createQuery(query
                        .select(root)
                        .where(criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(UserEntity_.isActivated), isActivated),
                                criteriaBuilder.equal(userRoleEntityJoin.get(BaseEntity_.id), userRoleEntity.getId())))
                        .orderBy(criteriaBuilder.asc(root)));

        getAllQuery.setMaxResults(nRow);
        getAllQuery.setFirstResult(nRow * (pageNumber - 1));

        return getAllQuery.getResultList();
    }

    @Override
    public List<UserEntity> getAll(@NotNull @Max(20L) Integer nRow, @NotNull Integer pageNumber, @NotEmpty String keyword, @NotNull UserRoleEntity userRoleEntity) {

        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        Fetch<UserEntity, UserRoleEntity> userRoleEntityFetch = root
                .fetch(UserEntity_.userRoleEntity);
        Join<UserEntity, UserRoleEntity> userRoleEntityJoin =
                (Join<UserEntity, UserRoleEntity>) userRoleEntityFetch;

        TypedQuery<UserEntity> getAllQuery = entityManager
                .createQuery(query
                        .select(root)
                        .where(criteriaBuilder.and(
                                criteriaBuilder.equal(userRoleEntityJoin.get(BaseEntity_.id), userRoleEntity.getId()),
                                criteriaBuilder.or(
                                        criteriaBuilder.like(root.get(UserEntity_.username), convertKeyword(keyword)),
                                        criteriaBuilder.like(root.get(UserEntity_.email), convertKeyword(keyword)))))
                        .orderBy(criteriaBuilder.asc(root)));

        getAllQuery.setMaxResults(nRow);
        getAllQuery.setFirstResult(nRow * (pageNumber - 1));

        return getAllQuery.getResultList();
    }

    @Override
    public List<UserEntity> getAll(@NotNull @Max(20L) Integer nRow, @NotNull Integer pageNumber, @NotEmpty String keyword, @NotNull UserRoleEntity userRoleEntity, @NotNull Boolean isActivated) {

        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        Fetch<UserEntity, UserRoleEntity> userRoleEntityFetch = root
                .fetch(UserEntity_.userRoleEntity);
        Join<UserEntity, UserRoleEntity> userRoleEntityJoin =
                (Join<UserEntity, UserRoleEntity>) userRoleEntityFetch;

        TypedQuery<UserEntity> getAllQuery = entityManager
                .createQuery(query
                        .select(root)
                        .where(criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(UserEntity_.isActivated), isActivated),
                                criteriaBuilder.equal(userRoleEntityJoin.get(BaseEntity_.id), userRoleEntity.getId()),
                                criteriaBuilder.or(
                                        criteriaBuilder.like(root.get(UserEntity_.username), convertKeyword(keyword)),
                                        criteriaBuilder.like(root.get(UserEntity_.email), convertKeyword(keyword)))))
                        .orderBy(criteriaBuilder.asc(root)));

        getAllQuery.setMaxResults(nRow);
        getAllQuery.setFirstResult(nRow * (pageNumber - 1));

        return getAllQuery.getResultList();
    }
}