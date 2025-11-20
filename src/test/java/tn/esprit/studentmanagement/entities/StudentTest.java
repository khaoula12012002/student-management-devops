package tn.esprit.studentmanagement.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {
    @Test
    public void testConstructorAndGetters() {
        Department department = new Department();
        List<Enrollment> enrollments = new ArrayList<>();
        LocalDate dob = LocalDate.of(2000, 1, 1);
        Student student = new Student(1L, "John", "Doe", "john@example.com", "1234567890", dob, "123 Main St", department, enrollments);
        assertEquals(1L, student.getIdStudent());
        assertEquals("John", student.getFirstName());
        assertEquals("Doe", student.getLastName());
        assertEquals("john@example.com", student.getEmail());
        assertEquals("1234567890", student.getPhone());
        assertEquals(dob, student.getDateOfBirth());
        assertEquals("123 Main St", student.getAddress());
        assertEquals(department, student.getDepartment());
        assertEquals(enrollments, student.getEnrollments());
    }

    @Test
    public void testSetters() {
        Student student = new Student();
        student.setIdStudent(2L);
        student.setFirstName("Jane");
        student.setLastName("Smith");
        student.setEmail("jane@example.com");
        student.setPhone("0987654321");
        LocalDate dob = LocalDate.of(1999, 5, 15);
        student.setDateOfBirth(dob);
        student.setAddress("456 Elm St");
        Department department = new Department();
        student.setDepartment(department);
        List<Enrollment> enrollments = new ArrayList<>();
        student.setEnrollments(enrollments);
        assertEquals(2L, student.getIdStudent());
        assertEquals("Jane", student.getFirstName());
        assertEquals("Smith", student.getLastName());
        assertEquals("jane@example.com", student.getEmail());
        assertEquals("0987654321", student.getPhone());
        assertEquals(dob, student.getDateOfBirth());
        assertEquals("456 Elm St", student.getAddress());
        assertEquals(department, student.getDepartment());
        assertEquals(enrollments, student.getEnrollments());
    }

    @Test
    public void testEqualsAndHashCode() {
        Department department = new Department();
        List<Enrollment> enrollments = new ArrayList<>();
        LocalDate dob = LocalDate.of(2000, 1, 1);
        Student student1 = new Student(1L, "John", "Doe", "john@example.com", "1234567890", dob, "123 Main St", department, enrollments);
        Student student2 = new Student(1L, "John", "Doe", "john@example.com", "1234567890", dob, "123 Main St", department, enrollments);
        assertEquals(student1, student2);
        assertEquals(student1.hashCode(), student2.hashCode());
    }

    @Test
    public void testToString() {
        Department department = new Department();
        List<Enrollment> enrollments = new ArrayList<>();
        LocalDate dob = LocalDate.of(2000, 1, 1);
        Student student = new Student(1L, "John", "Doe", "john@example.com", "1234567890", dob, "123 Main St", department, enrollments);
        String str = student.toString();
        assertTrue(str.contains("John"));
        assertTrue(str.contains("Doe"));
        assertTrue(str.contains("john@example.com"));
    }
}
