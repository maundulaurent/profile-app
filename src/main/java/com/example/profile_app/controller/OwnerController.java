package com.example.profile_app.controller;

import com.example.profile_app.entity.Availability;
import com.example.profile_app.entity.Profile;
import com.example.profile_app.repository.ProfileRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    private final ProfileRepository profileRepo;

    public OwnerController(ProfileRepository profileRepo) {
        this.profileRepo = profileRepo;
    }

    private Profile getOwnedProfile(Authentication auth) {
        return profileRepo.findByOwnerUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("No profile for " + auth.getName()));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Profile profile = getOwnedProfile(auth);
        model.addAttribute("profile", profile);
        return "owner/dashboard";
    }

    @GetMapping("/edit")
    public String editForm(Authentication auth, Model model) {
        Profile profile = getOwnedProfile(auth);
        model.addAttribute("profile", profile);
        model.addAttribute("allSkills", List.of("python", "javascript", "sql", "ux", "testing"));
        model.addAttribute("availabilities", Availability.values());
        return "owner/edit";
    }

    @PostMapping("/edit")
    public String saveEdit(Authentication auth,
                           @RequestParam String headline,
                           @RequestParam String biography,
                           @RequestParam(required = false) List<String> skillIds,
                           @RequestParam String contactEmail,
                           @RequestParam String availability,
                           Model model) {

        Profile profile = getOwnedProfile(auth); // OWNERSHIP CHECK

        if (headline == null || headline.isBlank()) {
            return renderFormWithError(model, profile, headline, biography, skillIds, contactEmail, availability,
                    "Headline cannot be blank");
        }
        if (contactEmail == null || !contactEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return renderFormWithError(model, profile, headline, biography, skillIds, contactEmail, availability,
                    "Contact email is not valid");
        }
        List<String> allowed = List.of("python", "javascript", "sql", "ux", "testing");
        if (skillIds != null) {
            for (String s : skillIds) {
                if (!allowed.contains(s)) {
                    return renderFormWithError(model, profile, headline, biography, skillIds, contactEmail, availability,
                            "Unknown skill: " + s);
                }
            }
        }
        Availability avail;
        try {
            avail = Availability.fromValue(availability);
        } catch (IllegalArgumentException e) {
            return renderFormWithError(model, profile, headline, biography, skillIds, contactEmail, availability,
                    "Invalid availability");
        }

        profile.setHeadline(headline);
        profile.setBiography(biography);
        profile.setSkillIds(skillIds == null ? List.of() : skillIds);
        profile.setContactEmail(contactEmail);
        profile.setAvailability(avail);
        profileRepo.save(profile);

        return "redirect:/owner/dashboard";
    }

    private String renderFormWithError(Model model, Profile original,
                                       String headline, String biography,
                                       List<String> skillIds, String contactEmail,
                                       String availability, String error) {
        Profile display = new Profile();
        display.setId(original.getId());
        display.setName(original.getName());
        display.setPublished(original.isPublished());
        display.setHeadline(headline);
        display.setBiography(biography);
        display.setSkillIds(skillIds == null ? List.of() : skillIds);
        display.setContactEmail(contactEmail);
        try {
            display.setAvailability(Availability.fromValue(availability));
        } catch (Exception e) {
            display.setAvailability(original.getAvailability());
        }

        model.addAttribute("profile", display);
        model.addAttribute("allSkills", List.of("python", "javascript", "sql", "ux", "testing"));
        model.addAttribute("availabilities", Availability.values());
        model.addAttribute("error", error);
        return "owner/edit";
    }

    @PostMapping("/publish")
    public String publish(Authentication auth) {
        Profile profile = getOwnedProfile(auth);
        profile.setPublished(true);
        profileRepo.save(profile);
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/unpublish")
    public String unpublish(Authentication auth) {
        Profile profile = getOwnedProfile(auth);
        profile.setPublished(false);
        profileRepo.save(profile);
        return "redirect:/owner/dashboard";
    }

    @GetMapping("/preview")
    public String preview(Authentication auth, Model model) {
        Profile profile = getOwnedProfile(auth);
        model.addAttribute("profile", profile);
        return "owner/preview";
    }
}