package com.example.profile_app.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    private String id;

    private String title;

    @Column(length = 2000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_evidence", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "link")
    private List<String> evidenceLinks = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_contributors", joinColumns = @JoinColumn(name = "project_id"))
    private List<Contributor> contributors = new ArrayList<>();

    public Project() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getEvidenceLinks() { return evidenceLinks; }
    public void setEvidenceLinks(List<String> evidenceLinks) { this.evidenceLinks = evidenceLinks; }
    public List<Contributor> getContributors() { return contributors; }
    public void setContributors(List<Contributor> contributors) { this.contributors = contributors; }

    @Embeddable
    public static class Contributor {
        private String profileId;
        private String role;

        public Contributor() {}
        public Contributor(String profileId, String role) {
            this.profileId = profileId;
            this.role = role;
        }
        public String getProfileId() { return profileId; }
        public void setProfileId(String profileId) { this.profileId = profileId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}