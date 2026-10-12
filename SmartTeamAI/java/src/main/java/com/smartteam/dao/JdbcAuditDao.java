package com.smartteam.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAuditDao {
    private final JdbcTemplate jdbc;
    public JdbcAuditDao(JdbcTemplate jdbc){this.jdbc=jdbc;}
    public int countStudents(){ return jdbc.queryForObject("SELECT COUNT(*) FROM students", Integer.class); }
    public int countProjects(){ return jdbc.queryForObject("SELECT COUNT(*) FROM projects", Integer.class); }
}
