package sso.pro.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class SSOClientFilter implements Filter {

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取当前会话对象
        HttpSession session = request.getSession();
        // 获取用户名
        String username = (String) session.getAttribute("username");
        // 获取票据
        String ticket = request.getParameter("ticket");
        // 对请求地址进行编码
        String url = URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");

        // 用户名为空
        if (null == username) {
            // 票据不为空
            if (null != ticket && !"".equals(ticket)) {
                PostMethod postMethod = new PostMethod("http://localhost:8087/sso-server/ticket");
                postMethod.addParameter("ticket", ticket);
                postMethod.addParameter("service", url);
                HttpClient httpClient = new HttpClient();
                try {
                    // 用票据兑换用户名
                    httpClient.executeMethod(postMethod);
                    username = postMethod.getResponseBodyAsString();
                    postMethod.releaseConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 用户名不为空
                if (null != username && !"".equals(username)) {
                    // 设置用户名到当前会话对象中
                    session.setAttribute("username", username);
                    // 放行
                    filterChain.doFilter(request, response);
                } else {
                    // 用户名为空 跳转登陆页面
                    response.sendRedirect("http://localhost:8087/sso-server/login.jsp?service=" + url);
                }
            } else {
                // 票据为空跳转登陆页面
                response.sendRedirect("http://localhost:8087/sso-server/login.jsp?service=" + url);
            }
        } else {
            // 放行
            filterChain.doFilter(request, response);
        }
    }

    public void init(FilterConfig arg0) throws ServletException {
    }

}