package com.smartteam.repository;

import com.smartteam.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByToStudentIdOrderByIdDesc(Long toStudentId);
    List<Rating> findByProjectId(Long projectId);
    List<Rating> findByProjectIdAndFromStudentId(Long projectId, Long fromStudentId);
    boolean existsByProjectIdAndFromStudentIdAndToStudentId(Long projectId, Long fromStudentId, Long toStudentId);
}
