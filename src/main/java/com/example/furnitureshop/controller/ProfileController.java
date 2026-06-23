package com.example.furnitureshop.controller;

import com.example.furnitureshop.entity.Users;
import com.example.furnitureshop.repo.DTOs.UsersDTO;
import com.example.furnitureshop.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    private Users getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userService.findByUsername(username);
    }

    @GetMapping
    public String viewProfile(Model model) {
        Users user = getCurrentUser();

        model.addAttribute("user", user);

        return "profile";
    }

    @GetMapping("/{username}")
    public String showEditForm(@PathVariable String username, Model model) {
        Users user = getCurrentUser();
        UsersDTO dto = new UsersDTO();

        dto.setUsername(username);
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        model.addAttribute("userDTO", dto);
        model.addAttribute("isAdmin", user.getRole().equals(Users.ROLE_ADMIN));

        return "redirect:/register?edit/" + username;
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("userDTO") UsersDTO dto,
                                BindingResult bindingResult, Model model,
                                HttpServletRequest request) {
        Users currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRole().equals(Users.ROLE_ADMIN);
        String originalUsername = currentUser.getUsername();

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match.");
                model.addAttribute("isAdmin", isAdmin);
                return "redirect:/register?edit/" + originalUsername;
            }

            try {
                if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
                    currentUser.setUsername(dto.getUsername());
                } else {
                    currentUser.setUsername(currentUser.getUsername());
                }

                if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
                    currentUser.setEmail(dto.getEmail());
                } else {
                    currentUser.setEmail(currentUser.getEmail());
                }

                String newRole = null;

                if (dto.getRole() != null && !dto.getRole().isEmpty()) {
                    if (dto.getRole().equals(Users.ROLE_ADMIN) || dto.getRole().equals(Users.ROLE_USER)) {
                        currentUser.setRole(dto.getRole());
                        newRole = dto.getRole();
                    } else {
                        currentUser.setRole(currentUser.getRole());
                    }
                }

                if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty() && dto.getPassword().equals(dto.getConfirmPassword())) {
                    currentUser.setPassword(dto.getNewPassword());
                } else {
                    currentUser.setPassword(dto.getPassword());
                }

                userService.updateUser(currentUser);

                if (newRole != null) {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getName().equals(originalUsername)) {
                        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                                auth.getPrincipal(), auth.getCredentials(),
                                List.of(new SimpleGrantedAuthority("ROLE_" + newRole)));
                        SecurityContextHolder.getContext().setAuthentication(newAuth);

                        HttpSession session = request.getSession(false);

                        if (session != null) {
                            session.setAttribute(
                                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                                    SecurityContextHolder.getContext()
                            );
                        }
                    }
                }

                return "redirect:/profile?updated";
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("isAdmin", isAdmin);
            }
        }

            return "redirect:/register?edit/" + originalUsername;
    }
}
