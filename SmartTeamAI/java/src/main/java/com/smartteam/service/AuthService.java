package com.smartteam.service;
import com.smartteam.entity.Student; import com.smartteam.repository.StudentRepository; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Service;
@Service public class AuthService {
 private final StudentRepository repo; private final PasswordEncoder encoder;
 public AuthService(StudentRepository repo,PasswordEncoder encoder){this.repo=repo;this.encoder=encoder;}
 public Student register(String name,String email,String password,String dept,int year,String exp,String availability,String interests,String bio){
  if(repo.findByEmail(email).isPresent()) throw new IllegalArgumentException("Email already registered");
  return repo.save(new Student(name,email,encoder.encode(password),dept,year,exp,availability,interests,bio));
 }
 public Student login(String email,String password){ Student s=repo.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Invalid email or password")); if(!encoder.matches(password,s.getPasswordHash())) throw new IllegalArgumentException("Invalid email or password"); return s; }
}
