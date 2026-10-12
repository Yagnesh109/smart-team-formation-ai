package com.smartteam.service;

import com.smartteam.entity.*;
import com.smartteam.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectTeamService {

    private final ProjectRepository projectRepo;
    private final StudentRepository studentRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final ProjectRequestRepository requestRepo;
    private final RatingRepository ratingRepo;

    public ProjectTeamService(ProjectRepository projectRepo, StudentRepository studentRepo,
                              TeamMemberRepository teamMemberRepo, ProjectRequestRepository requestRepo,
                              RatingRepository ratingRepo) {
        this.projectRepo = projectRepo;
        this.studentRepo = studentRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.requestRepo = requestRepo;
        this.ratingRepo = ratingRepo;
    }

    @Transactional
    public Project createProject(String name, String description, Long ownerId, int teamSize, String requiredSkills) {
        Student owner = studentRepo.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Project project = new Project(name, description, owner, teamSize, requiredSkills);
        Project saved = projectRepo.save(project);

        // Automatically add owner as team member
        TeamMember ownerMember = new TeamMember(saved, owner, "Project Owner");
        teamMemberRepo.save(ownerMember);

        return saved;
    }

    @Transactional
    public void sendInvitation(Long projectId, Long ownerId, Long candidateId, String message) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        if (!project.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("Only the project owner can send invitations.");
        }
        Student candidate = studentRepo.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate student not found"));
        Student owner = studentRepo.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        if (teamMemberRepo.existsByProjectIdAndStudentId(projectId, candidateId)) {
            throw new IllegalStateException("Student is already a member of this project.");
        }

        if (requestRepo.existsByProjectIdAndStudentIdAndStatus(projectId, candidateId, "PENDING")) {
            throw new IllegalStateException("An invitation or request is already pending for this student.");
        }

        ProjectRequest req = new ProjectRequest(project, candidate, owner, "INVITATION", message);
        requestRepo.save(req);
    }

    @Transactional
    public void sendJoinRequest(Long projectId, Long applicantId, String message) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        if (project.getOwner().getId().equals(applicantId)) {
            throw new IllegalStateException("You are the owner of this project.");
        }
        Student applicant = studentRepo.findById(applicantId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (teamMemberRepo.existsByProjectIdAndStudentId(projectId, applicantId)) {
            throw new IllegalStateException("You are already a team member in this project.");
        }

        if (requestRepo.existsByProjectIdAndStudentIdAndStatus(projectId, project.getOwner().getId(), "PENDING")) {
            // Check if applicant already requested for this project
            boolean existing = requestRepo.findByProjectId(projectId).stream()
                    .anyMatch(r -> r.getRequestedBy().getId().equals(applicantId) && "PENDING".equals(r.getStatus()));
            if (existing) {
                throw new IllegalStateException("You have already sent a join request for this project.");
            }
        }

        ProjectRequest req = new ProjectRequest(project, project.getOwner(), applicant, "APPLICATION", message);
        requestRepo.save(req);
    }

    @Transactional
    public void respondToRequest(Long requestId, Long currentUserId, boolean accept, String roleName) {
        ProjectRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new IllegalStateException("This request has already been processed.");
        }

        // Check permission
        if ("INVITATION".equals(request.getRequestType())) {
            // Collaborator accepting invite: target student must be current user
            if (!request.getStudent().getId().equals(currentUserId)) {
                throw new IllegalStateException("Unauthorized to respond to this invitation.");
            }
        } else if ("APPLICATION".equals(request.getRequestType())) {
            // Owner accepting join request: target student (owner) must be current user
            if (!request.getStudent().getId().equals(currentUserId)) {
                throw new IllegalStateException("Unauthorized to respond to this application.");
            }
        }

        if (accept) {
            request.setStatus("ACCEPTED");
            Student newMemberStudent = "INVITATION".equals(request.getRequestType()) ? request.getStudent() : request.getRequestedBy();
            String assignedRole = (roleName != null && !roleName.isBlank()) ? roleName : "Collaborator";

            if (!teamMemberRepo.existsByProjectIdAndStudentId(request.getProject().getId(), newMemberStudent.getId())) {
                TeamMember newMember = new TeamMember(request.getProject(), newMemberStudent, assignedRole);
                teamMemberRepo.save(newMember);
            }
        } else {
            request.setStatus("REJECTED");
        }

        requestRepo.save(request);
    }

    @Transactional
    public void completeProject(Long projectId, Long ownerId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        if (!project.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("Only the project creator can complete this project.");
        }
        project.setStatus("COMPLETED");
        projectRepo.save(project);
    }

    @Transactional
    public void submitRating(Long projectId, Long fromStudentId, Long toStudentId, int techScore, int teamworkScore, int reliabilityScore, String review) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!"COMPLETED".equalsIgnoreCase(project.getStatus())) {
            throw new IllegalStateException("Ratings can only be submitted for completed projects.");
        }

        if (fromStudentId.equals(toStudentId)) {
            throw new IllegalArgumentException("You cannot rate yourself.");
        }

        boolean isFromMember = teamMemberRepo.existsByProjectIdAndStudentId(projectId, fromStudentId);
        boolean isToMember = teamMemberRepo.existsByProjectIdAndStudentId(projectId, toStudentId);

        if (!isFromMember || !isToMember) {
            throw new IllegalStateException("Both students must be members of the project to submit ratings.");
        }

        if (ratingRepo.existsByProjectIdAndFromStudentIdAndToStudentId(projectId, fromStudentId, toStudentId)) {
            throw new IllegalStateException("You have already rated this teammate for this project.");
        }

        Student from = studentRepo.findById(fromStudentId).orElseThrow();
        Student to = studentRepo.findById(toStudentId).orElseThrow();

        Rating rating = new Rating(project, from, to, techScore, teamworkScore, reliabilityScore, review);
        ratingRepo.save(rating);
    }

    public List<TeamMember> getProjectMembers(Long projectId) {
        return teamMemberRepo.findByProjectId(projectId);
    }

    public Map<Long, List<TeamMember>> getMembersMapForProjects(List<Project> projects) {
        Map<Long, List<TeamMember>> map = new HashMap<>();
        for (Project p : projects) {
            map.put(p.getId(), teamMemberRepo.findByProjectId(p.getId()));
        }
        return map;
    }

    public Map<String, Object> getStudentDashboardStats(Long studentId) {
        List<Project> myProjects = projectRepo.findByOwnerIdOrderByIdDesc(studentId);
        List<TeamMember> memberships = teamMemberRepo.findByStudentId(studentId);

        List<Project> collaboratingProjects = memberships.stream()
                .map(TeamMember::getProject)
                .filter(p -> !p.getOwner().getId().equals(studentId))
                .distinct()
                .collect(Collectors.toList());

        // Calculate total unique collaborators
        Set<Long> uniqueCollaboratorIds = new HashSet<>();
        // 1. Members in projects created by studentId
        for (Project p : myProjects) {
            List<TeamMember> members = teamMemberRepo.findByProjectId(p.getId());
            for (TeamMember m : members) {
                if (!m.getStudent().getId().equals(studentId)) {
                    uniqueCollaboratorIds.add(m.getStudent().getId());
                }
            }
        }
        // 2. Teammates in collaborating projects
        for (Project p : collaboratingProjects) {
            List<TeamMember> members = teamMemberRepo.findByProjectId(p.getId());
            for (TeamMember m : members) {
                if (!m.getStudent().getId().equals(studentId)) {
                    uniqueCollaboratorIds.add(m.getStudent().getId());
                }
            }
        }

        // Ratings stats
        List<Rating> receivedRatings = ratingRepo.findByToStudentIdOrderByIdDesc(studentId);
        double avgScore = 0.0;
        if (!receivedRatings.isEmpty()) {
            double total = receivedRatings.stream().mapToDouble(Rating::getAverageScore).sum();
            avgScore = Math.round((total / receivedRatings.size()) * 10.0) / 10.0;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("myProjectsCount", myProjects.size());
        stats.put("collaboratingProjectsCount", collaboratingProjects.size());
        stats.put("totalCollaboratorsCount", uniqueCollaboratorIds.size());
        stats.put("avgRating", avgScore);
        stats.put("ratingsCount", receivedRatings.size());
        return stats;
    }

    public List<ProjectRequest> getPendingRequestsForUser(Long studentId) {
        return requestRepo.findByStudentIdAndStatusOrderByIdDesc(studentId, "PENDING");
    }

    public List<Rating> getStudentRatings(Long studentId) {
        return ratingRepo.findByToStudentIdOrderByIdDesc(studentId);
    }
}
