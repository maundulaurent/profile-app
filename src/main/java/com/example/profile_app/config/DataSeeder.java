package com.example.profile_app.config;

import com.example.profile_app.entity.*;
import com.example.profile_app.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Component
public class DataSeeder implements CommandLineRunner {

    private final ProfileRepository profileRepo;
    private final ProjectRepository projectRepo;
    private final SkillRepository skillRepo;

    public DataSeeder(ProfileRepository profileRepo, ProjectRepository projectRepo, SkillRepository skillRepo) {
        this.profileRepo = profileRepo;
        this.projectRepo = projectRepo;
        this.skillRepo = skillRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        // SEED ONCE: if skills table already has data, do nothing.
        if (skillRepo.count() > 0) {
            System.out.println("Data already seeded. Skipping.");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        // 1. Skills
        try (InputStream is = new ClassPathResource("data/skills.json").getInputStream()) {
            JsonNode skills = mapper.readTree(is);
            for (JsonNode s : skills) {
                skillRepo.save(new Skill(s.get("id").asText(), s.get("label").asText()));
            }
        }

        // 2. Profiles
        try (InputStream is = new ClassPathResource("data/profiles.json").getInputStream()) {
            JsonNode profiles = mapper.readTree(is);
            for (JsonNode p : profiles) {
                Profile profile = new Profile();
                profile.setId(p.get("id").asText());
                profile.setName(p.get("name").asText());
                profile.setHeadline(p.get("headline").asText());
                profile.setBiography(p.get("biography").asText());
                profile.setPublished(p.get("published").asBoolean());
                profile.setContactEmail(p.get("contact_email").asText());

                List<String> skills = new ArrayList<>();
                for (JsonNode sk : p.get("skill_ids")) {
                    skills.add(sk.asText());
                }
                profile.setSkillIds(skills);

                // Assign owners: p01 -> ownerA, p02 -> ownerB. Others no owner.
                if ("p01".equals(profile.getId())) {
                    profile.setOwnerUsername("ownerA");
                } else if ("p02".equals(profile.getId())) {
                    profile.setOwnerUsername("ownerB");
                }

                profileRepo.save(profile);
            }
        }

        // 3. Projects
        try (InputStream is = new ClassPathResource("data/projects.json").getInputStream()) {
            JsonNode projects = mapper.readTree(is);
            for (JsonNode j : projects) {
                Project project = new Project();
                project.setId(j.get("id").asText());
                project.setTitle(j.get("title").asText());
                project.setDescription(j.get("description").asText());

                List<String> links = new ArrayList<>();
                for (JsonNode l : j.get("evidence_links")) {
                    links.add(l.asText());
                }
                project.setEvidenceLinks(links);

                List<Project.Contributor> contributors = new ArrayList<>();
                for (JsonNode c : j.get("contributors")) {
                    contributors.add(new Project.Contributor(
                            c.get("profile_id").asText(),
                            c.get("role").asText()
                    ));
                }
                project.setContributors(contributors);

                projectRepo.save(project);
            }
        }

        System.out.println("Seeding complete.");
    }
}