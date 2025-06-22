package com.amartha.billing.repository;

import com.amartha.billing.entity.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, UUID> {
}
