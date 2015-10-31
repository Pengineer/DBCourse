package edu.hust.action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.hust.model.Student;

public class TableOperateAction extends ActionSupport{
	String operate;
	List<Student> students;

	public String select() throws Exception {
		if(!operate.trim().startsWith("select") || operate == null || operate.trim().equals("")) {
			return INPUT;
		}
		Connection conn1 = null;
		Connection conn2 = this.getConnection(3307, "root", "123456");
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String securityLevel = (String) ActionContext.getContext().getSession().get("securityLevel");
		students = new ArrayList<Student>();
		final int sort = getSortValue(operate);;//0：不排序；-1：降序；1：升序
		final String sortColumn = getSortColumn(operate);
		if(securityLevel.equals("高级")) {//高安全级用户
			conn1 = this.getConnection(3306, "root", "123456");
			rs1 = this.query(conn1, operate);
			rs2 = this.query(conn2, operate);
			while(rs1.next()) { //先得到高安全级数据库实例的数据
				Student stu = new Student();
				stu.setStudentid(rs1.getString(1));
				stu.setName(rs1.getString(2));
				stu.setClassName(rs1.getString(3));
				students.add(stu);
			}
			while(rs2.next()) { //再得到低安全级数据库实例的数据，并与高安全级数据库实例的数据合并
				Boolean flag = true;//低安全级数据库特有
				String studentId = rs2.getString(1);
				String name = rs2.getString(2);
				String className = rs2.getString(3);
				int isDelete = rs2.getInt(4);
				for(Student student : students) {
					if (student.getStudentid().equals(studentId)) {
						flag = false;
						if(student.getName() == null) {
							student.setName(name);
						}
						if(student.getClassName() == null) {
							student.setClassName(className);
						}
					}
				}
				if(flag && isDelete == 0) {
					Student stu = new Student();
					stu.setStudentid(studentId);
					stu.setName(name);
					stu.setClassName(className);
					students.add(stu);
				}
			}
		} else {//普通用户
			rs2 = this.query(conn2, operate);
			while(rs2.next()) {
				Student stu = new Student();
				stu.setStudentid(rs2.getString(1));
				stu.setName(rs2.getString(2));
				stu.setClassName(rs2.getString(3));
				students.add(stu);
			}
		}
		
		Collections.sort(students, new Comparator<Student>() {
            public int compare(Student stu1, Student stu2) {
            	if ("c_studentId".equals(sortColumn)) {
            		if(stu1.getStudentid() == null || stu2.getStudentid() == null) {
            			return 1;
            		}
            		return stu1.getStudentid().compareTo(stu2.getStudentid()) * sort;
            	} else if ("c_name".equals(sortColumn)) {
            		if(stu1.getName() == null || stu2.getName() == null) {
            			return 1;
            		}
            		return stu2.getName().compareTo(stu2.getName()) * sort;
            	} else if ("c_class".equals(sortColumn)) {
            		if(stu1.getClassName() == null || stu2.getClassName() == null) {
            			return 1;
            		}
            		return stu2.getClassName().compareTo(stu2.getClassName()) * sort;
            	}
                return 0;
            }
        });

		if(rs1 != null) {
			rs1.close();
		}
		if(rs2 != null) {
			rs2.close();
		}
		if(conn1 != null) {
			conn1.close();
		}
		if(conn2 != null) {
			conn2.close();
		}
		return SUCCESS;
	}
	
