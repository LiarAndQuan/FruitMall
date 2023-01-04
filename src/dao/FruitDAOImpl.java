package dao;

import myssm.bacedao.BaseDAO;
import pojo.Fruit;

import java.util.List;

public class FruitDAOImpl extends BaseDAO<Fruit> implements FruitDAO {

    public static final int RECORD_COUNT = 5;

    @Override
    public List<Fruit> getFruitList(String keyword, Integer pageNo) {
        String sql = "select id,name,inventory,price from fruitlists where name like ? limit ?,?";
        return super.executeQuery(sql, "%" + keyword + "%", (pageNo - 1) * RECORD_COUNT, RECORD_COUNT);
    }

    @Override
    //根据id查询对象
    public Fruit getFruitByFid(Integer fid) {
        String sql = "select id,name,inventory,price from fruitlists where id = ? ";
        return super.load(sql, fid);
    }

    @Override
    //更新
    public void updateFruit(Fruit fruit) {
        String sql = "update fruitlists set name = ?,inventory = ?,price = ? where id = ?";
        super.executeUpdate(sql, fruit.getName(), fruit.getInventory(), fruit.getPrice(), fruit.getId());
    }

    @Override
    //根据id删除
    public void delFruit(Integer fid) {
        String sql = "delete from fruitlists where id = ?";
        super.executeUpdate(sql, fid);
    }

    @Override
    //添加
    public void addFruit(Fruit fruit) {
        String sql = "insert into fruitlists(name,inventory,price) values(?,?,?)";
        super.executeUpdate(sql, fruit.getName(), fruit.getInventory(), fruit.getPrice());
    }

    @Override
    public Integer getFruitCount(String keyword) {
        String sql = "select count(*) from fruitlists where name like ?";
        return ((Long) super.executeComplexQuery(sql, "%" + keyword + "%")[0]).intValue();
    }
}
