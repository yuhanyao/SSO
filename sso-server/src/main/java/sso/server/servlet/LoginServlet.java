package sso.server.servlet;

import sso.server.JVMCache;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = -3170191388656385924L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取用户名
        String username = request.getParameter("username");
        // 获取密码
        String password = request.getParameter("password");
        // 获取服务名称
        String service = request.getParameter("service");
        if ("null".equals(service)) {
            service = "";
        }

        // 如果用户名密码匹配
        if ("cloud".equals(username) && "cloud".equals(password)) {
            // 创建一个cookie 设置路径为/
            Cookie cookie = new Cookie("sso", username);
            cookie.setPath("/");
            response.addCookie(cookie);

            long time = System.currentTimeMillis();
            String timeString = username + time;
            JVMCache.TICKET_AND_NAME.put(timeString, username);
            // 如果请求的参数中包含服务名
            if (Objects.nonNull(service) && !"".equals(service)) {
                StringBuilder url = new StringBuilder();
                // 拼接服务名
                url.append(service);
                // 服务名包含?
                if (0 <= service.indexOf("?")) {
                    // 追加一个&
                    url.append("&");
                    // 否则追加？
                } else {
                    url.append("?");
                }
                // 追加票据
                url.append("ticket=").append(timeString);
                // 重定向会源地址
                response.sendRedirect(url.toString());
            } else {
                response.sendRedirect("/sso-server/index.jsp");
            }
        } else {
            response.sendRedirect("/sso/login.jsp?service=" + service);
        }
    }

}