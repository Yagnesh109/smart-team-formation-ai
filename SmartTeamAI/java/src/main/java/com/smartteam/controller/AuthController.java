package com.smartteam.controller;

import com.smartteam.entity.Student;
import com.smartteam.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth){this.auth=auth;}

    @GetMapping("/") public String home(){return "redirect:/login";}
    @GetMapping("/login") public String loginPage(){return "auth/login";}
    @GetMapping("/register") public String registerPage(){return "auth/register";}

    @PostMapping("/register")
    public String register(@RequestParam String name,@RequestParam String email,@RequestParam String password,
                           @RequestParam String department,@RequestParam int studyYear,@RequestParam String experienceLevel,
                           @RequestParam String availability,@RequestParam(required=false) String interests,
                           @RequestParam(required=false) String bio,Model model){
        try { auth.register(name,email,password,department,studyYear,experienceLevel,availability,interests,bio); return "redirect:/login?registered"; }
        catch(Exception e){model.addAttribute("error",e.getMessage()); return "auth/register";}
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,@RequestParam String password,HttpSession session,Model model){
        try { Student s=auth.login(email,password); session.setAttribute("studentId",s.getId()); session.setAttribute("studentName",s.getName()); return "redirect:/dashboard"; }
        catch(Exception e){model.addAttribute("error",e.getMessage()); return "auth/login";}
    }

    @GetMapping("/logout") public String logout(HttpSession session){session.invalidate(); return "redirect:/login?logout";}
}
