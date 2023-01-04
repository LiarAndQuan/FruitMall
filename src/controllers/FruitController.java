package controllers;

import pojo.Fruit;
import service.FruitService;
import service.FruitServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class FruitController {

    FruitService fruitService = new FruitServiceImpl();

    private String update(Integer id, String name, Integer price, Integer inventory) {
        fruitService.updateFruit(new Fruit(id, name, inventory, price));
        //更新操作结束之后就重定向到index页面
        return "redirect:fruit.do";
    }

    private String edit(Integer id, HttpServletRequest request) {
        if (id != null) {
            Fruit fruit = fruitService.getFruitByFid(id);
            //为什么要保存作用域
            request.setAttribute("fruit", fruit);
            //为什么这里是请求转发??
            //因为我们在主页面点击了编辑,然后我们调用了这个方法,跳转到了edit.html,然后我们需要共享数据显示在edit.html
            return "edit";
        }
        return "error";
    }

    private String del(Integer id) {
        if (id != null) {
            fruitService.delFruit(id);
            //删除操作重定向到index页面
            return "redirect:fruit.do";
        }
        return "error";
    }

    private String add(String name, Integer price, Integer inventory) {
        Fruit fruit = new Fruit(name, inventory, price);
        fruitService.addFruit(fruit);
        //添加操作重定向到index页面
        return "redirect:fruit.do";
    }

    private String index(String oper, String keyword, Integer pageNo, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (pageNo == null) {
            pageNo = 1;
        }
        if ("search".equals(oper)) {//如果是按照关键字查询
            pageNo = 1;
            if (keyword == null) {
                keyword = "";
            }
            session.setAttribute("keyword", keyword);
        } else {//不是按照关键字查询
            Object keywordObj = session.getAttribute("keyword");
            if (keywordObj != null) {
                keyword = (String) keywordObj;
            } else {
                keyword = "";
            }
        }
        session.setAttribute("pageNO", pageNo);//将pageNo设置在session中
        List<Fruit> fruitList = fruitService.getFruitList(keyword, pageNo);//查询所有的水果列表
        
        session.setAttribute("fruitList", fruitList);//将水果列表设置在session中

        int pageCount = fruitService.getPageCount(keyword);//返回总页数
        session.setAttribute("pageCount", pageCount);//设置总页数
        
        return "index";
        //默认跳转到了index.html并且用了thymeleaf渲染,所有的重定向都调用了
    }
}
