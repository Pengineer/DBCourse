package edu.hust.action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport{
	String username;
	String password;
	int flag;
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String login(){
		flag=0;
		Connection conn = this.getConnection(3306, "root", "123456");
		ResultSet rs = this.query(conn, "select c_password,c_security_level from t_user where c_name = \'" + username + "\'");
		boolean hasNext = false;
		try {
			hasNext = rs.next();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if(!hasNext) { //用户不存在
			flag=1;
			return INPUT;
		} else {
			try {
				if(!rs.getString(1).equals(password)) { //密码错误
					flag=2;
					return INPUT;
				}
				ActionContext.getContext().getSession().put("securityLevel", rs.getString(2));
			} catch (SQLException e) {
				System.out.println("用户校验失败！");
				e.printStackTrace();
			} finally {
				try {
					rs.close();
					conn.close();
				} catch (SQLException e) {
					System.out.println("资源关闭失败");
				}
			}		
		}
		return SUCCESS;
	}

	public Connection getConnection(int instance, String user, String password) {
		String url = "jdbc:mysql://192.168.88.156:" + instance + "/test";
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			System.out.println("数据库连接获取失败！");
		}
		return con;
	} 
	
	public ResultSet query(Connection connection, String sql) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs == null) {
				return null;
			}
		} catch (SQLException e) {
			System.out.println("查询失败！");
			e.printStackTrace();
		} 
		return rs;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
