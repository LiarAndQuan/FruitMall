package service;

import dao.FruitDAO;
import dao.FruitDAOImpl;
import pojo.Fruit;

import java.util.List;

public class FruitServiceImpl implements FruitService {

    FruitDAO fruitDAO = new FruitDAOImpl();

    //根据关键字获取水果列表
    @Override
    public List<Fruit> getFruitList(String keyword, Integer pageNo) {
        return fruitDAO.getFruitList(keyword, pageNo);
    }

    //添加水果
    @Override
    public void addFruit(Fruit fruit) {
        fruitDAO.addFruit(fruit);
    }

    //根据id获取值
    @Override
    public Fruit getFruitByFid(Integer fid) {
        return fruitDAO.getFruitByFid(fid);
    }

    //删除
    @Override
    public void delFruit(Integer fid) {
        fruitDAO.delFruit(fid);
    }

    //获得总页数
    @Override
    public Integer getPageCount(String keyword) {
        int count = fruitDAO.getFruitCount(keyword);
        int recordCount = FruitDAOImpl.RECORD_COUNT;
        return (count + recordCount - 1) / recordCount;
    }

    //更新
    @Override
    public void updateFruit(Fruit fruit) {
        fruitDAO.updateFruit(fruit);
    }
}
