package com.library.repository;

import com.library.model.Booking;
import com.library.model.Room;
import com.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    List<Booking> findByRoom(Room room);

    void deleteByUser(User user);
}
