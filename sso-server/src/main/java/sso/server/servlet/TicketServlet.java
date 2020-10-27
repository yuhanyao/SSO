package sso.server.servlet;

import sso.server.JVMCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TicketServlet extends HttpServlet {
    private static final long serialVersionUID = 5964206637772848290L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取票据
        String ticket = request.getParameter("ticket");
        // 从内存中获取用户名
        String username = JVMCache.TICKET_AND_NAME.get(ticket);
        // 移除票据 票据仅可以使用一次
        JVMCache.TICKET_AND_NAME.remove(ticket);
        // 将用户名返回
        PrintWriter writer = response.getWriter();
        writer.write(username);
    }

}