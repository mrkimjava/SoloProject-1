package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTemplete {
	
	public static Connection getConnection() {
		
		Connection con = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("your db");
			
		} catch (ClassNotFoundException e) {
			System.out.println("Driver laod error");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Database connection error");
			e.printStackTrace();
		}
		
		return con;
	}
	
	public static void close(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("[Error] Connection close error.");
			e.printStackTrace();
		}
	}
	
	public static void close(Statement stmt) {
		try {
			stmt.close();
		} catch (SQLException e) {
			System.out.println("[Error] Statement close error.");
			e.printStackTrace();
		}
	}
	
	public static void close(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException e) {
			System.out.println("[Error] ResultSet close error.");
			e.printStackTrace();
		}
	}
	
	public static void closeAll(Connection con, Statement stmt, ResultSet rs) {
		if(con != null)	close(con);
		if(stmt != null) close(stmt);
		if(rs != null) close(rs);
	}
	
	public static void commit(Connection con) {
		try {
			con.commit();
		} catch (SQLException e) {
			System.out.println("commit error.");
			e.printStackTrace();
		}
	}
	
	public static void rollback(Connection con) {
		try {
			con.rollback();
		} catch (SQLException e) {
			System.out.println("rollback error.");
			e.printStackTrace();
		}
	}
}









