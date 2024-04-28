package com.vik.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class StudentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    // After introduction of @ServiceConnection we don't need to manual registering of database
    /*
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }*/

    @Test
    public void testAddNewStudent() {
        Student student = new Student();
        student.setName("Ramesh");
        student.setDateOfBirth("01/01/2020");
        student.setGrade("I");

        ResponseEntity<Student> studentResponseEntity = restTemplate.
                postForEntity("/students", student, Student.class);
        Student savedStudent = studentResponseEntity.getBody();

        assertEquals(OK, studentResponseEntity.getStatusCode());
        assertNotNull(savedStudent);
    }

    @Test
    public void testFetchStudentById() {
        Student student = new Student();
        student.setName("Ramesh");
        student.setDateOfBirth("01/01/2020");
        student.setGrade("I");

        studentRepository.save(student);
        studentRepository.findById(1L);
        System.out.println("student --- "+studentRepository.findById(1L));
        ResponseEntity<Student> studentResponseEntity = restTemplate.
                getForEntity("/students/" + student.getId(), Student.class);
        Student studentResponse = studentResponseEntity.getBody();
        assertEquals(OK, studentResponseEntity.getStatusCode());
        assertNotNull(studentResponse);
        assertEquals(1, studentResponse.getId());
        assertEquals(student.getName(), studentResponse.getName());
        assertEquals(student.getDateOfBirth(), studentResponse.getDateOfBirth());
        assertEquals(student.getGrade(), studentResponse.getGrade());
    }
}
