package myssm.listeners;

import myssm.ioc.BeanFactory;
import myssm.ioc.ClassPathXmlApplicationContext;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.*;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


@WebListener
public class ContextLoaderListener implements ServletContextListener {
    @Override
    //ServletContext监听器,这个方法会在Servlet创建的时候调用,创建IOC容器
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //获取上下文参数
        ServletContext application = servletContextEvent.getServletContext();
        //创建IOC容器
        BeanFactory beanFactory = new ClassPathXmlApplicationContext();
        //将IOC容器保存到application作用域
        application.setAttribute("beanFactory",beanFactory);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
