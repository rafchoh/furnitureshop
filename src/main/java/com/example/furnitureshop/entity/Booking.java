package com.example.furnitureshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_booking_user"))
    private Users user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "furniture_id", nullable = false, foreignKey = @ForeignKey(name = "fk_booking_furniture"))
    private Furniture furniture;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Column(name = "delivery_address", length = 255)
    private String deliveryAddress;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private String status = STATUS_PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}