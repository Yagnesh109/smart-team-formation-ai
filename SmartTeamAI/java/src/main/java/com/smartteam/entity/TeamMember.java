package com.smartteam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"project_id", "student_id"})
})
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "role_name", nullable = false, length = 80)
    private String roleName;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();

    public TeamMember() {}

    public TeamMember(Project project, Student student, String roleName) {
        this.project = project;
        this.student = student;
        this.roleName = roleName;
    }

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public Student getStudent() { return student; }
    public String getRoleName() { return roleName; }
    public LocalDateTime getJoinedAt() { return joinedAt; }

    public void setId(Long id) { this.id = id; }
    public void setProject(Project project) { this.project = project; }
    public void setStudent(Student student) { this.student = student; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
