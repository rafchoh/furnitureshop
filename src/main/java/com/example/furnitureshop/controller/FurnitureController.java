package com.example.furnitureshop.controller;

import com.example.furnitureshop.entity.Furniture;
import com.example.furnitureshop.entity.Users;
import com.example.furnitureshop.repo.DTOs.FurnitureDTO;
import com.example.furnitureshop.services.FurnitureService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
public class FurnitureController {
    private final FurnitureService furnitureService;

    public FurnitureController(FurnitureService furnitureService) {
        this.furnitureService = furnitureService;
    }

    @GetMapping("/")
    public String index(Model model) {
//        List<Furniture> furnitureList = furnitureService.getAvailableFurniture();
        List<Furniture> furnitureList = furnitureService.getAllFurniture();
        List<Map<String, Object>> furnitureItems = new ArrayList<>();

        for (Furniture f : furnitureList) {
            Map<String, Object> item = new HashMap<>();
            item.put("furniture", f);

            if (f.getImage() != null) {
                String base64 = Base64.getEncoder().encodeToString(f.getImage());
                item.put("base64Image", base64);
            } else {
                item.put("base64Image", null);
            }

            furnitureItems.add(item);
        }
        model.addAttribute("furnitureItems", furnitureItems);

        return "home";
    }

    @GetMapping("/furniture/{id}")
    public String details(@PathVariable UUID id, Model model) {
        Furniture furniture = furnitureService.getFurnitureById(id);

        if (furniture.getImage() != null) {
            String base64 = Base64.getEncoder().encodeToString(furniture.getImage());
            model.addAttribute("imageBase64", base64);
        }

        model.addAttribute("furniture", furniture);

        return "furn-details";
    }


    @GetMapping("/furniture/admin/add")
    public String showAddForm(Model model) {
        model.addAttribute("furnitureDTO", new FurnitureDTO());
        return "add-furniture";
    }

    @PostMapping("/furniture/admin/add")
    public String addFurniture(@Valid @ModelAttribute("furnitureDTO") FurnitureDTO dto,
                               BindingResult bindingResult, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "add-furniture";
        }
        Furniture furniture = new Furniture();
        furniture.setName(dto.getName());
        furniture.setDescription(dto.getDescription());
        furniture.setPrice(dto.getPrice());
        furniture.setAvailable(dto.isAvailable());
        MultipartFile file = dto.getImageFile();
        if (file != null && !file.isEmpty()) {
            furniture.setImage(file.getBytes());
        }
        furnitureService.saveFurniture(furniture);
        return "redirect:/";
    }

    @GetMapping("/furniture/admin/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Furniture furniture = furnitureService.getFurnitureById(id);
        FurnitureDTO dto = new FurnitureDTO();

        dto.setName(furniture.getName());
        dto.setDescription(furniture.getDescription());
        dto.setPrice(furniture.getPrice());
        dto.setAvailable(furniture.isAvailable());

        if (furniture.getImage() != null) {
            String base64 = Base64.getEncoder().encodeToString(furniture.getImage());
            model.addAttribute("existingImage", base64);
        }

        model.addAttribute("furnitureDTO", dto);
        model.addAttribute("furnitureId", id);
        return "edit-furniture";
    }

    @PostMapping("/furniture/admin/{id}/edit")
    public String editFurniture(@PathVariable UUID id,
                                @Valid @ModelAttribute("furnitureDTO") FurnitureDTO dto,
                                BindingResult bindingResult, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "redirect:/furniture/admin/" + id + "/edit";
        }

        model.addAttribute("furnitureId", id);

        Furniture furniture = furnitureService.getFurnitureById(id);
        furniture.setName(dto.getName());
        furniture.setDescription(dto.getDescription());
        furniture.setPrice(dto.getPrice());
        furniture.setAvailable(dto.isAvailable());

        if (!dto.isDelImg()) {
            MultipartFile file = dto.getImageFile();
            if (file != null && !file.isEmpty()) {
                furniture.setImage(file.getBytes());
            }
        } else {
            furniture.setImage(null);
        }

        furnitureService.saveFurniture(furniture);
        return "redirect:/furniture/" + id ;
    }

    @PostMapping("/furniture/admin/{id}/delete")
    public String deleteFurniture(@PathVariable UUID id) {
        furnitureService.deleteFurniture(id);
        return "redirect:/?deleted";
    }
}
