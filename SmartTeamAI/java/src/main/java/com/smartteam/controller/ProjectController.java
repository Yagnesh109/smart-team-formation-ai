package com.smartteam.controller;

import com.smartteam.entity.*;
import com.smartteam.repository.ProjectRepository;
import com.smartteam.repository.StudentRepository;
import com.smartteam.service.ProjectTeamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepo;
    private final StudentRepository studentRepo;
    private final ProjectTeamService projectTeamService;

    public ProjectController(ProjectRepository projectRepo, StudentRepository studentRepo, ProjectTeamService projectTeamService) {
        this.projectRepo = projectRepo;
        this.studentRepo = studentRepo;
        this.projectTeamService = projectTeamService;
    }

    @GetMapping("/new")
    public String form(HttpSession session) {
        return session.getAttribute("studentId") == null ? "redirect:/login" : "project/new";
    }

    @PostMapping("/new")
    public String create(@RequestParam String name, @RequestParam String description,
                         @RequestParam int teamSize, @RequestParam String requiredSkills,
                         HttpSession session, RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/login";

        try {
            projectTeamService.createProject(name, description, studentId, teamSize, requiredSkills);
            ra.addFlashAttribute("successMessage", "Project created successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/complete")
    public String completeProject(@PathVariable("id") Long projectId, HttpSession session, RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/login";

        try {
            projectTeamService.completeProject(projectId, studentId);
            ra.addFlashAttribute("successMessage", "Project has been marked as COMPLETED!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/invite")
    public String inviteCollaborator(@PathVariable("id") Long projectId,
                                     @RequestParam Long candidateId,
                                     @RequestParam(required = false, defaultValue = "Would love for you to join our project!") String message,
                                     HttpSession session, RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/login";

        try {
            projectTeamService.sendInvitation(projectId, studentId, candidateId, message);
            ra.addFlashAttribute("successMessage", "Invitation sent successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/request-join")
    public String requestToJoin(@PathVariable("id") Long projectId,
                                @RequestParam(required = false, defaultValue = "Hi! I am interested in collaborating on this project.") String message,
                                HttpSession session, RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/login";

        try {
            projectTeamService.sendJoinRequest(projectId, studentId, message);
            ra.addFlashAttribute("successMessage", "Join request sent to project owner!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/requests/{requestId}/respond")
    public String respondToRequest(@PathVariable("requestId") Long requestId,
                                   @RequestParam boolean accept,
                                   @RequestParam(required = false, defaultValue = "Collaborator") String roleName,
                                   HttpSession session, RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/login";

        try {
            projectTeamService.respondToRequest(requestId, studentId, accept, roleName);
            ra.addFlashAttribute("successMessage", accept ? "Request accepted! Member added to team." : "Request declined.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}/feedback")
    public String feedbackPage(@PathVariable("id") Long projectId, HttpSession session, Model model, RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/login";

        Project project = projectRepo.findById(projectId).orElse(null);
        if (project == null) {
            ra.addFlashAttribute("errorMessage", "Project not found");
            return "redirect:/dashboard";
        }

        List<TeamMember> members = projectTeamService.getProjectMembers(projectId);
        List<Student> teammates = members.stream()
                .map(TeamMember::getStudent)
                .filter(s -> !s.getId().equals(studentId))
                .collect(Collectors.toList());

        model.addAttribute("project", project);
        model.addAttribute("teammates", teammates);
        model.addAttribute("currentStudentId", studentId);

        return "project/feedback";
    }

    @PostMapping("/{id}/rate")
    public String submitRating(@PathVariable("id") Long projectId,
                               @RequestParam Long toStudentId,
                               @RequestParam int technicalScore,
                               @RequestParam int teamworkScore,
                               @RequestParam int reliabilityScore,
                               @RequestParam(required = false) String review,
                               HttpSession session, RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/login";

        try {
            projectTeamService.submitRating(projectId, studentId, toStudentId, technicalScore, teamworkScore, reliabilityScore, review);
            ra.addFlashAttribute("successMessage", "Feedback rating submitted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projects/" + projectId + "/feedback";
    }
}
