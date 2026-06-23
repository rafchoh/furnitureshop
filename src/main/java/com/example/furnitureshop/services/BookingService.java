package com.example.furnitureshop.services;

import com.example.furnitureshop.entity.Booking;
import com.example.furnitureshop.entity.Furniture;
import com.example.furnitureshop.entity.Users;
import com.example.furnitureshop.repo.BookingRepo;
import com.example.furnitureshop.repo.DTOs.BookingDTO;
import com.example.furnitureshop.repo.FurnitureRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepo bookingRepository;
    private final FurnitureRepo furnitureRepository;

    public BookingService(BookingRepo bookingRepository, FurnitureRepo furnitureRepository) {
        this.bookingRepository = bookingRepository;
        this.furnitureRepository = furnitureRepository;
    }

    public List<Booking> getCartItems(Users user) {
        return bookingRepository.findByUserAndStatus(user, Booking.STATUS_PENDING);
    }

    @Transactional
    public void addToCart(Users user, Furniture furniture) {
        boolean alreadyInCart = bookingRepository
                .findByUserAndFurnitureIdAndStatus(user, furniture.getId(), Booking.STATUS_PENDING)
                .isPresent();

        if (!alreadyInCart) {
            Booking cartEntry = new Booking();
            cartEntry.setUser(user);
            cartEntry.setFurniture(furniture);
            cartEntry.setStatus(Booking.STATUS_PENDING);
            bookingRepository.save(cartEntry);
        }
    }

    @Transactional
    public void removeFromCart(Users user, UUID bookingId) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            if (booking.getUser().getId().equals(user.getId())
                    && Booking.STATUS_PENDING.equals(booking.getStatus())) {
                bookingRepository.delete(booking);
            }
        });
    }

    @Transactional
    public int checkout(Users user, BookingDTO dto) {
        List<Booking> cartItems = getCartItems(user);

        if (cartItems.isEmpty()) {
            return 0;
        }

        for (Booking booking : cartItems) {
            booking.setBookingDate(dto.getBookingDate());
            booking.setDeliveryAddress(dto.getDeliveryAddress());
            booking.setNotes(dto.getNotes());
            booking.setStatus(Booking.STATUS_CONFIRMED);

            Furniture furniture = booking.getFurniture();
            furniture.setAvailable(false);
            furnitureRepository.save(furniture);
        }

        bookingRepository.saveAll(cartItems);
        return cartItems.size();
    }

    public List<Booking> getUserBookings(Users user) {
        return bookingRepository.findByUserAndStatusNot(user, Booking.STATUS_PENDING);
    }

    @Transactional
    public boolean cancelBooking(Users user, UUID bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return false;

        Booking booking = opt.get();
        if (!booking.getUser().getId().equals(user.getId())) return false;
        if (!Booking.STATUS_CONFIRMED.equals(booking.getStatus())) return false;

        booking.setStatus(Booking.STATUS_CANCELLED);
        bookingRepository.save(booking);

        Furniture furniture = booking.getFurniture();
        furniture.setAvailable(true);
        furnitureRepository.save(furniture);
        
        return true;
    }
}