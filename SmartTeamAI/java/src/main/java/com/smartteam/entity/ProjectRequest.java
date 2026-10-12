package com.smartteam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_requests")
public class ProjectRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requested_by", nullable = false)
    private Student requestedBy;

    @Column(name = "request_type", nullable = false, length = 20)
    private String requestType; // INVITATION or APPLICATION

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, ACCEPTED, REJECTED

    @Column(length = 500)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ProjectRequest() {}

    public ProjectRequest(Project project, Student student, Student requestedBy, String requestType, String message) {
        this.project = project;
        this.student = student;
        this.requestedBy = requestedBy;
        this.requestType = requestType;
        this.message = message;
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public Student getStudent() { return student; }
    public Student getRequestedBy() { return requestedBy; }
    public String getRequestType() { return requestType; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setProject(Project project) { this.project = project; }
    public void setStudent(Student student) { this.student = student; }
    public void setRequestedBy(Student requestedBy) { this.requestedBy = requestedBy; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