	//insert into t_student values('','','')
	public String add() throws Exception {
		if(!operate.trim().startsWith("insert") || operate == null || operate.trim().equals("")) {
			return INPUT;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		String securityLevel = (String) ActionContext.getContext().getSession().get("securityLevel");
		if(securityLevel.equals("高级")) {//高安全级用户
			conn = this.getConnection(3306, "root", "123456");
			ps = conn.prepareStatement(operate);
			ps.executeUpdate();
		}  else {//低安全级用户
			conn = this.getConnection(3307, "root", "123456");
			//operate:insert into t_student values(1,1,1);
			operate = operate.replaceAll("\\)", ",0)");
			ps = conn.prepareStatement(operate);
			ps.executeUpdate();
		}
		if(conn != null) {
			conn.close();
		}
		
		return SUCCESS;
	}
	
	public String modify() throws Exception {
		if(!operate.trim().startsWith("update") || operate == null || operate.trim().equals("")) {
			return INPUT;
		}
		Connection conn1 = this.getConnection(3306, "root", "123456");
		Connection conn2 = this.getConnection(3307, "root", "123456");
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		//operate：update t_user set c_class="1" where c_studentId = "xxx";
		String tmpSQL = "select * from t_student" + operate.replaceAll("^.*(where.*)$", " $1");
		ResultSet rs1 = this.query(conn1, tmpSQL);
		ResultSet rs2 = this.query(conn2, tmpSQL);
		String securityLevel = (String) ActionContext.getContext().getSession().get("securityLevel");
		if(securityLevel.equals("高级")) {//高安全级用户
			//判断该数据是否是低安全级数据
			if (!rs1.next() && rs2.next()) {//是：高安全级数据库没有，低安全级数据库有
				//1,对低安全级的数据不做修改，在高安全级实例中增加一条数据，只记录数据的studentId和被修改字段，没修改的字段不变
				String studentId = rs2.getString(1);
				String name = rs2.getString(2);
				String className = rs2.getString(3);
				String addSQL="";
				if (operate.replaceAll("^.*( set .*) where.*$", "$1").trim().contains("c_class")) {
					addSQL = "insert into t_student values(\"" + studentId + "\"," + null + ",\"" + className +"\")";
				} else {
					addSQL = "insert into t_student values(\"" + studentId + "\",\"" + name + "\"," + null +")";
				}
				ps1 = conn1.prepareStatement(addSQL);
				ps1.executeUpdate();
				ps2 = conn1.prepareStatement(operate);
				ps2.executeUpdate();
			} else {//否，直接修改
				ps1 = conn1.prepareStatement(operate);
				ps1.executeUpdate();
			}
		}  else {//低安全级用户
			if (rs1.next() && rs2.next()) {//高级实例有该数据，那么判断是否存在引用，如果存在而且修改的是引用部分，则需要先将引用部分备份到高级数据库，再修改
				String studentId = rs1.getString(1);
				String name1 = rs1.getString(2);
				String className1 = rs1.getString(3);
				String name2 = rs2.getString(2);
				String className2 = rs2.getString(3);
				String addSQL="";
				if (name1 == null && name2 != null && operate.replaceAll("^.*( set .*) where.*$", "$1").trim().contains("c_name")) { //修改的是被引用的name
					addSQL = "update t_student set c_name = \'" + name2 + "\' " + " where c_studentId=\'" + studentId + "\'";
				} else if (className1 == null && className2 != null && operate.replaceAll("^.*( set .*) where.*$", "$1").trim().contains("c_class")) { //修改的是被引用的class
					addSQL = "update t_student set c_class = \'" + className2 + "\' " + " where c_studentId=\'" + studentId + "\'";
				}
				ps1 = conn1.prepareStatement(addSQL);
				ps1.executeUpdate();
			} 
			//高安全级数据库没有，低安全级数据库有，数据完全属于低级实例，则直接修改  
			ps2 = conn2.prepareStatement(operate);
			ps2.executeUpdate();
		}
		if(rs1 != null) {
			rs1.close();
		}
		if(rs2 != null) {
			rs2.close();
		}
		if(conn1 != null) {
			conn1.close();
		}
		if(conn2 != null) {
			conn2.close();
		}
		return SUCCESS;
	}
	
	//operate：delete from t_student where c_studentId='01'
	public String delete() throws Exception{
		if(!operate.trim().startsWith("delete") || operate == null || operate.trim().equals("")) {
			return INPUT;
		}
		//需要在低安全级的数据库表中增加一个字段，标识该用户被高安全级用户删除，高安全用户看不到，但是低安全级的用户可以看到
		Connection conn1 = this.getConnection(3306, "root", "123456");
		Connection conn2 = this.getConnection(3307, "root", "123456");
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String securityLevel = (String) ActionContext.getContext().getSession().get("securityLevel");
		if(securityLevel.equals("高级")) {//高安全级用户
			//判断该数据是否是低安全级数据
			String tmpSQL1 = "select * from t_student" + operate.replaceAll("^.*(where.*)$", " $1");
			rs2 = this.query(conn2, tmpSQL1);
			if(rs2.next()) {//低安全级有该数据：标记即可
				String tmpSQL2 = "update t_student set c_flag=1" + operate.replaceAll("^.*(where.*)$", " $1");
				ps2 = conn2.prepareStatement(tmpSQL2);
				ps2.executeUpdate();
			}
			ps1 = conn1.prepareStatement(operate);
			ps1.executeUpdate();
		} else {//普通用户
			//判断高安全级实例是否引用了该数据部分内容
			String tmpSQL1 = "select * from t_student" + operate.replaceAll("^.*(where.*)$", " $1");
			rs1 = this.query(conn1, tmpSQL1);
			rs2 = this.query(conn2, tmpSQL1);
			if(rs1.next() && rs2.next()) { //是：将高安全级实例数据根据低安全级实例数据补全
				String studentId = rs1.getString(1);
				String name1 = rs1.getString(2);
				String className1 = rs1.getString(3);
				String name2 = rs2.getString(2);
				String className2 = rs2.getString(3);
				if(name1 == null) {
					String tmpSQL2 = "update t_student set c_name=\'" + name2 + "\' where c_studentId=\'" + studentId + "\'";
					conn1.prepareStatement(tmpSQL2).executeUpdate();
				}
				if(className1 == null) {
					String tmpSQL2 = "update t_student set c_class=\'" + className2 + "\' where c_studentId=\'" + studentId + "\'";
					conn1.prepareStatement(tmpSQL2).executeUpdate();
				}
			} 
			ps2 = conn2.prepareStatement(operate);
			ps2.executeUpdate();
		}
		if(rs1 != null) {
			rs1.close();
		}
		if(rs2 != null) {
			rs2.close();
		}
		if(conn1 != null) {
			conn1.close();
		}
		if(conn2 != null) {
			conn2.close();
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
		} catch (SQLException e) {
			System.out.println("查询失败！");
		} 
		return rs;
	}
	
	public int getSortValue(String sql) {
		if(sql.contains("order") && sql.contains("by")){
			if(sql.contains("desc") || sql.contains("DESC")) {
				return -1;
			}
			if(sql.contains("asc") || sql.contains("ASC")) {
				return 1;
			}
		}
		return 1;
	}
	
	public String getSortColumn(String sql) {
		if(sql.contains("order") && sql.contains("by")) {
			String column = sql.replaceAll("\\s+", "").substring(sql.indexOf("order"));
			if(column.contains("c_studentId")) {
				return "c_studentId";
			} else if(column.contains("c_name")) {
				return "c_name";
			} else if(column.contains("c_class")) {
				return "c_class";
			}
		}
		return "";
	}
	
	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}
	
	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}
	
}
