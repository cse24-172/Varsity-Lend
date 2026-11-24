
package bw.co.varsitylend.backend.student;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://cse24-172.github.io")
public class RegistrationController {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegistrationRequest request) {

        if (!request.getPassword().equals(request.getConfirm())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passwords do not match"
            );
        }

        String email = request.getEmail().trim().toLowerCase();

        if (studentRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }

        Student student = new Student();
        student.setFullName(request.getFullname().trim());
        student.setEmail(email);
        student.setPhone(request.getPhone().trim());
        student.setInstitution(request.getInstitution().trim());
        student.setStudentNumber(
                request.getStudentid() == null
                        ? null
                        : request.getStudentid().trim()
        );
        student.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        Student savedStudent = studentRepository.save(student);

        Map<String, Object> response = Map.of(
                "message", "Your account has been created successfully",
                "studentId", savedStudent.getId(),
                "fullName", savedStudent.getFullName(),
                "email", savedStudent.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
