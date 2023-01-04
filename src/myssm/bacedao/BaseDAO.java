package myssm.bacedao;


import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T> {

    protected Connection conn;
    protected PreparedStatement ps;
    protected ResultSet rs;

    //T的Class对象
    private Class<T> entityClass;

    //构造器,在构造器中可以获取T的真实类型并给entityClass赋值
    @SuppressWarnings("unchecked")
    public BaseDAO() {
        //这个构造器是子类调用的,即FruitDAOImpl,创造的是FruitDAOImpl的实例
        //那么子类构造方法内部首先会调用父类（BaseDAO）的无参构造方法
        //因此此处的getClass()会被执行，但是getClass获取的是FruitDAOImpl的Class
        //所以getGenericSuperclass()获取到的是BaseDAO的Class
        Type genericSuperclass = getClass().getGenericSuperclass();//获取父类的泛型
        //ParameterizedType 参数化类型
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        //取得第一个参数就是真实的T
        Type actualTypeArgument = actualTypeArguments[0];
        //利用反射赋值
        try {
            entityClass = (Class<T>) Class.forName(actualTypeArgument.getTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //返回连接
    protected Connection getConn() {
        try {
            return ConnUtil.getConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //设置参数
    private void setParams(PreparedStatement psmt, Object... params) {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                try {
                    psmt.setObject(i + 1, params[i]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //执行更新操作,返回被影响的行数
    protected int executeUpdate(String sql, Object... params) {
        try {
            boolean insertFlag = sql.trim().toUpperCase().startsWith("INSERT");
            conn = getConn();
            if (insertFlag) {
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                ps = conn.prepareStatement(sql);
            }
            setParams(ps, params);
            int count = ps.executeUpdate();
            if (insertFlag) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return ((Long) rs.getLong(1)).intValue();
                }
            } else {
                return count;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //返回一个结果集
    protected Object[] executeComplexQuery(String sql, Object... params) {
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            setParams(ps, params);
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            if (rs.next()) {
                Object[] columnValueArr = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    columnValueArr[i] = columnValue;
                }
                return columnValueArr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取一个实体对象
    protected T load(String sql, Object... params) {
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            setParams(ps, params);
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            if (rs.next()) {
                T entity = entityClass.getDeclaredConstructor().newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = rsmd.getColumnLabel(i + 1);
                    Object columnValue = rs.getObject(i + 1);
                    Field field = entityClass.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(entity, columnValue);
                }
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //返回一个对象集
    protected List<T> executeQuery(String sql, Object... params) {
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            setParams(ps, params);
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<T> lists = new ArrayList<>();
            while (rs.next()) {
                T entity = entityClass.getDeclaredConstructor().newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = metaData.getColumnName(i + 1);
                    Object columnValue = rs.getObject(i + 1);
                    Field field = entityClass.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(entity, columnValue);
                }
                lists.add(entity);
            }
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}