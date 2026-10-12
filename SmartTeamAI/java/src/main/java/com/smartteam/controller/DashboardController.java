package com.smartteam.controller;

import com.smartteam.entity.*;
import com.smartteam.repository.ProjectRepository;
import com.smartteam.repository.StudentRepository;
import com.smartteam.repository.TeamMemberRepository;
import com.smartteam.service.ProjectTeamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final StudentRepository studentRepo;
    private final ProjectRepository projectRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final ProjectTeamService projectTeamService;

    public DashboardController(StudentRepository studentRepo, ProjectRepository projectRepo,
                               TeamMemberRepository teamMemberRepo, ProjectTeamService projectTeamService) {
        this.studentRepo = studentRepo;
        this.projectRepo = projectRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.projectTeamService = projectTeamService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "all") String filter, HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) {
            return "redirect:/login";
        }

        Student student = studentRepo.findById(studentId).orElseThrow();
        Map<String, Object> stats = projectTeamService.getStudentDashboardStats(studentId);
        List<ProjectRequest> pendingRequests = projectTeamService.getPendingRequestsForUser(studentId);

        List<Project> displayedProjects;
        if ("my".equalsIgnoreCase(filter)) {
            displayedProjects = projectRepo.findByOwnerIdOrderByIdDesc(studentId);
        } else if ("collaborating".equalsIgnoreCase(filter)) {
            List<TeamMember> memberships = teamMemberRepo.findByStudentId(studentId);
            displayedProjects = memberships.stream()
                    .map(TeamMember::getProject)
                    .filter(p -> !p.getOwner().getId().equals(studentId))
                    .distinct()
                    .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                    .collect(Collectors.toList());
        } else {
            displayedProjects = projectRepo.findAllByOrderByIdDesc();
        }

        Map<Long, List<TeamMember>> membersMap = projectTeamService.getMembersMapForProjects(displayedProjects);

        // All active students for inviting collaborators
        List<Student> candidates = studentRepo.findByActiveTrue().stream()
                .filter(s -> !s.getId().equals(studentId))
                .collect(Collectors.toList());

        List<Rating> studentRatings = projectTeamService.getStudentRatings(studentId);

        model.addAttribute("student", student);
        model.addAttribute("projects", displayedProjects);
        model.addAttribute("stats", stats);
        model.addAttribute("membersMap", membersMap);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("activeFilter", filter);
        model.addAttribute("candidates", candidates);
        model.addAttribute("studentRatings", studentRatings);

        return "dashboard/index";
    }
}
