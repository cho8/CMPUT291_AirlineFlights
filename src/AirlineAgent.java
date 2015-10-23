import java.sql.Connection;
import java.sql.Statement;

public class AirlineAgent extends User {
	private Statement stmt;
	private Connection m_con;
	public AirlineAgent(String email) {
		super(email);
		// TODO Auto-generated constructor stub
	}

	public void getConnection(Statement s, Connection c) {
		stmt = s;
		m_con = c;
	}
	
	public void recordDep() {
		
	}
	public void recordArr() {
		
	}

}
