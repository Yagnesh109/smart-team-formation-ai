package com.smartteam.entity;
import jakarta.persistence.*;
@Entity @Table(name="skills")
public class Skill {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String name;
 private String category;
 public Skill(){} public Skill(String name,String category){this.name=name;this.category=category;}
 public Long getId(){return id;} public String getName(){return name;} public String getCategory(){return category;}
 public void setId(Long v){id=v;} public void setName(String v){name=v;} public void setCategory(String v){category=v;}
}
