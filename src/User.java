import java.util.*;
import java.sql.*;
import java.io.*;

public class User{
	private static Statement stmt;
	private static Connection m_con;

	private String u_email; //TODO

	public User(String email) {
		this.stmt = AirlineSystem.stmt;
		u_email = email;
	}

	public static boolean isUser(String m_email) throws SQLException {

		String userT = 
				"select email "+
						"from users "+
						"where email='"+m_email+"'";
		ResultSet rs = stmt.executeQuery(userT);
		if(rs.next()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkPassword(String m_email, String pw) throws SQLException {
		String userT = 
				"select email, pass "+
						"from users "+
						"where email='"+m_email+
						"' and pass='"+pw+"'";
		ResultSet rs = stmt.executeQuery(userT);
		return rs.next();
	}
	
	public void createNewPassenger(String name, String country) throws SQLException {
		String passenger =
				"insert into passengers values (" + u_email + "," + name + "," + country + ")";
		stmt.executeUpdate(passenger);

	}
	
	public void createNewUser(String pass) throws SQLException{
		//check password
		String newuser =
				"insert into users values ('" + u_email + "','" + pass + "',null)";

		stmt.executeUpdate(newuser);
	}
	
	public String getEmail() {
		return u_email;
	}
}
