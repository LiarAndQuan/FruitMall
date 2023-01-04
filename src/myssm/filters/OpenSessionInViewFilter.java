package myssm.filters;

import myssm.trans.TransactionManger;

import javax.servlet.*;
import java.io.IOException;


public class OpenSessionInViewFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            TransactionManger.beginTransaction();
            filterChain.doFilter(servletRequest, servletResponse);
            TransactionManger.commit();
        } catch (Exception e) {
            TransactionManger.rollback();
        }
    }

    @Override
    public void destroy() {

    }
}
