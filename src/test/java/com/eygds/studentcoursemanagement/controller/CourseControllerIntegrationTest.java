package com.eygds.studentcoursemanagement.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eygds.studentcoursemanagement.model.Course;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private Course sampleCourse() {
    Course course = new Course();
    course.setName("Math 101");
    return course;
  }

  // --------------------
  // CREATE COURSE (ADMIN)
  // --------------------
  @Test
  void testCreateCourseAsAdmin() throws Exception {
    String json = objectMapper.writeValueAsString(sampleCourse());

    mockMvc.perform(post("/api/courses")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Math 101"));
  }

  // --------------------
  // GET ALL COURSES (USER & ADMIN)
  // --------------------
  @Test
  void testGetAllCoursesAsUser() throws Exception {
    // First, create a course as ADMIN
    String json = objectMapper.writeValueAsString(sampleCourse());
    mockMvc.perform(post("/api/courses")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk());

    // Fetch as USER
    mockMvc.perform(get("/api/courses")
            .with(user("user").roles("USER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Math 101"));
  }

  // --------------------
  // UPDATE COURSE (ADMIN ONLY)
  // --------------------
  @Test
  void testUpdateCourseAsAdmin() throws Exception {
    // Create course first
    String json = objectMapper.writeValueAsString(sampleCourse());
    String response = mockMvc.perform(post("/api/courses")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andReturn()
        .getResponse()
        .getContentAsString();

    Course created = objectMapper.readValue(response, Course.class);
    created.setName("Updated Math");

    mockMvc.perform(put("/api/courses/" + created.getId())
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(created)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Math"));
  }

  @Test
  void testUpdateCourseAsUserForbidden() throws Exception {
    // Create course as ADMIN
    String json = objectMapper.writeValueAsString(sampleCourse());
    String response = mockMvc.perform(post("/api/courses")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andReturn()
        .getResponse()
        .getContentAsString();

    Course created = objectMapper.readValue(response, Course.class);
    created.setName("Updated Math");

    // Try to update as USER -> should be forbidden
    mockMvc.perform(put("/api/courses/" + created.getId())
            .with(user("user").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(created)))
        .andExpect(status().isForbidden());
  }

  // --------------------
  // DELETE COURSE (ADMIN ONLY)
  // --------------------
  @Test
  void testDeleteCourseAsAdmin() throws Exception {
    // Create course first
    String json = objectMapper.writeValueAsString(sampleCourse());
    String response = mockMvc.perform(post("/api/courses")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andReturn()
        .getResponse()
        .getContentAsString();

    Course created = objectMapper.readValue(response, Course.class);

    mockMvc.perform(delete("/api/courses/" + created.getId())
            .with(user("admin").roles("ADMIN")))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteCourseAsUserForbidden() throws Exception {
    // Create course first
    String json = objectMapper.writeValueAsString(sampleCourse());
    String response = mockMvc.perform(post("/api/courses")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andReturn()
        .getResponse()
        .getContentAsString();

    Course created = objectMapper.readValue(response, Course.class);

    // Attempt delete as USER -> forbidden
    mockMvc.perform(delete("/api/courses/" + created.getId())
            .with(user("user").roles("USER")))
        .andExpect(status().isForbidden());
  }

  // --------------------
  // GET COURSE BY ID
  // --------------------
  @Test
  void testGetCourseByIdAsUser() throws Exception {
    // Create course as ADMIN
    String json = objectMapper.writeValueAsString(sampleCourse());
    String response = mockMvc.perform(post("/api/courses")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andReturn()
        .getResponse()
        .getContentAsString();

    Course created = objectMapper.readValue(response, Course.class);

    // Fetch as USER
    mockMvc.perform(get("/api/courses/" + created.getId())
            .with(user("user").roles("USER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Math 101"));
  }
}
