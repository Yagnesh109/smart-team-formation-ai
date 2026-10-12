package com.smartteam.repository;

import com.smartteam.entity.ProjectRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProjectRequestRepository extends JpaRepository<ProjectRequest, Long> {
    List<ProjectRequest> findByStudentIdAndStatusOrderByIdDesc(Long studentId, String status);
    List<ProjectRequest> findByRequestedByIdOrderByIdDesc(Long requestedById);
    List<ProjectRequest> findByProjectId(Long projectId);
    List<ProjectRequest> findByStudentIdOrderByIdDesc(Long studentId);
    Optional<ProjectRequest> findByProjectIdAndStudentIdAndRequestedByIdAndStatus(Long projectId, Long studentId, Long requestedById, String status);
    boolean existsByProjectIdAndStudentIdAndStatus(Long projectId, Long studentId, String status);
}
