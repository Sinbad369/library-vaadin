package com.rubinbaldoro.library.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.rubinbaldoro.library.entity.Student;
import com.rubinbaldoro.library.entity.VerificationToken;
import com.rubinbaldoro.library.repository.StudentRepository;
import com.rubinbaldoro.library.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    // FIX: Added the missing arguments to the constructor!
    public StudentService(StudentRepository studentRepository,
                          VerificationTokenRepository tokenRepository,
                          EmailService emailService) {
        this.studentRepository = studentRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    public List<Student> findAll(String filterText){
        if(filterText == null || filterText.isEmpty()){
            return studentRepository.findAll();
        } else{
            return studentRepository.search(filterText);
        }
    }

    public Student getById(Long id){
        return studentRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Student not found with id: " + id));
    }

    // FIX: Combined the two save methods into one perfect method
    @Transactional
    public Student save(Student student) {
        boolean isNew = (student.getId() == null);

        if (isNew) {
            // 1. Check if login already exists
            if (studentRepository.findByLogin(student.getLogin()).isPresent()) {
                throw new RuntimeException("Username already exists!");
            }

            // 2. Check if email already exists
            List<Student> allStudents = studentRepository.findAll();
            boolean emailExists = allStudents.stream()
                    .anyMatch(s -> s.getEmail().equalsIgnoreCase(student.getEmail()));
            if (emailExists) {
                throw new RuntimeException("Email already registered!");
            }

            // 3. Initially disabled until verified
            student.setEnabled(false);
        }

        // Save the student
        Student savedStudent = studentRepository.save(student);

        // Generate token and send email for NEW registrations
        if (isNew) {
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(token, savedStudent);
            tokenRepository.save(verificationToken);

            // Send Email
            emailService.sendVerificationEmail(savedStudent.getEmail(), token);
        }

        return savedStudent;
    }

    public void delete(Student student){
        studentRepository.delete(student);
    }

    public void deleteById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found");
        }
        try {
            studentRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete student. They might still have books.");
        }
    }

    public long count(){
        return studentRepository.count();
    }

    public Optional<Student> findByLogin(String login) {
        return studentRepository.findByLogin(login);
    }

    @Transactional
    public boolean verifyToken(String token) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // Token invalid or expired
        }

        Student student = tokenOpt.get().getStudent();
        student.setEnabled(true);
        studentRepository.save(student);

        // Delete token after successful use
        tokenRepository.delete(tokenOpt.get());
        return true;
    }

    // Gets the currently logged-in student
    public Optional<Student> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return Optional.empty();
        }
        // auth.getName() returns the 'login' username
        return studentRepository.findByLogin(auth.getName());
    }
}