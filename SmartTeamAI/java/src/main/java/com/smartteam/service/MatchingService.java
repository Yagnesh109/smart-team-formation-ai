package com.smartteam.service;

import com.smartteam.entity.Student;
import com.smartteam.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.*;

@Service
public class MatchingService {
    private final StudentRepository studentRepo;
    private final RestClient mlClient;
    public MatchingService(StudentRepository studentRepo, RestClient.Builder builder){
        this.studentRepo=studentRepo;
        this.mlClient=builder.baseUrl("http://127.0.0.1:8000").build();
    }

    public List<Map<String,Object>> recommend(String requiredSkills, String interest, String availability, String experience){
        List<String> required=Arrays.stream(requiredSkills.split(",")).map(String::trim).filter(s->!s.isBlank()).map(String::toLowerCase).toList();
        List<Map<String,Object>> result=new ArrayList<>();
        for(Student s:studentRepo.findByActiveTrue()){
            double skill=localSkillScore(required,s);
            double interestScore=containsIgnoreCase(s.getInterests(),interest)?100:30;
            double avail=containsIgnoreCase(s.getAvailability(),availability)?100:40;
            double exp=experience.equalsIgnoreCase(s.getExperienceLevel())?100:60;
            Map<String,Object> features=Map.of("skill_match",skill,"interest_match",interestScore,"availability",avail,"experience",exp);
            double ml=mlScore(features);
            double finalScore=0.70*ml+0.30*skill;
            result.add(new LinkedHashMap<>(Map.of("studentId",s.getId(),"name",s.getName(),"department",s.getDepartment(),"experience",s.getExperienceLevel(),"skillMatch",round(skill),"mlScore",round(ml),"matchScore",round(finalScore))));
        }
        result.sort((a,b)->Double.compare((Double)b.get("matchScore"),(Double)a.get("matchScore")));
        return result.stream().limit(10).toList();
    }

    private double localSkillScore(List<String> required,Student s){
        if(required.isEmpty()) return 0;
        String all=(s.getBio()==null?"":s.getBio())+" "+(s.getInterests()==null?"":s.getInterests());
        long hit=required.stream().filter(k->all.toLowerCase().contains(k)).count();
        return 100.0*hit/required.size();
    }
    private boolean containsIgnoreCase(String text,String needle){return text!=null && needle!=null && !needle.isBlank() && text.toLowerCase().contains(needle.toLowerCase());}
    private double mlScore(Map<String,Object> f){
        try { Map<?,?> r=mlClient.post().uri("/predict").body(f).retrieve().body(Map.class); return r!=null && r.get("probability") instanceof Number n?n.doubleValue()*100:50; }
        catch(Exception e){return 50;}
    }
    private double round(double v){return Math.round(v*10.0)/10.0;}
}
