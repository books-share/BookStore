package edu.zjut.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author john
 * @date 2017年8月21日
 * @version v1.0
 * 
 */

public class JDBCUtils {
	
	private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
	
	public static Connection getConnection() throws Exception {
		Connection conn = threadLocal.get();
		if (conn == null) {
			Properties prop = new Properties();
			prop.load(JDBCUtils.class.getResourceAsStream("/jdbc.properties"));
			Class.forName(prop.getProperty("driver"));
			conn = DriverManager.getConnection(prop.getProperty("url"),prop);
			threadLocal.set(conn);
		}
		return conn;
	}

	
	public static void free (ResultSet rs, Statement st, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				rs = null;
			}
		}
		if (st != null){
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				st = null;
			}
		}
		if (conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				threadLocal.remove();
			}
		}
	}
	
	public static boolean update(String sql, Object...params) throws Exception {
		Connection conn = getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		if(params != null){
			int index = 1;
			for(Object obj : params){
				ps.setObject(index++, obj);
			}
		}
		int count = ps.executeUpdate();
		free(null, ps, null);
		return count > 0 ? true : false;
	}
	
	public static <T> boolean insert(String sql, T param) throws Exception {
		Connection conn = getConnection();
		String reg = "[:](\\w{1,})";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(sql);
		List<String> list = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			list.add(m.group(1));
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		for(int i = 0; i < list.size(); i++){
			String name = list.get(i);
			Field[] fields = param.getClass().getDeclaredFields();
			for(Field field : fields){
				field.setAccessible(true);
				if(name.equals(field.getName())){
					ps.setObject(i + 1, field.get(param));
				}
			}
		}
		int count = 0;
		count = ps.executeUpdate();
		free(null, ps, null);
		return count > 0 ? true : false;
	}
	
	/**
	 * 
	 * @param param
	 * @param sql // update s_emp set salary=:salary,last_name=:name where id=:id
	 * @return
	 * @throws Exception
	 */
	public static <T> boolean update (T param, String sql) throws FileNotFoundException, IOException, Exception{
		Connection conn = getConnection();
		String reg = "[:](\\w{1,})";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(sql);
		List<String> list = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			list.add(m.group(1));
			m.appendReplacement(sb, "?");
		}
		m.appendTail(sb);

		PreparedStatement ps = conn.prepareStatement(sb.toString());
		for(int i = 0; i < list.size(); i++){
			String name = list.get(i);
			Field[] fields = param.getClass().getDeclaredFields();
			for(Field field : fields){
				field.setAccessible(true);
				if(name.equals(field.getName())){
					ps.setObject(i + 1, field.get(param));
				}
			}
		}
		int count = 0;
		count = ps.executeUpdate();
		free(null, ps, null);
		return count > 0 ? true : false;
	
	}
	
	public static <T> List<T> queryForList(String sql, Class<T> clazz, Object...params) throws Exception {
		Connection conn = JDBCUtils.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		if(params != null){
			int index = 1;
			for(Object obj : params){
				ps.setObject(index++, obj);
			}
		}
		List<T> list = new ArrayList<T>();
		ResultSet rs = ps.executeQuery();
		System.out.println("dfsdf");
		while (rs.next()){
			T t = clazz.newInstance();
			ResultSetMetaData data = rs.getMetaData();
			int count = data.getColumnCount();
			for (int i = 1; i <= count; i++){
				String columnName = data.getColumnName(i);
				int columnType = data.getColumnType(i);
				Field[] fs = clazz.getDeclaredFields();
				for (Field f : fs) {
					f.setAccessible(true);
					if (columnName.equalsIgnoreCase(f.getName())) {
						Object value = rs.getObject(columnName);
						f.set(t,convert(value, f.getType(), columnType));
					}
				}
			}
			list.add(t);
		}

		free(rs, ps, null);
		return list;
	}
	
	public static <T> T queryForObject(String sql, Class<T> clazz, Object... params) throws Exception {
		Connection conn = JDBCUtils.getConnection();
//		System.out.println("in jdbc:" + sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		if (params != null) {
			int index = 1;
			for (Object obj : params) {
				ps.setObject(index++, obj);
			}
		}
		ResultSet rs = ps.executeQuery();
		// 创建T类型的对象
		T t = null;
		if (rs.next()) {
			t = clazz.newInstance();
			// 获取一行数据对象
			ResultSetMetaData data = rs.getMetaData();
			// 获取一行数据的列数
			int count = data.getColumnCount();
			for (int i = 1; i <= count; i++) {
				// 获取列名
				String columnName = data.getColumnName(i);
				// 获取列类型
				int columnType = data.getColumnType(i);
				// 获取T类型中所有的属性
				Field[] fs = clazz.getDeclaredFields();
				for (Field f : fs) {
					// 设置属性可被访问
					f.setAccessible(true);
					// 如果列名和属性名字一直，要将列值设置为属性值
					if (columnName.equalsIgnoreCase(f.getName())) {
						Object value = rs.getObject(columnName);
						// 设置t对象属性
						System.out.println("in:" + value);
						f.set(t, convert(value, f.getType(), columnType));
					}
				}
			}
		}
		free(rs, ps, null);
		return t;
	}

	public static int queryForInt(String sql, Object... params) throws Exception {
		Connection conn = JDBCUtils.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		int count = 0;
		// 设置参数
		if (params != null) {
			int index = 1;
			for (Object obj : params) {
				ps.setObject(index++, obj);
			}
		}
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			count = rs.getInt(1);
		}
		free(rs, ps, null);
		return count;
	}
	
	private static Object convert(Object val, Class<?> type, int columnType) {

		if (val == null)
			return val;
		if ((type == Byte.class || type == byte.class) && (columnType == Types.NUMERIC || columnType == Types.BIT)) {
			return Byte.parseByte(String.valueOf(val));
		}
		if ((type == Short.class || type == short.class) && (columnType == Types.NUMERIC)) {
			return Short.parseShort(String.valueOf(val));
		}
		if ((type == Integer.class || type == int.class)
				&& (columnType == Types.NUMERIC || columnType == Types.INTEGER)) {
			return Integer.parseInt(String.valueOf(val));
		}
		if ((type == Long.class || type == long.class)
				&& (Types.INTEGER == columnType || Types.NUMERIC == columnType)) {
			return Long.parseLong(String.valueOf(val));
		}
		if ((type == Float.class || type == float.class)
				&& (Types.FLOAT == columnType || Types.NUMERIC == columnType)) {
			return Float.parseFloat(String.valueOf(val));
		}
		if ((type == Double.class || type == double.class)
				&& (Types.DOUBLE == columnType || Types.NUMERIC == columnType)) {
			return Double.parseDouble(String.valueOf(val));
		}
		if ((type == String.class) && (Types.VARCHAR == columnType || Types.CHAR == columnType)) {
			return String.valueOf(val);
		}

		if ((type == Character.class || type == char.class)
				&& (Types.VARCHAR == columnType || Types.CHAR == columnType)) {
			return String.valueOf(val).charAt(0);
		}

		if ((type == java.util.Date.class) && (Types.DATE == columnType || Types.TIMESTAMP == columnType)) {
			return val;
		}
		throw new RuntimeException("请检查数据库字段类型和返回的类型是否一致！！！");
	}
	
}


