package com.smartteam.repository;

import com.smartteam.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByProjectId(Long projectId);
    List<TeamMember> findByStudentId(Long studentId);
    Optional<TeamMember> findByProjectIdAndStudentId(Long projectId, Long studentId);
    boolean existsByProjectIdAndStudentId(Long projectId, Long studentId);
    long countByProjectId(Long projectId);
}
