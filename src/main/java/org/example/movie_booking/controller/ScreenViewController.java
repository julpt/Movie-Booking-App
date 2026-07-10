package org.example.movie_booking.controller;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.exceptions.ScreenHasScreeningsException;
import org.example.movie_booking.model.dto.ScreenNameUpdateRequest;
import org.example.movie_booking.model.dto.ScreenResponse;
import org.example.movie_booking.service.CinemaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ScreenViewController {

    private final CinemaService cinemaService;

    @GetMapping("/screens/{screenId}/edit")
    public String editScreenForm(@PathVariable Long screenId,
                                 @RequestParam Long cinemaId, Model model) {
        ScreenResponse screen = cinemaService.getScreenById(screenId);

        model.addAttribute("screenId", screenId);
        model.addAttribute("cinemaId", cinemaId);
        model.addAttribute("screenName", new ScreenNameUpdateRequest(screen.name()));
        return "cinemas/screen-edit";
    }

    @PostMapping("/screens/{screenId}/edit")
    public String updateScreenName(@PathVariable Long screenId,
                                   @Validated @ModelAttribute("screenName") ScreenNameUpdateRequest request,
                                   BindingResult result, Model model,
                                   @RequestParam Long cinemaId) {
        if (result.hasErrors()) {
            model.addAttribute("screenId", screenId);
            model.addAttribute("cinemaId", cinemaId);
            return "cinemas/screen-edit";
        }
        cinemaService.updateScreenName(screenId, request);
        return "redirect:/cinemas/" + cinemaId + "/screens";
    }

    @PostMapping("/screens/{screenId}/delete")
    public String deleteScreen(@PathVariable Long screenId,
                               @RequestParam Long cinemaId,
                               RedirectAttributes redirectAttributes) {
        try {
            cinemaService.deleteScreen(screenId);
        } catch (ScreenHasScreeningsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cinemas/" + cinemaId + "/screens";
    }
}