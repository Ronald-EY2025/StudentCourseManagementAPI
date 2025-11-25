package com.eygds.studentcoursemanagement.service;

import com.eygds.studentcoursemanagement.entity.CourseEntity;
import com.eygds.studentcoursemanagement.entity.StudentEntity;
import com.eygds.studentcoursemanagement.model.Student;
import com.eygds.studentcoursemanagement.repository.CourseRepository;
import com.eygds.studentcoursemanagement.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository repo;
    private final CourseRepository courseRepository;

    public Student save(final Student student) {
        StudentEntity entity = new StudentEntity();
        entity.setName(student.getName());
        entity.setEmail(student.getEmail());

        if (student.getCourse() != null) {
            CourseEntity course = getCourseByName(student.getCourse());
            entity.setCourse(course);
            course.getStudents().add(entity);
        }

        StudentEntity saved = repo.save(entity);
        return mapToModel(saved);
    }

    public List<Student> findAll() {
        return repo.findAll()
            .stream()
            .map(this::mapToModel)
            .collect(Collectors.toList());
    }

    public Optional<Student> getById(final Long id) {
        return repo.findById(id).map(this::mapToModel);
    }

    public Student update(final Long id, final Student updatedStudent) {
        return repo.findById(id)
            .map(existing -> {
                existing.setName(updatedStudent.getName());
                existing.setEmail(updatedStudent.getEmail());

                if (updatedStudent.getCourse() != null) {
                    CourseEntity course = getCourseByName(updatedStudent.getCourse());
                    existing.setCourse(course);
                }

                StudentEntity saved = repo.save(existing);
                return mapToModel(saved);
            })
            .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public boolean delete(final Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private CourseEntity getCourseByName(final String courseName) {
        return courseRepository.findByNameIgnoreCase(courseName)
            .orElseThrow(() -> new RuntimeException("Course not found with name: " + courseName));
    }

    private Student mapToModel(StudentEntity entity) {
        return new Student(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getCourse() != null ? entity.getCourse().getName() : null
        );
    }
}
