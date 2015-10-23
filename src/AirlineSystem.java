import java.util.*;
import java.sql.*;
import java.sql.Date;
import java.io.*;

public class AirlineSystem {
	static Statement stmt;
	static Connection m_con;
	static String m_url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
	String m_driverName = "oracle.jdbc.driver.OracleDriver";

	static User user;




	public void makeConnection(String m_userName, String m_password){

		try {
			Class drvClass = Class.forName(m_driverName); 
			// DriverManager.registerDriver((Driver)drvClass.newInstance());- not needed. 
			// This is automatically done by Class.forName().
		} catch(Exception e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		} 

		try{
			// Establish a connection
			m_con = DriverManager.getConnection(m_url, m_userName,
					m_password);
			// Changed to reflect changes made in the result set and to make these changes permanent to the database too

		}  catch(SQLException ex) {
			System.err.println("SQLException: " +
					ex.getMessage());
		}

		try {
			stmt = m_con.createStatement(
					ResultSet.TYPE_SCROLL_SENSITIVE, 
					ResultSet.CONCUR_UPDATABLE);
		} catch(SQLException er) {
			System.err.println("SecondException: "+ er.getMessage());
		}
	}

	public void stopConnection() throws SQLException {
		stmt.close();
		m_con.close();
	}

	public static ResultSet searchFlights(String u_src, String u_dst, String u_depDate, String u_sortBy, String u_sortOrder) throws SQLException{


		//		String availFlights = 
		//				"create view available_flights(flightno,dep_date, src,dst,dep_time,arr_time,fare,seats, price) as " +
		//						"select f.flightno, sf.dep_date, f.src, f.dst, "+
		//						"f.dep_time+(trunc(sf.dep_date)-trunc(f.dep_time))," +
		//						"f.dep_time+(trunc(sf.dep_date)-trunc(f.dep_time))+(f.est_dur/60+a2.tzone-a1.tzone)/24, "+ 
		//						"fa.fare, fa.limit-count(tno), fa.price" +
		//						"from flights f, flight_fares fa, sch_flights sf, bookings b, airports a1, airports a2 "+
		//						"where f.flightno=sf.flightno and f.flightno=fa.flightno and f.src=a1.acode and "+
		//						"f.dst=a2.acode and fa.flightno=b.flightno(+) and fa.fare=b.fare(+) and "+
		//						"sf.dep_date=b.dep_date(+) "+
		//						"group by f.flightno, sf.dep_date, f.src, f.dst, f.dep_time, f.est_dur,a2.tzone,"+
		//						"a1.tzone, fa.fare, fa.limit, fa.price "+
		//						"having fa.limit-count(tno) > 0";
		//		String goodConnects = 
		//				"create view good_connections (src,dst,dep_date,flightno1,flightno2, layover,price) as "+
		//						"select a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, a2.dep_time-a1.arr_time, "+
		//						"min(a1.price+a2.price) "+
		//						"from available_flights a1, available_flights a2 "+
		//						"where a1.dst=a2.src and a1.arr_time +1.5/24 <=a2.dep_time and a1.arr_time +5/24 >=a2.dep_time "+
		//						"group by a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, a2.dep_time, a1.arr_time";
		//		String searchRS = 
		//				"select flightno1, flightno2, layover, price "+
		//						"from ("+
		//						"select flightno1, flightno2, layover, price, row_number() over "+
		//						"(order by "+u_sortBy+" "+u_sortOrder+") rn "+
		//						"from "+
		//						"(select flightno1, flightno2, layover, price "+
		//						"from good_connections "+
		//						"where to_char(dep_date,'"+u_depDate+"')='22/12/2015' "+
		//						"and src='"+u_src+"' and dst='"+u_dst+"' "+
		//						"union "+
		//						"select flightno flightno1, '' flightno2, 0 layover, price "+
		//						"from available_flights "+
		//						"where to_char(dep_date,'DD/MM/YYYY')='22/12/2015' and src='YEG' and dst='LAX')) "+
		//						"where rn <=5";
		//
		//
		//		stmt.executeUpdate("drop table available_flights");
		//		stmt.executeUpdate("drop table good_connections");
		//		stmt.executeUpdate(availFlights);
		//		stmt.executeUpdate(goodConnects);
		//		ResultSet rs = stmt.executeQuery(searchRS);
		//		// currently uses default sort criteria by price, how to sort by number of flights?
		//		stmt.executeUpdate("drop table available_flights");
		//		stmt.executeUpdate("drop table good_connections");
		//		// print out the thing
		//		return rs;
		return null;
	}
	public static boolean checkPassengers(String u_name) throws SQLException {
		String psgTable = 
				"select name, email, country "+
						"from passengers "+
						"where name= '"+u_name+"'";
		ResultSet p_rs = stmt.executeQuery(psgTable);

		// Look for passenger's name in passengers table
		return p_rs.next();

	}
	public void updatePassengers(String u_name, String u_country) throws SQLException{
		// check name in passenger table

		String psgTable = 
				"select name, email, country "+
						"from passengers "+
						"where name= '"+u_name+"' ";
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
			p_rs.updateRow();
		}
	}
	private static int generateTix() throws SQLException {
		List<Integer> tixList = new ArrayList<Integer>();
		Random rn = new Random();
		int n = rn.nextInt();
		String ticketNumsQ = 
				"select tno "+
						"from tickets "+
						"where tno= '"+n+"'";
		ResultSet rs = stmt.executeQuery(ticketNumsQ);
		while (rs.next()) {
			tixList.add(rs.getInt("tno"));
			rs = stmt.executeQuery(ticketNumsQ);
		}
		// regenerate and requery db if tno already exists
		while (tixList.contains(n)) {
			n = rn.nextInt();
		}
		rs.close();
		return n;
	}
	private static String generateSeat() throws SQLException{
		List<String> seatList = new ArrayList<String>();
		Random rn = new Random();
		String n = String.valueOf(rn.nextInt()) + (char)((rn.nextInt(5) + 'A'));
		String seatsQ = 
				"select seat "+
						"from bookings "+
						"where seat='"+n+"'";
		ResultSet rs = stmt.executeQuery(seatsQ);
		// regenerate and requery db if seat already exists
		while (rs.next()) {
			n = String.valueOf(rn.nextInt()) + (char)((rn.nextInt(5) + 'A'));;
			rs = stmt.executeQuery(seatsQ);
		}
		rs.close();
		return n;

	}



	public static String makeBookings(String u_name, String email, String flightno, Float u_price, 
			String u_fare, Date u_depDate) throws SQLException {

		String bookingsQ = 
				"select tno, flightno, fare, dep_date, seat "+ 
						"from bookings";
		String ticketsQ = 
				"select tno, name, email, paid_price "+
						"from tickets";

		ResultSet bookings_rs = stmt.executeQuery(bookingsQ);
		ResultSet tickets_rs = stmt.executeQuery(ticketsQ);
		int tno = generateTix();
		String seat = generateSeat();
		m_con.setAutoCommit(false);	// start transaction block
		tickets_rs.moveToInsertRow();
		tickets_rs.updateInt("tno", tno);
		tickets_rs.updateString("name", u_name);
		tickets_rs.updateString("email", user.getEmail());
		tickets_rs.updateFloat("paid_price", u_price);
		tickets_rs.insertRow();
		bookings_rs.updateInt("tno", tno);
		bookings_rs.updateString("flightno", flightno);
		bookings_rs.updateString("fare", u_fare);
		bookings_rs.updateDate("dep_date", u_depDate);
		bookings_rs.updateString("seat", seat); //TODO:
		bookings_rs.insertRow();

		bookings_rs.close();
		tickets_rs.close();
		// check
		String checkBookingsQ =
				"select tno, flightno, fare, dep_date, seat "+
						"from bookings "+
						"where tno="+tno+
						" and flightno= '"+flightno+"'"+
						" and dep_date="+u_depDate;
		ResultSet check_rs = stmt.executeQuery(checkBookingsQ);

		if(!check_rs.next()) {
			m_con.commit();
			m_con.setAutoCommit(true);
			//TODO: confirmation message in the GUI
			return "Did the thing!";
		} else {
			//TODO: 
			m_con.setAutoCommit(true);
			return "Couldn't do the thing";
		}

	}
	public static ResultSet listBookings() throws SQLException {
		String bookingInfoQ =
				"select b.tno, t.name, b.dep_date, t.paid_price "+
						"from bookings b, tickets t "+
						"where b.tno=t.tno "+
						"and t.email = '"+user.getEmail()+"'";
		ResultSet rs = stmt.executeQuery(bookingInfoQ);
		// TODO: print out the freaking thing in the GUI
		return rs;
	}

	public static ResultSet bookingDetail() throws SQLException {
		String detailQ = // TODO: display a whole bunch of crap for this booking
				"";
		ResultSet rs = stmt.executeQuery(detailQ);
		return rs;
	}

	public static void cancelBooking(String u_flightno, Date u_dep_date, String u_seat) throws SQLException {
		String bookingsQ = 
				"select tno, flightno, fare, dep_date, seat "+
						"from bookings "+
						"where flightno= '"+u_flightno+"'"+
						" and dep_date="+u_dep_date+
						" and seat= '"+u_seat+"'";
		ResultSet rs = stmt.executeQuery(bookingsQ);
		while(rs.next()) {
			rs.deleteRow();
		}
	}
}