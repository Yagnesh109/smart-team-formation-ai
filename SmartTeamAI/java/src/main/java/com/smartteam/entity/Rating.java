package com.smartteam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_student", nullable = false)
    private Student fromStudent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_student", nullable = false)
    private Student toStudent;

    @Column(name = "technical_score", nullable = false)
    private Integer technicalScore;

    @Column(name = "teamwork_score", nullable = false)
    private Integer teamworkScore;

    @Column(name = "reliability_score", nullable = false)
    private Integer reliabilityScore;

    @Column(length = 1000)
    private String review;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Rating() {}

    public Rating(Project project, Student fromStudent, Student toStudent, Integer technicalScore, Integer teamworkScore, Integer reliabilityScore, String review) {
        this.project = project;
        this.fromStudent = fromStudent;
        this.toStudent = toStudent;
        this.technicalScore = technicalScore;
        this.teamworkScore = teamworkScore;
        this.reliabilityScore = reliabilityScore;
        this.review = review;
    }

    public Double getAverageScore() {
        if (technicalScore == null || teamworkScore == null || reliabilityScore == null) return 0.0;
        return (technicalScore + teamworkScore + reliabilityScore) / 3.0;
    }

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public Student getFromStudent() { return fromStudent; }
    public Student getToStudent() { return toStudent; }
    public Integer getTechnicalScore() { return technicalScore; }
    public Integer getTeamworkScore() { return teamworkScore; }
    public Integer getReliabilityScore() { return reliabilityScore; }
    public String getReview() { return review; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setProject(Project project) { this.project = project; }
    public void setFromStudent(Student fromStudent) { this.fromStudent = fromStudent; }
    public void setToStudent(Student toStudent) { this.toStudent = toStudent; }
    public void setTechnicalScore(Integer technicalScore) { this.technicalScore = technicalScore; }
    public void setTeamworkScore(Integer teamworkScore) { this.teamworkScore = teamworkScore; }
    public void setReliabilityScore(Integer reliabilityScore) { this.reliabilityScore = reliabilityScore; }
    public void setReview(String review) { this.review = review; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
