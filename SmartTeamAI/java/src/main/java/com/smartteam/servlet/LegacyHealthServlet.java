package com.smartteam.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns="/legacy/health", name="LegacyHealthServlet")
public class LegacyHealthServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().write("{\"service\":\"SmartTeam AI\",\"status\":\"UP\",\"technology\":\"Jakarta Servlet\"}");
    }
}
