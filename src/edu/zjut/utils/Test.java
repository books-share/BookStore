package edu.zjut.utils;

import java.sql.Connection;

public class Test {

    public static void main(String[] args) throws Exception {
        Connection c = JDBCUtils.getConnection();
        System.out.println(c);
    }
}
