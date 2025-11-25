package com.eygds.studentcoursemanagement.repository;
import com.eygds.studentcoursemanagement.entity.CourseEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

  Optional<CourseEntity> findByNameIgnoreCase(String name);

}
