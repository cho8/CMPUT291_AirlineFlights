import java.sql.*;

public class User{
	private static Statement stmt;

	private String u_email; //TODO
	private static boolean isAgent = false;

	public User(String email) {
		this.stmt = AirlineSystem.stmt;
		u_email = email;
	}

	public String getEmail() {
		return u_email;
	}


	public static boolean isUser(String m_email) throws SQLException {
		String userQ = 
				"select email "+
						"from users "+
						"where email='"+m_email+"'";
		ResultSet rs = stmt.executeQuery(userQ);


		if(rs.next()) {
			isAgent = checkAgent();
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean checkAgent() throws SQLException {
		String agentQ = "select * from airline_agents where email='"+Main.currentuser.getEmail()+"'";
		ResultSet rs = stmt.executeQuery(agentQ);
		if (rs.next()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isAgent() {
		return isAgent;
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

	public void setLastLogin() throws SQLException{
		String lastLoginQ =
				"UPDATE users "+
						"SET last_login= to_date(sysdate, 'dd-mon-yyyy') "+
						"WHERE email= '"+u_email+"'";
		stmt.executeUpdate(lastLoginQ);
		

	}

}
