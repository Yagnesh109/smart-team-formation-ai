package com.smartteam.repository;

import com.smartteam.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatusOrderByIdDesc(String status);
    List<Project> findByOwnerIdOrderByIdDesc(Long ownerId);
    List<Project> findAllByOrderByIdDesc();
    List<Project> findByIdInOrderByIdDesc(List<Long> ids);
}
