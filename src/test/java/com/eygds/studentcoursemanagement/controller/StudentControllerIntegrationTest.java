package com.eygds.studentcoursemanagement.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eygds.studentcoursemanagement.model.Student;
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
class StudentControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private Student sampleStudent() {
    Student student = new Student();
    student.setName("John Doe");
    student.setEmail("john@example.com");
    return student;
  }

  // --------------------
  // CREATE STUDENT
  // --------------------
  @Test
  void testCreateStudentAsAdmin() throws Exception {
    String json = objectMapper.writeValueAsString(sampleStudent());

    mockMvc.perform(post("/api/students")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"));
  }

  // --------------------
  // GET ALL STUDENTS
  // --------------------
  @Test
  void testGetAllStudentsAsUser() throws Exception {
    // Create a student first
    String json = objectMapper.writeValueAsString(sampleStudent());
    mockMvc.perform(post("/api/students")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/students")
            .with(user("user").roles("USER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("John Doe"));
  }

  // --------------------
  // GET STUDENT BY ID
  // --------------------
  @Test
  void testGetStudentByIdAsUser() throws Exception {
    String json = objectMapper.writeValueAsString(sampleStudent());
    String response = mockMvc.perform(post("/api/students")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andReturn()
        .getResponse()
        .getContentAsString();

    Student created = objectMapper.readValue(response, Student.class);

    mockMvc.perform(get("/api/students/" + created.getId())
            .with(user("user").roles("USER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Doe"));
  }

  // --------------------
  // UPDATE STUDENT
  // --------------------
  @Test
  void testUpdateStudentAsAdmin() throws Exception {
    String json = objectMapper.writeValueAsString(sampleStudent());
    String response = mockMvc.perform(post("/api/students")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andReturn()
        .getResponse()
        .getContentAsString();

    Student created = objectMapper.readValue(response, Student.class);
    created.setName("Jane Doe");

    mockMvc.perform(put("/api/students/" + created.getId())
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(created)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Jane Doe"));
  }

  // --------------------
  // DELETE STUDENT
  // --------------------
  @Test
  void testDeleteStudentAsAdmin() throws Exception {
    String json = objectMapper.writeValueAsString(sampleStudent());
    String response = mockMvc.perform(post("/api/students")
            .with(user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andReturn()
        .getResponse()
        .getContentAsString();

    Student created = objectMapper.readValue(response, Student.class);

    mockMvc.perform(delete("/api/students/" + created.getId())
            .with(user("admin").roles("ADMIN")))
        .andExpect(status().isNoContent());
  }
}
