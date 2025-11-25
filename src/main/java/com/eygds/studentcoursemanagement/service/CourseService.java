package com.eygds.studentcoursemanagement.service;

import com.eygds.studentcoursemanagement.entity.CourseEntity;
import com.eygds.studentcoursemanagement.entity.StudentEntity;
import com.eygds.studentcoursemanagement.model.Course;
import com.eygds.studentcoursemanagement.model.Student;
import com.eygds.studentcoursemanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository repo;

  public Course save(final Course course) {
    CourseEntity entity = new CourseEntity();
    entity.setName(course.getName());
    CourseEntity saved = repo.save(entity);
    return mapToModel(saved);
  }

  public List<Course> findAll() {
    return repo.findAll()
        .stream()
        .map(this::mapToModel)
        .collect(Collectors.toList());
  }

  public Course getById(final Long id) {
    return repo.findById(id)
        .map(this::mapToModel)
        .orElse(null);
  }

  public Course update(final Long id, final Course updatedCourse) {
    return repo.findById(id)
        .map(existing -> {
          existing.setName(updatedCourse.getName());
          CourseEntity saved = repo.save(existing);
          return mapToModel(saved);
        })
        .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
  }

  public boolean delete(final Long id) {
    if (repo.existsById(id)) {
      repo.deleteById(id);
      return true;
    }
    return false;
  }

  private Course mapToModel(CourseEntity courseEntity) {
    List<Student> students = courseEntity.getStudents() == null ? List.of() :
        courseEntity.getStudents()
            .stream()
            .map(this::mapStudentToModel)
            .collect(Collectors.toList());

    return new Course(courseEntity.getId(), courseEntity.getName(), students);
  }

  private Student mapStudentToModel(StudentEntity studentEntity) {
    return new Student(
        studentEntity.getId(),
        studentEntity.getName(),
        studentEntity.getEmail(),
        studentEntity.getCourse() != null ? studentEntity.getCourse().getName() : null
    );
  }
}
