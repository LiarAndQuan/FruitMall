package myssm.myspringmvc;


import myssm.ioc.BeanFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@WebServlet("*.do")
public class DispatcherServlet extends ViewBaseServlet {

    BeanFactory beanFactory;

    @Override
    public void init() throws ServletException {
        super.init();
        //获取上下文参数
        ServletContext application = getServletContext();
        //获取beanFactory对象
        Object beanFactoryObj = application.getAttribute("beanFactory");
        if (beanFactoryObj != null) {
            //赋值
            beanFactory = (BeanFactory) beanFactoryObj;
        } else {
            throw new RuntimeException("IOC容器为空");
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        //获取具体的请求路径-->/fruit.do?operate=add
        String servletPath = request.getServletPath();
        //去掉'/'
        servletPath = servletPath.substring(1);
        //获取访问的控制器名称
        int lastIndex = servletPath.lastIndexOf(".do");
        servletPath = servletPath.substring(0, lastIndex);
        //获取访问的控制器对象
        Object controllerBeanObj = beanFactory.getBean(servletPath);
        //获取浏览器的operate参数,表示我们要调用那个方法
        String operate = request.getParameter("operate");
        //如果没有获取到或者为"",就默认调用index方法
        if (operate == null || "".equals(operate)) {
            operate = "index";
        }
        try {
            //获取到控制器中所有的方法
            Method[] methods = controllerBeanObj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                //找到与浏览器的operate对应名字的方法
                if (operate.equals(method.getName())) {
                    //获取方法的参数列表
                    Parameter[] parameters = method.getParameters();
                    //准备一个容器存储调用方法的参数值
                    Object[] parametersValue = new Object[parameters.length];
                    for (int i = 0; i < parametersValue.length; i++) {
                        //取出具体的参数对象
                        Parameter parameter = parameters[i];
                        //获取参数的名字
                        String parameterName = parameter.getName();
                        //如果参数名字为request,response,session就直接调用赋值
                        if ("request".equals(parameterName)) {
                            parametersValue[i] = request;
                        } else if ("response".equals(parameterName)) {
                            parametersValue[i] = response;
                        } else if ("session".equals(parameterName)) {
                            parametersValue[i] = request.getSession();
                        } else {
                            //否则就根据名字从浏览器中获取对应的参数值
                            String parameterValue = request.getParameter(parameterName);
                            //将其赋值给parameterObj
                            Object parameterObj = parameterValue;
                            //获取这个参数的具体的类型名称
                            String typeName = parameter.getType().getName();
                            //如果是Integer类型就强转
                            if(parameterValue!=null){
                                if ("java.lang.Integer".equals(typeName)) {
                                    parameterObj = Integer.parseInt(parameterValue);
                                }
                            }
                            //最后赋值
                            parametersValue[i] = parameterObj;
                        }
                    }
                    //调用方法
                    method.setAccessible(true);
                    Object returnObj = method.invoke(controllerBeanObj, parametersValue);
                    //最后进行视图处理
                    String methodReturnString = (String) returnObj;
                    if (methodReturnString.startsWith("redirect:")) {
                        String redirectName = methodReturnString.substring("redirect:".length());
                        //重定向,比如：  redirect:fruit.do
                        response.sendRedirect(redirectName);
                    } else {
                        //转发,比如：  "edit"
                        super.processTemplate(methodReturnString, request, response);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
