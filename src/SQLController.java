import java.sql.*;
import java.util.*;

public class SQLController {
	
	private static Connection con;
	
	
	public static void makeConnection(String user,String pass) throws SQLException{
		
		try{

			String m_driverName = "oracle.jdbc.driver.OracleDrive";
			Class drvClass = Class.forName(m_driverName);
		}catch(ClassNotFoundException e){
			System.out.println("Could not load driver");
		}

		String url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";


		con = DriverManager.getConnection(url,user,pass);
		
		}
	
}
