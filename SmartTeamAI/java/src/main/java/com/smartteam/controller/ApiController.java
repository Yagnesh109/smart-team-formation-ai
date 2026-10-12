package com.smartteam.controller;

import com.smartteam.dao.JdbcAuditDao; import org.springframework.web.bind.annotation.*; import java.util.Map;

@RestController @RequestMapping("/api")
public class ApiController {
 private final JdbcAuditDao audit; public ApiController(JdbcAuditDao audit){this.audit=audit;}
 @GetMapping("/stats") public Map<String,Integer> stats(){return Map.of("students",audit.countStudents(),"projects",audit.countProjects());}
}
