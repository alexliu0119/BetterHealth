package com.health.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.health.model.BodyComposition;
import com.health.model.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BodyCompositionDAO extends JpaRepository<BodyComposition, Long> {
    List<BodyComposition> findByUserAndRecordDateBetween(User user, Date startDate, Date endDate);
    Optional<BodyComposition> findTopByUserOrderByRecordDateDesc(User user);
	List<BodyComposition> findByUserOrderByRecordDateDesc(User user);
}