package com.smartteam.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="projects")
public class Project {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Column(nullable=false,length=2000) private String description;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="owner_id",nullable=false) private Student owner;
 @Column(name="team_size",nullable=false) private Integer teamSize;
 @Column(nullable=false) private String status="OPEN";
 @Column(name="required_skills",nullable=false,length=1000) private String requiredSkills;
 @Column(name="created_at") private LocalDateTime createdAt=LocalDateTime.now();
 public Project(){}
 public Project(String name,String description,Student owner,Integer teamSize,String requiredSkills){this.name=name;this.description=description;this.owner=owner;this.teamSize=teamSize;this.requiredSkills=requiredSkills;}
 public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;} public Student getOwner(){return owner;} public Integer getTeamSize(){return teamSize;} public String getStatus(){return status;} public String getRequiredSkills(){return requiredSkills;}
 public void setId(Long v){id=v;} public void setName(String v){name=v;} public void setDescription(String v){description=v;} public void setOwner(Student v){owner=v;} public void setTeamSize(Integer v){teamSize=v;} public void setStatus(String v){status=v;} public void setRequiredSkills(String v){requiredSkills=v;}
}
