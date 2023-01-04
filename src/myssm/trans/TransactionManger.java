package myssm.trans;

import myssm.bacedao.ConnUtil;

import java.sql.Connection;

public class TransactionManger {
    public static void beginTransaction() {
        try {
            ConnUtil.getConn().setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void commit() {
        try{
            Connection conn =ConnUtil.getConn();
            conn.commit();
            ConnUtil.closeConn();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void rollback() {
        try{
            Connection conn = ConnUtil.getConn();
            conn.rollback();
            ConnUtil.closeConn();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
