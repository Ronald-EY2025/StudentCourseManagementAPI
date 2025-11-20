package com.eygds.StudentCourseManagementAPI;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.springframework.boot.system.JavaVersion;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentCourseManagementApiApplicationTests {

    /*
	@Test
	void contextLoads() {
	}
	*/

    @Test
    @Order(1)
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("Is Windows the OS of this PC?")
    void testForWinOSOnly() {
        System.out.println("Your OS is " + OS.current());
    }

    @Test
    @Order(2)
    @EnabledOnOs(OS.LINUX)
    @DisplayName("Is Linux the OS of this PC?")
    void testForLinuxOSOnly() {
        System.out.println("Your OS is " + OS.current());
    }

    @Test
    @Order(3)
    @EnabledOnOs(OS.MAC)
    @DisplayName("Is MacOS the OS of this PC?")
    void testForMacOSOnly() {
        System.out.println("Your OS is " + OS.current());
    }

    @Test
    @Order(4)
    @EnabledOnJre(JRE.JAVA_22)
    @DisplayName("Is Java 22 the JRE installed in this PC?")
    void testForJava22() {

    }

    @Test
    @Order(5)
    @EnabledForJreRange(min=JRE.JAVA_8, max=JRE.JAVA_25)
    @DisplayName("Is the JRE installed in this PC falls between Java 8 to Java 25?")
    void
       testForJavaVersions() {
        System.out.println("Your Java JRE Version is " + JavaVersion.getJavaVersion());
    }
}