package com.smartteam.controller;

import com.smartteam.entity.Project;
import com.smartteam.repository.ProjectRepository;
import com.smartteam.service.MatchingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/matching")
public class MatchingController {

    private final MatchingService matching;
    private final ProjectRepository projectRepo;

    public MatchingController(MatchingService matching, ProjectRepository projectRepo) {
        this.matching = matching;
        this.projectRepo = projectRepo;
    }

    @GetMapping
    public String page(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/login";

        List<Project> myProjects = projectRepo.findByOwnerIdOrderByIdDesc(studentId);
        model.addAttribute("myProjects", myProjects);
        return "student/matching";
    }

    @PostMapping
    public String recommend(@RequestParam String requiredSkills, @RequestParam String interest,
                            @RequestParam String availability, @RequestParam String experience,
                            HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/login";

        List<Project> myProjects = projectRepo.findByOwnerIdOrderByIdDesc(studentId);

        model.addAttribute("requiredSkills", requiredSkills);
        model.addAttribute("interest", interest);
        model.addAttribute("availability", availability);
        model.addAttribute("experience", experience);
        model.addAttribute("myProjects", myProjects);
        model.addAttribute("results", matching.recommend(requiredSkills, interest, availability, experience, studentId));
        return "student/matching";
    }
}
