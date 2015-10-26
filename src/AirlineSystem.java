import java.util.*;
import java.sql.*;
import java.sql.Date;

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
			stmt = m_con.createStatement(
					ResultSet.TYPE_SCROLL_SENSITIVE, 
					ResultSet.CONCUR_UPDATABLE);
			// Changed to reflect changes made in the result set and to make these changes permanent to the database too
		}  catch(SQLException ex) {
			System.err.println("SQLException: " +
					ex.getMessage());
		}

	}

	public void closeConnection() throws SQLException {
		stmt.close();
		m_con.close();
	}

	private static void flightsQuery(String u_src, String u_dst, String u_depDate) throws SQLException {
		String searchAvailable = "create view available_flights(flightno,dep_date, src,dst,dep_time,arr_time,fare,seats, "+
				"price) as "+
				"select f.flightno, sf.dep_date, f.src, f.dst, f.dep_time+(trunc(sf.dep_date)-trunc(f.dep_time)), "+
				"f.dep_time+(trunc(sf.dep_date)-trunc(f.dep_time))+(f.est_dur/60+a2.tzone-a1.tzone)/24, "+
				"fa.fare, fa.limit-count(tno), fa.price "+
				"from flights f, flight_fares fa, sch_flights sf, bookings b, airports a1, airports a2 "+
				"where f.flightno=sf.flightno and f.flightno=fa.flightno and f.src=a1.acode and "+
				"f.dst=a2.acode and fa.flightno=b.flightno(+) and fa.fare=b.fare(+) and "+
				"sf.dep_date=b.dep_date(+) "+
				"group by f.flightno, sf.dep_date, f.src, f.dst, f.dep_time, f.est_dur,a2.tzone, "+
				"a1.tzone, fa.fare, fa.limit, fa.price "+
				"having fa.limit-count(tno) > 0";

		stmt.executeUpdate("drop view available_flights");
		stmt.executeUpdate(searchAvailable);
	}


	public static ResultSet searchFlightsStandard(String u_src, String u_dst, String u_depDate, String orderBy) throws SQLException{
		
		flightsQuery(u_src, u_dst, u_depDate);
		String goodConnect = "create view good_connections (src,dst,dep_date,flightno1,flightno2, layover,price, seats, dep_time, arr_time, stops) as "+
				"select a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, " +
				"(a2.dep_time-a1.arr_time)*24, a1.price+a2.price, case when a1.seats <= a2.seats then a1.seats else a2.seats end, a1.dep_time, a2.arr_time, 1 stops "+
				"from available_flights a1, available_flights a2 "+
				"where a1.dst=a2.src "+
				"group by a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, "+
				"a2.dep_time, a1.arr_time, a1.price+a2.price, a1.seats, a2.seats, a1.dep_time, a2.arr_time";
		String viewFlightsQ = "select src, dst, dep_date, flightno1, flightno2, layover, price, stops, seats, dep_time, arr_time "+
				"from (select src, dst, dep_date, flightno1, flightno2, layover, price, 1 stops, seats, dep_time, arr_time "+
				"from good_connections "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char(dep_date,'DD-MM-YYYY')='"+u_depDate+"' "+
				"union "+
				"select src, dst, dep_date, flightno flightno1, '' flightno2, 0 layover, price, 0 stops, seats, dep_time, arr_time "+
				"from available_flights "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char(dep_date,'DD-MM-YYYY')='"+u_depDate+"') "+
				"order by "+orderBy; //price asc

		stmt.executeUpdate("drop view good_connections");
		stmt.executeUpdate(goodConnect);
		ResultSet rs = stmt.executeQuery(viewFlightsQ);
		//return resultset to display on gui
		return rs;
	}

	public static ResultSet searchFlightsModified(String u_src, String u_dst, String u_depDate, String orderBy) throws SQLException{
		
		flightsQuery(u_src, u_dst, u_depDate);
		String goodConnect2 = "create view good_connections2 (src, dst, dep_date, flightno1, flightno2, flightno3, layover, layover2, price, seats, dep_time, arr_time, stops) as "+
				"select a1.src, a3.dst, a1.dep_date, a1.flightno, a2.flightno, a3.flightno, a2.dep_time-a1.arr_time, a3.dep_time-a2.arr_time , "+
				"a1.price+a2.price+a3.price, case when a1.seats <= a2.seats and a1.seats <= a3.seats then a1.seats when a2.seats <= a3.seats then a2.seats else a3.seats end, a1.dep_time, a3.arr_time, 2 stops "+
				"from available_flights a1, available_flights a2, available_flights a3 "+
				"where a1.dst=a2.src and a2.dst=a3.src "+
				"group by a1.src, a3.dst, a1.dep_date, a1.flightno, a2.flightno, a3.flightno, a2.dep_time, a1.arr_time, a3.dep_time, a2.arr_time, "+
				"a1.price+a2.price+a3.price, a1.seats, a2.seats, a3.seats, a1.dep_time, a3.arr_time";
		String viewFlightsQ = "select src, dst, dep_date, flightno1, flightno2, flightno3, layover, layover2, price, stops, seats, dep_time, arr_time "+
				"from (select src, dst, dep_date, flightno1, flightno2, '' flightno3, layover, 0 layover2, price, 1 stops, seats, dep_time, arr_time "+
				"from good_connections "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char(dep_date,'DD-MM-YYYY')='"+u_depDate+"' "+
				"union "+
				"select src, dst, dep_date, flightno flightno1, '' flightno2, '' flightno3, 0 layover, 0 layover2, price, 0 stops, seats, dep_time, arr_time "+
				"from available_flights "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char(dep_date,'DD-MM-YYYY')='"+u_depDate+"' "+
				"union "+
				"select src, dst, dep_date, flightno1, flightno2, flightno3, layover, layover2, price, 2 stops, seats, dep_time, arr_time "+
				"from good_connections2 "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char(dep_date,'DD-MM-YYYY')='"+u_depDate+"') "+
				"order by "+orderBy; //stops asc, price asc";
		stmt.executeUpdate("drop view good_connections2");
		stmt.executeUpdate(goodConnect2);
		ResultSet rs = stmt.executeQuery(viewFlightsQ);
		//return resultset to display on gui
		return rs;
	}

	public void updatePassengers(String u_name, String u_country) throws SQLException{
		// check name in passenger table
		String psgTable = 
				"select name, email, country "+
						"from passengers "+
						"where name= '"+u_name+"' "+
						"and email='"+user.getEmail()+"'";
		ResultSet p_rs = stmt.executeQuery(psgTable);

		// Look for passenger's name in passengers table. Add if not exist
		if (!p_rs.next()) {
			p_rs.moveToInsertRow();
			p_rs.updateString("name",u_name);
			p_rs.updateString("email", user.getEmail());
			p_rs.updateString("country", u_country);
			p_rs.updateRow();
		}
	}

	private static Boolean checkTicket(int n) throws SQLException {
		// check if ticket is unique
		String tnoQ = 
				"select tno "+
						"from tickets "+
						"where tno='"+n+"'";
		ResultSet rs = stmt.executeQuery(tnoQ);
		return rs.next();
	}
	
	private static int generateTix() throws SQLException {

		Random rn = new Random();
		int n = rn.nextInt();
		while (checkTicket(n))
			n = rn.nextInt();
		return n;
	}

	private static Boolean checkSeat(String seat) throws SQLException {
		//check if seat not taken
		String seatsQ = 
				"select seat "+
						"from available_flights a1, available_flights a2"+
						"where a1.flightno=a2.flightno "+
						"and a1.dep_date = a2.dep_date "+
						"and a1.seat<>a2.seat";
		ResultSet rs = stmt.executeQuery(seatsQ);
		return rs.next();
	}

	private static Boolean checkBooking(int tno, String seat) throws SQLException {
		// check if flight still available
		String checkFlightQ = "select flightno, dep_date"+
				"from available_flights a1, available_flights a2 "+
				"where a1.flightno=a2.flightno "+
				"and a1.dep_date=a2.dep_date";
		ResultSet checkFlight = stmt.executeQuery(checkFlightQ);	
		return (checkFlight.next() && checkSeat(seat) && checkTicket(tno));
	}

	public static Boolean makeBookings(String u_name, String email, String flightno, Float u_price, 
			String u_fare, String u_depDate, String u_seat) throws SQLException {

		// should we do all conversions in the system? ie pass all values into this method as strings?
		String bookingsQ = 
				"select tno, flightno, fare, dep_date, seat "+ 
						"from bookings";
		String ticketsQ = 
				"select tno, name, email, paid_price "+
						"from tickets";

		ResultSet bookings_rs = stmt.executeQuery(bookingsQ);
		ResultSet tickets_rs = stmt.executeQuery(ticketsQ);
		int tno = generateTix();
		m_con.setAutoCommit(false);	// start transaction block
		tickets_rs.moveToInsertRow();
		tickets_rs.updateInt("tno", tno);
		tickets_rs.updateString("name", u_name);
		tickets_rs.updateString("email", user.getEmail());
		tickets_rs.updateFloat("paid_price", u_price);
		bookings_rs.updateInt("tno", tno);
		bookings_rs.updateString("flightno", flightno);
		bookings_rs.updateString("fare", u_fare);
		bookings_rs.updateDate("dep_date", Date.valueOf(u_depDate));
		bookings_rs.updateString("seat", u_seat); 

		if(checkBooking(tno, u_seat)) {
			tickets_rs.insertRow();
			bookings_rs.insertRow();
			m_con.commit();
			m_con.setAutoCommit(true);
			//TODO: confirmation message in the GUI
			return true;
		} else {
			//TODO: make sure this works
			m_con.setAutoCommit(true);
			// return whether the booking failed, what we do hereafter depends on design choice
			return false;
		}

	}
	public static ResultSet listBookings() throws SQLException {
		String bookingInfoQ = "select b.tno, t.name, b.dep_date, t.paid_price "+
				"from bookings b, tickets t "+
				"where b.tno=t.tno "+
				"and t.email='"+user.getEmail()+"'";
		ResultSet rs = stmt.executeQuery(bookingInfoQ);
		// return resultset to display on gui
		return rs;
	}

	public static ResultSet bookingDetail() throws SQLException {
		String detailQ = "select "+
				"from bookings b, tickets t, ";// TODO: display more information, design choice

		ResultSet rs = stmt.executeQuery(detailQ);
		// return resultset to display on gui
		return rs;
	}

	public static void cancelBooking(int u_tno, String u_flightno, String u_dep_date, String u_seat) throws SQLException {
		String bookingsQ = "delete from bookings "+
				"where flightno= '"+u_flightno+"'"+
				" and dep_date="+u_dep_date+
				" and tno="+u_tno;
		String ticketsQ = "delete from tickets "+
				"where tno="+u_tno;

		stmt.executeUpdate(bookingsQ);
		stmt.executeUpdate(ticketsQ);
	}

}