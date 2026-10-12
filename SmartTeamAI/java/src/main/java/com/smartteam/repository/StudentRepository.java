package com.smartteam.repository;
import com.smartteam.entity.Student; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface StudentRepository extends JpaRepository<Student,Long>{Optional<Student> findByEmail(String email); List<Student> findByActiveTrue();}
