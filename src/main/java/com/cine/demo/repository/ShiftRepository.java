package com.cine.demo.repository;

import com.cine.demo.model.Shift;
import com.cine.demo.model.enums.ShiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByShiftDate(LocalDate date);
    List<Shift> findByShiftDateBetween(LocalDate from, LocalDate to);
    List<Shift> findByEmployeeName(String employeeName);
    List<Shift> findByStatus(ShiftStatus status);
}
