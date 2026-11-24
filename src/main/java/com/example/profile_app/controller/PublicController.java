package com.example.profile_app.controller;

import com.example.profile_app.entity.Availability;
import com.example.profile_app.entity.Profile;
import com.example.profile_app.entity.Project;
import com.example.profile_app.repository.ProfileRepository;
import com.example.profile_app.repository.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PublicController {

    private final ProfileRepository profileRepo;
    private final ProjectRepository projectRepo;

    public PublicController(ProfileRepository profileRepo, ProjectRepository projectRepo) {
        this.profileRepo = profileRepo;
        this.projectRepo = projectRepo;
    }

    @GetMapping({"/", "/directory"})
    public String directory(@RequestParam(required = false) String q,
                            @RequestParam(required = false) String skill,
                            @RequestParam(required = false, defaultValue = "any") String availability,
                            Model model) {

        List<Profile> all = profileRepo.findByPublishedTrue();

        List<Profile> filtered = all.stream().filter(p -> {
            if (q != null && !q.isBlank()) {
                String ql = q.toLowerCase();
                boolean match = p.getName().toLowerCase().contains(ql)
                        || p.getHeadline().toLowerCase().contains(ql)
                        || p.getBiography().toLowerCase().contains(ql);
                if (!match) return false;
            }
            if (skill != null && !skill.isBlank() && !p.getSkillIds().contains(skill)) {
                return false;
            }
            if (availability != null && !availability.equals("any")) {
                Availability target = Availability.fromValue(availability);
                if (p.getAvailability() != target) return false;
            }
            return true;
        }).collect(Collectors.toList());

        model.addAttribute("profiles", filtered);
        model.addAttribute("q", q);
        model.addAttribute("skill", skill);
        model.addAttribute("availability", availability);
        model.addAttribute("allSkills", List.of("python", "javascript", "sql", "ux", "testing"));
        return "directory";
    }

    @GetMapping("/profiles/{id}")
    public String profileDetail(@PathVariable String id, Model model) {
        Optional<Profile> p = profileRepo.findByIdAndPublishedTrue(id);
        if (p.isEmpty()) {
            return "not-found";
        }
        Profile profile = p.get();

        // Projects this profile contributes to
        List<Project> associated = projectRepo.findAll().stream()
                .filter(proj -> proj.getContributors().stream()
                        .anyMatch(c -> c.getProfileId().equals(id)))
                .collect(Collectors.toList());

        model.addAttribute("profile", profile);
        model.addAttribute("projects", associated);
        return "profile-detail";
    }

    @GetMapping("/projects/{id}")
    public String projectDetail(@PathVariable String id, Model model) {
        Optional<Project> p = projectRepo.findById(id);
        if (p.isEmpty()) {
            return "not-found";
        }
        Project project = p.get();

        // Strip unpublished contributors from public response (R2.04 / R1 privacy)
        List<Project.Contributor> visible = project.getContributors().stream()
                .filter(c -> profileRepo.findById(c.getProfileId())
                        .map(Profile::isPublished).orElse(false))
                .collect(Collectors.toList());

        model.addAttribute("project", project);
        model.addAttribute("visibleContributors", visible);
        return "project-detail";
    }
}