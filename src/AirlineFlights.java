import java.util.*;
import java.sql.*;
import java.sql.Date;
import java.io.*;

public class AirlineFlights {
	static Statement stmt;
	static Connection m_con;
	private String flightSel;

	static User user;
	static String u_email;
	static String u_name;
	static String u_country;
	
	static String u_src = ""; //TODO
	static String u_dst = ""; //TODO
	static Date u_depDate = null; //TODO
	static String u_sortBy = ""; //TODO
	static String u_sortOrder = ""; //TODO
	
	String u_seat; //TODO
	int u_tno;	//TODO: Generate random ticket number not in db
	String u_fare; //TODO
	float u_price;	//TODO
	
	public static ResultSet searchFlights() throws SQLException{


		String availFlights = 
				"create view available_flights(flightno,dep_date, src,dst,dep_time,arr_time,fare,seats, price) as " +
						"select f.flightno, sf.dep_date, f.src, f.dst, "+
						"f.dep_time+(trunc(sf.dep_date)-trunc(f.dep_time))," +
						"f.dep_time+(trunc(sf.dep_date)-trunc(f.dep_time))+(f.est_dur/60+a2.tzone-a1.tzone)/24, "+ 
						"fa.fare, fa.limit-count(tno), fa.price" +
						"from flights f, flight_fares fa, sch_flights sf, bookings b, airports a1, airports a2 "+
						"where f.flightno=sf.flightno and f.flightno=fa.flightno and f.src=a1.acode and "+
						"f.dst=a2.acode and fa.flightno=b.flightno(+) and fa.fare=b.fare(+) and "+
						"sf.dep_date=b.dep_date(+) "+
						"group by f.flightno, sf.dep_date, f.src, f.dst, f.dep_time, f.est_dur,a2.tzone,"+
						"a1.tzone, fa.fare, fa.limit, fa.price "+
						"having fa.limit-count(tno) > 0";
		String goodConnects = 
				"create view good_connections (src,dst,dep_date,flightno1,flightno2, layover,price) as "+
						"select a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, a2.dep_time-a1.arr_time, "+
						"min(a1.price+a2.price) "+
						"from available_flights a1, available_flights a2 "+
						"where a1.dst=a2.src and a1.arr_time +1.5/24 <=a2.dep_time and a1.arr_time +5/24 >=a2.dep_time "+
						"group by a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, a2.dep_time, a1.arr_time";
		String searchRS = 
				"select flightno1, flightno2, layover, price "+
						"from ("+
						"select flightno1, flightno2, layover, price, row_number() over "+
						"(order by "+u_sortBy+" "+u_sortOrder+") rn "+
						"from "+
						"(select flightno1, flightno2, layover, price "+
						"from good_connections "+
						"where to_char(dep_date,'"+u_depDate+"')='22/12/2015' "+
						"and src='"+u_src+"' and dst='"+u_dst+"' "+
						"union "+
						"select flightno flightno1, '' flightno2, 0 layover, price "+
						"from available_flights "+
						"where to_char(dep_date,'DD/MM/YYYY')='22/12/2015' and src='YEG' and dst='LAX')) "+
						"where rn <=5";


		stmt.executeUpdate("drop table available_flights");
		stmt.executeUpdate("drop table good_connections");
		stmt.executeUpdate(availFlights);
		stmt.executeUpdate(goodConnects);
		ResultSet rs = stmt.executeQuery(searchRS);
		// currently uses default sort criteria by price, how to sort by number of flights?
		stmt.executeUpdate("drop table available_flights");
		stmt.executeUpdate("drop table good_connections");
		// print out the thing
		return rs;
	}
	public void makeBooking(String f) throws SQLException {
		flightSel = f;
		
		updatePassengers();
		updateBookingsTickets();

	}
	private void updatePassengers() throws SQLException{
		// check name in passenger table

		String psgTable = 
				"select name, email, country "+
						"from passengers ";
		ResultSet p_rs = stmt.executeQuery(psgTable);

		// Look for passenger's name in passengers table. Add if not exist
		boolean nameFound = false;
		while (p_rs.next()) {
			if (p_rs.getString("name").matches(u_name)) {
				nameFound = true;
				break;
			}
		}
		if (!nameFound) {
			p_rs.moveToInsertRow();
			p_rs.updateString("name",u_name);
			p_rs.updateString("email", user.getEmail());
			p_rs.updateString("country", u_country);
			// do we need to check passenger table?
			p_rs.updateRow();
		}
	}
	private void updateBookingsTickets() throws SQLException {

		String bTable = 
				"select tno, flightno, fare, dep_date, seat "+ 
						"from bookings";
		String tTable = 
				"select tno, name, email, paid_price "+
						"from tickets";

		ResultSet bookings_rs = stmt.executeQuery(bTable);
		ResultSet tickets_rs = stmt.executeQuery(tTable);
		m_con.setAutoCommit(false);	// start transaction block
		tickets_rs.moveToInsertRow();
		tickets_rs.updateInt("tno", u_tno);
		tickets_rs.updateString("name", u_name);
		tickets_rs.updateString("email", user.getEmail());
		tickets_rs.updateFloat("paid_price", u_price);
		tickets_rs.insertRow();
		bookings_rs.updateInt("tno", u_tno);
		bookings_rs.updateString("flightno", flightSel);
		bookings_rs.updateString("fare", u_fare);
		bookings_rs.updateDate("dep_date", u_depDate);
		bookings_rs.updateString("seat", u_seat);
		bookings_rs.insertRow();
		// check
		m_con.commit();

	}
	public static ResultSet listBookings() throws SQLException {
		String bookingsT =
				"select b.tno, t.name, b.dep_date, t.paid_price "+
						"from bookings b, tickets t "+
						"where b.tno=t.tno";
		ResultSet rs = stmt.executeQuery(bookingsT);
		// print out the thing
		return rs;
	}
	public static void main(String args[]) {
		String m_driverName = "oracle.jdbc.driver.OracleDriver";

		try {
			Class drvClass = Class.forName(m_driverName);
		} catch(Exception e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}

		String m_url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";

		// get oracle username
		System.out.print("Username: ");
		Console co = System.console();
		String m_userName = co.readLine();

		// obtain password
		char[] passwordArray = co.readPassword("Password: ");
		String m_password = new String(passwordArray);

		try {
			// Establish connection
			m_con = DriverManager.getConnection(m_url, m_userName, m_password);
			stmt = m_con.createStatement(
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			/****** query stuff goes here *******/
			String email;
			user = new User(stmt, m_con, u_email); //TODO: login and get user info

			
			
			/******************/
			stmt.close();
			m_con.close();
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
	}
}