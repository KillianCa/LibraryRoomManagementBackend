package com.library.service;

import com.library.repository.RoomRepository;
import com.library.repository.UserRepository;
import com.library.repository.BookingRepository;
import com.library.model.User;
import com.library.model.Room;
import com.library.model.Booking;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    final private BookingRepository bookingRepository;
    final private UserRepository userRepository;
    final private RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(bookingRepository::findByUser).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<Booking> getBookingsByRoom(Long roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        return room.map(bookingRepository::findByRoom).orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    @Transactional
    public Booking createBooking(Long userId, long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        List<Booking> existingBookings = bookingRepository.findByRoom(room);
        for (Booking booking : existingBookings) {
            if (startTime.isBefore(booking.getEndTime()) && endTime.isAfter(booking.getStartTime())) {
                throw new IllegalArgumentException("Room is already booked for this time slot");
            }
        }

        Booking booking = new Booking(user, room, startTime, endTime);
        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new IllegalArgumentException("Booking not found");
        }
        bookingRepository.deleteById(bookingId);
    }
}
