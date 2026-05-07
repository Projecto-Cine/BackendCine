package com.cine.demo.repository;

import com.cine.demo.model.RoomBooking;
import com.cine.demo.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {
    List<RoomBooking> findByUserId(Long userId);
    List<RoomBooking> findByRoomId(Long roomId);
    List<RoomBooking> findByBookingDateBetween(LocalDate from, LocalDate to);
    long countByStatus(BookingStatus status);
}
