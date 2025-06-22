package com.amartha.billing.repository;

import com.amartha.billing.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {

    @Query("SELECT l FROM Loan l LEFT JOIN FETCH l.repaymentSchedules WHERE l.id = :loanId")
    Optional<Loan> findByIdWithRepaymentSchedules(@Param("loanId") UUID loanId);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM RepaymentSchedule r " +
            "WHERE r.loan.id = :loanId AND r.status <> 'PAID'")
    BigDecimal findOutstandingAmountByLoanId(@Param("loanId") UUID loanId);
}
