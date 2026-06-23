package com.example.furnitureshop.repo.DTOs;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class BookingDTO {

    @NotNull(message = "User is required")
        private UUID userId;

    @NotNull(message = "Furniture selection is required")
        private UUID furnitureId;

    @Future(message = "Booking date must be date in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate bookingDate;

    @NotBlank(message = "Delivery address is required")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
        private String deliveryAddress;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
        private String notes;

        private String status;
}
