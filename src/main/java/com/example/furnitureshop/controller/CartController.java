package com.example.furnitureshop.controller;

import com.example.furnitureshop.entity.Booking;
import com.example.furnitureshop.entity.Furniture;
import com.example.furnitureshop.entity.Users;
import com.example.furnitureshop.repo.DTOs.BookingDTO;
import com.example.furnitureshop.services.BookingService;
import com.example.furnitureshop.services.FurnitureService;
import com.example.furnitureshop.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final BookingService bookingService;
    private final FurnitureService furnitureService;
    private final UserService userService;

    public CartController(BookingService bookingService,
                          FurnitureService furnitureService,
                          UserService userService) {
        this.bookingService = bookingService;
        this.furnitureService = furnitureService;
        this.userService = userService;
    }

    private Users currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return userService.findByUsername(auth.getName());
    }

    private BigDecimal calcTotal(List<Booking> items) {
        return items.stream()
                .map(b -> b.getFurniture().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @GetMapping
    public String viewCart(Model model) {
        Users user = currentUser();
        List<Booking> cartItems = bookingService.getCartItems(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", calcTotal(cartItems));

        return "cart";
    }


    @PostMapping("/add/{furnitureId}")
    public String addToCart(@PathVariable UUID furnitureId,
                            RedirectAttributes redirectAttributes) {
        Users user = currentUser();
        Furniture furniture = furnitureService.getFurnitureById(furnitureId);

        if (furniture == null || !furniture.isAvailable()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Item is not available.");
            return "redirect:/";
        }

        bookingService.addToCart(user, furniture);
        redirectAttributes.addFlashAttribute("successMessage", "Item added to cart.");

        return "redirect:/cart";
    }

    @PostMapping("/remove/{bookingId}")
    public String removeFromCart(@PathVariable UUID bookingId,
                                 RedirectAttributes redirectAttributes) {
        Users user = currentUser();
        bookingService.removeFromCart(user, bookingId);
        redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");

        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        Users user = currentUser();
        List<Booking> cartItems = bookingService.getCartItems(user);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("furnitureList", cartItems.stream()
                .map(Booking::getFurniture)
                .toList());
        model.addAttribute("total", calcTotal(cartItems));

        if (!model.containsAttribute("checkoutDTO")) {
            model.addAttribute("checkoutDTO", new BookingDTO());
        }

        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute("checkoutDTO") BookingDTO dto,
                                  BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes) {
        Users user = currentUser();

        if (!bindingResult.hasErrors()) {
            List<Booking> cartItems = bookingService.getCartItems(user);
            model.addAttribute("furnitureList", cartItems.stream()
                    .map(Booking::getFurniture)
                    .toList());
            model.addAttribute("total", calcTotal(cartItems));

            return "checkout";
        }

        int confirmed = bookingService.checkout(user, dto);

        if (confirmed == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart was empty.");
            return "redirect:/cart";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                confirmed + " item(s) successfully booked!");

        return "redirect:/cart/bookings";
    }

    @GetMapping("/bookings")
    public String viewBookings(Model model) {
        Users user = currentUser();
        model.addAttribute("bookings", bookingService.getUserBookings(user));

        return "bookings";
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public String cancelBooking(@PathVariable UUID bookingId,
                                RedirectAttributes redirectAttributes) {
        Users user = currentUser();
        boolean cancelled = bookingService.cancelBooking(user, bookingId);

        if (cancelled) {
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not cancel booking. It may already be cancelled or not belong to you.");
        }

        return "redirect:/cart/bookings";
    }
}