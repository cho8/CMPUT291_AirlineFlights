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

		String goodConnect = "create view good_connections (src,dst,dep_date,flightno1,flightno2, layover,price, seats, dep_time, arr_time, stops) as "+
				"select a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, " +
				"(a2.dep_time-a1.arr_time)*24, a1.price+a2.price, case when a1.seats <= a2.seats then a1.seats else a2.seats end, a1.dep_time, a2.arr_time, 1 stops "+
				"from available_flights a1, available_flights a2 "+
				"where a1.dst=a2.src "+
				"group by a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, "+
				"a2.dep_time, a1.arr_time, a1.price+a2.price, a1.seats, a2.seats, a1.dep_time, a2.arr_time";
		stmt.executeUpdate("drop view available_flights");
		stmt.executeUpdate("drop view good_connections");
		stmt.executeUpdate(searchAvailable);
		stmt.executeUpdate(goodConnect);
	}
	public static ResultSet searchFlightsStandard(String u_src, String u_dst, String u_depDate) throws SQLException{

		flightsQuery(u_src, u_dst, u_depDate);
		String viewFlightsQ = "select src, dst, dep_date, flightno1, flightno2, layover, price, stops, seats, dep_time, arr_time "+
				"from (select src, dst, dep_date, flightno1, flightno2, layover, price, 1 stops, seats, dep_time, arr_time "+
				"from good_connections "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char( '"+u_depDate+"','DD/MM/YYYY')='15/10/2015' "+
				"union "+
				"select src, dst, dep_date, flightno flightno1, '' flightno2, 0 layover, price, 0 stops, seats, dep_time, arr_time "+
				"from available_flights "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char('"+u_depDate+"','DD/MM/YYYY')='15/10/2015') "+
				"order by price asc";
		ResultSet rs = stmt.executeQuery(viewFlightsQ);
		//return result set to display on gui
		return rs;
	}

	public static ResultSet searchFlightsThreeFlights(String u_src, String u_dst, String u_depDate) throws SQLException{
		flightsQuery(u_src, u_dst, u_depDate);
		String viewFlightsQ = "select src, dst, dep_date, flightno1, flightno2, flightno3, layover, layover2, price, stops, seats, dep_time, arr_time "+
				"from (select src, dst, dep_date, flightno1, flightno2, '' flightno3, layover, 0 layover2, price, 1 stops, seats, dep_time, arr_time "+
				"from good_connections "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char('"+u_depDate+"','DD/MM/YYYY')='15/10/2015' "+
				"union "+
				"select src, dst, dep_date, flightno flightno1, '' flightno2, '' flightno3, 0 layover, 0 layover2, price, 0 stops, seats, dep_time, arr_time "+
				"from available_flights "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char('"+u_depDate+"','DD/MM/YYYY')='15/10/2015' "+
				"union "+
				"select src, dst, dep_date, flightno1, flightno2, flightno3, layover, layover2, price, 2 stops, seats, dep_time, arr_time "+
				"from good_connections2 "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char('"+u_depDate+"','DD/MM/YYYY')='15/10/2015') "+
				"order by stops asc, price asc";
		ResultSet rs = stmt.executeQuery(viewFlightsQ);
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
			n = String.valueOf(rn.nextInt(999)) + (char)((rn.nextInt(5) + 'A'));;
			rs = stmt.executeQuery(seatsQ);
		}
		return n;

	}



	public static String makeBookings(String u_name, String email, String flightno, Float u_price, 
			String u_fare, String u_depDate) throws SQLException {

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
		bookings_rs.updateString("dep_date", u_depDate);
		bookings_rs.updateString("seat", seat); //TODO:
		bookings_rs.insertRow();

		// check
		String checkBookingsQ =
				"select tno, flightno, fare, dep_date, seat "+
						"from bookings "+
						"where tno="+tno+
						" and flightno= '"+flightno+"'"+
						" and dep_date="+u_depDate;//TODO
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

	public static void cancelBooking(int u_tno, String u_flightno, String u_dep_date, String u_seat) throws SQLException {
		String bookingsQ = 
				"delete from bookings "+
						"where flightno= '"+u_flightno+"'"+
						" and dep_date="+u_dep_date+
						" and seat= '"+u_seat+"'"+
						" and tno="+u_tno;
		String ticketsQ = 
				"delete from tickets "+
						"where tno="+u_tno+
						" and email='"+user.getEmail();
		stmt.executeUpdate(bookingsQ);
	}

}