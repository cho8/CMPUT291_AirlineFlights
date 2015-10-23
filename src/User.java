import java.util.*;
import java.sql.*;
import java.io.*;

public class User{
	private static Statement stmt;
	private static Connection m_con;

	private String u_email; //TODO

	public User(Statement s, Connection c, String email) {
		stmt = s;
		m_con = c;
		u_email = email;
	}
	public boolean isUser() throws SQLException {
		String userT = 
				"select email "+
						"from users "+
						"where email="+u_email;
		ResultSet rs = stmt.executeQuery(userT);
		if(rs.next()) {
			return true;
		} else {
			return false;
		}
	}
	public boolean checkPassword(String pw) throws SQLException {
		String userT = 
				"select email, password "+
						"from users "+
						"where email="+u_email+
						" and password="+pw;
		ResultSet rs = stmt.executeQuery(userT);
		return rs.next();
	}
	
	public void createNewUser(String name, String country) throws SQLException {
		String userT =
				"select email, password, last_login "+
						"from users ";
		ResultSet rs = stmt.executeQuery(userT);
		rs.moveToInsertRow();
		rs.updateString("name", name);
		rs.updateString("country", country);
		rs.updateDate("date", null);
		rs.insertRow();
	}
	
	public String getEmail() {
		return u_email;
	}
}
