package com.eygds.studentcoursemanagement.controller;

import com.eygds.studentcoursemanagement.model.Course;
import com.eygds.studentcoursemanagement.service.CourseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Course> createCourse(@RequestBody final Course course) {
    return ResponseEntity.ok(courseService.save(course));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public ResponseEntity<List<Course>> getAllCourses() {
    return ResponseEntity.ok(courseService.findAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public ResponseEntity<Course> getCourseById(@PathVariable final Long id) {
    final Course course = courseService.getById(id);
    if (course == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(course);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Course> updateCourse(
      @PathVariable final Long id,
      @RequestBody final Course updatedCourse) {
    try {
      final Course savedCourse = courseService.update(id, updatedCourse);
      return ResponseEntity.ok(savedCourse);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteCourse(@PathVariable final Long id) {
    final boolean deleted = courseService.delete(id);
    if (deleted) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}
