package com.smartteam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name="students")
public class Student {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false) private String name;
    @Column(nullable=false, unique=true) private String email;
    @Column(name="password_hash", nullable=false) private String passwordHash;
    @Column(nullable=false) private String department;
    @Column(name="study_year", nullable=false) private Integer studyYear;
    @Column(name="experience_level", nullable=false) private String experienceLevel;
    @Column(nullable=false) private String availability;
    private String interests;
    private String bio;
    @Column(nullable=false) private boolean active=true;
    @Column(name="created_at") private LocalDateTime createdAt=LocalDateTime.now();

    public Student() {}
    public Student(String name,String email,String passwordHash,String department,Integer studyYear,String experienceLevel,String availability,String interests,String bio){
        this.name=name;this.email=email;this.passwordHash=passwordHash;this.department=department;this.studyYear=studyYear;this.experienceLevel=experienceLevel;this.availability=availability;this.interests=interests;this.bio=bio;
    }
    public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;}
    public String getDepartment(){return department;} public Integer getStudyYear(){return studyYear;} public String getExperienceLevel(){return experienceLevel;} public String getAvailability(){return availability;}
    public String getInterests(){return interests;} public String getBio(){return bio;} public boolean isActive(){return active;}
    public void setId(Long id){this.id=id;} public void setName(String v){name=v;} public void setEmail(String v){email=v;} public void setPasswordHash(String v){passwordHash=v;}
    public void setDepartment(String v){department=v;} public void setStudyYear(Integer v){studyYear=v;} public void setExperienceLevel(String v){experienceLevel=v;} public void setAvailability(String v){availability=v;}
    public void setInterests(String v){interests=v;} public void setBio(String v){bio=v;} public void setActive(boolean v){active=v;}
}
