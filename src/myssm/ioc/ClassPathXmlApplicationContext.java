package myssm.ioc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ClassPathXmlApplicationContext implements BeanFactory {

    private final Map<String, Object> beanMap = new HashMap<>();
    
    private static final String path = "applicationContext.xml";

    public ClassPathXmlApplicationContext(){
        this(path);
    }

    public ClassPathXmlApplicationContext(String path) {
        try {
            //1.根据bean标签装载beanMap
            //获取输入流
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            //创建DocumentBuilderFactory
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
            //创建DocumentBuilder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            //创建Document
            Document document = documentBuilder.parse(is);
            //获取所有的bean标签,返回数组
            NodeList beanNodeList = document.getElementsByTagName("bean");
            //遍历bean标签
            for (int i = 0; i < beanNodeList.getLength(); i++) {
                //取到每一个bean标签beanNode
                Node beanNode = beanNodeList.item(i);
                //如果这个bean标签是Element类型的一个实例
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    //强转
                    Element element = (Element) beanNode;
                    //获取id
                    String beanId = element.getAttribute("id");
                    //获取配置的class名称
                    String className = element.getAttribute("class");
                    //根据名称获取Class
                    Class<?> beanClass = Class.forName(className);
                    //根据Class获取一个实例对象
                    Object beanObj = beanClass.getDeclaredConstructor().newInstance();
                    //将其封装在beanMap中
                    beanMap.put(beanId, beanObj);
                }
            }
            //2.根据property标签装载依赖关系
            //遍历bean标签
            for (int i = 0; i < beanNodeList.getLength(); i++) {
                //取出bean节点
                Node beanNode = beanNodeList.item(i);
                //如果是Element类型
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    //强转
                    Element beanElement = (Element) beanNode;
                    //获取bean中的id,为后面赋值做好准备
                    String beanId = beanElement.getAttribute("id");
                    //获取bean的所有的子节点
                    NodeList childNodes = beanElement.getChildNodes();
                    //遍历子节点
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        //获取每一个子节点
                        Node beanChildNode = childNodes.item(i);
                        //如果子节点的标签为property
                        if (beanChildNode.getNodeType() == Node.ELEMENT_NODE && "property".equals(beanChildNode.getNodeName())) {
                            //强转
                            Element propertyElement = (Element) beanChildNode;
                            //获取property中的name,即bean中的id对应的类中的属性名
                            String propertyId = propertyElement.getAttribute("name");
                            //获取property中的ref,即属性对应的类对应的bean标签中的id
                            String propertyRef = propertyElement.getAttribute("ref");
                            //根据bean中的id获取了对应的类
                            Object beanObject = beanMap.get(beanId);
                            //获取对应的类的Class对象
                            Class<?> beanClazz = beanObject.getClass();
                            //利用Class对象获取对应name的属性
                            Field field = beanClazz.getDeclaredField(propertyId);
                            //强制访问
                            field.setAccessible(true);
                            //利用反射赋值
                            field.set(beanObject, beanMap.get(propertyRef));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }
}
