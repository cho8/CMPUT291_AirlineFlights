import java.util.*;
import java.sql.*;

public class AirlineSystem {
	static Statement stmt;
	static Connection m_con;
	static String m_url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
	String m_driverName = "oracle.jdbc.driver.OracleDriver";

	static User user = Main.currentuser;

	public void makeConnection(String m_userName, String m_password) throws SQLException{
		try {
			Class drvClass = Class.forName(m_driverName); 
			// DriverManager.registerDriver((Driver)drvClass.newInstance());- not needed. 
			// This is automatically done by Class.forName().
		} catch(Exception e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		} 


		// Establish a connection
		m_con = DriverManager.getConnection(m_url, m_userName,
				m_password);			
		stmt = m_con.createStatement(
				ResultSet.TYPE_SCROLL_SENSITIVE, 
				ResultSet.CONCUR_UPDATABLE);
		// Changed to reflect changes made in the result set and to make these changes permanent to the database too


	}

	public void closeConnection() throws SQLException {
		stmt.close();
		m_con.close();
	}

	private static String citySearch(String city) throws SQLException{
		//TODO: To be implemented in UserScreen
		if (city.length() == 3) {
			// airport code
			return city.toUpperCase();
		} else {
			String airportsQ = "select distinct acode from airports where lower(city) ='%"+city+"%'";
			ResultSet rs = stmt.executeQuery(airportsQ);
			if (rs.next())
				return rs.getString("acode").toUpperCase();
			else
				return null;
		}
	}

	private static void createViews() {
		String searchAvailable = "create view available_flights(flightno,dep_date, src,dst,dep_time,arr_time, price) as "+
				"select flightno, dep_date, src, dst, dep_time, arr_time, min(price) from (select f.flightno, sf.dep_date, f.src, f.dst, f.dep_time+(trunc(sf.dep_date)-trunc(f.dep_time)) dep_time, "+ 
				"(f.dep_time+(trunc(sf.dep_date)-trunc(f.dep_time))+(f.est_dur/60+a2.tzone-a1.tzone)/24) arr_time, "+
				"fa.limit-count(tno) seats, fa.price "+
				"from flights f, flight_fares fa, sch_flights sf, bookings b, airports a1, airports a2 "+
				"where f.flightno=sf.flightno and f.flightno=fa.flightno and f.src=a1.acode and "+
				"f.dst=a2.acode and fa.flightno=b.flightno(+) and fa.fare=b.fare(+) and "+
				"sf.dep_date=b.dep_date(+) "+
				"group by f.flightno, sf.dep_date, f.src, f.dst, f.dep_time, f.est_dur,a2.tzone, "+
				"a1.tzone, fa.limit, fa.price "+
				"having fa.limit-count(tno) > 0) "+
				"group by flightno, dep_date, src, dst, dep_time, arr_time";
		String searchAvailableFull = "create view available_flights_full(flightno,dep_date, src,dst,dep_time,arr_time,fare,seats, "+
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
		String goodConnect = "create view good_connections (src,dst,dep_date,flightno1,flightno2, layover,price, dep_time, arr_time, stops, dep_date2) as "+
				"select a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, (a2.dep_time-a1.arr_time)*24, min(a1.price+a2.price), a1.dep_time, a2.arr_time, 1 stops, a2.dep_date dep_date2 "+
				"from available_flights_full a1, available_flights_full a2 "+
				"where a1.dst=a2.src  "+
				"group by a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, a2.dep_time, a1.arr_time, a1.dep_time, a2.arr_time, a2.dep_date";
		String goodConnectFull = "create view good_connections_full (src,dst,dep_date,flightno1,flightno2, layover,price, seats, dep_time, arr_time, stops, dep_date2, fare, fare2) as "+
				"select a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, (a2.dep_time-a1.arr_time)*24, a1.price+a2.price, case when a1.seats <= a2.seats then a1.seats else a2.seats end, "+
				"a1.dep_time, a2.arr_time, 1 stops, a2.dep_date dep_date2, a1.fare, a2.fare fare2 "+
				"from available_flights_full a1, available_flights_full a2 "+
				"where a1.dst=a2.src "+
				"group by a1.src, a2.dst, a1.dep_date, a1.flightno, a2.flightno, a2.dep_time, a1.arr_time, a1.seats, a2.seats, a1.dep_time, a2.arr_time, a2.dep_date, a1.price+a2.price, a1.fare, a2.fare";
		try{
			stmt.executeUpdate("drop view available_flights");
			stmt.executeUpdate("drop view available_flights_full");
			stmt.executeUpdate("drop view good_connections");
			stmt.executeUpdate("drop view good_connections_full");
		}catch(SQLException f){
			System.err.println("SQLException drop views: "+f.getMessage());
		}

		try{
			stmt.executeUpdate(searchAvailable);
			stmt.executeUpdate(searchAvailableFull);
		}catch(SQLException h){
			System.err.println("SQLException available_flights");
		}

		try{
			stmt.executeUpdate(goodConnect);
			stmt.executeUpdate(goodConnectFull);
		}catch(SQLException g){
			System.err.println("SQLException good_connection :" + g.getMessage());
		}
	}
	public static ResultSet searchFlightsStandard(String u_src, String u_dst, String u_depDate, String orderBy){

		createViews();
		String twoFlightsView = "create view twoflights as ("+
				"select src, dst, dep_date, flightno1, flightno2, layover, price, stops, seats, dep_time, arr_time, dep_date2, fare, fare2 "+
				"from (select gf.src, gf.dst, gf.dep_date, gf.flightno1, gf.flightno2, gf.layover, gf.price, 1 stops, gf.seats, gf.dep_time, "+
				"gf.arr_time, gf.dep_date2, gf.fare, gf.fare2 "+
				"from good_connections_full gf, good_connections g "+
				"where gf.flightno1 = g.flightno1 and gf.flightno2 = g.flightno2 and gf.dep_date = g.dep_date and gf.price = g.price "+
				"union "+
				"select af.src, af.dst, af.dep_date, af.flightno flightno1, '' flightno2, 0 layover, af.price, 0 stops, af.seats, af.dep_time, af.arr_time, null dep_date2, af.fare, '' fare2 "+
				"from available_flights a, available_flights_full af "+
				"where af.flightno = a.flightno and af.dep_date = a.dep_date and af.price = a.price))";
		String twoFlightsQ = "select * from twoflights "+
				"where src='"+u_src+"' and dst='"+u_dst+"' and to_char(dep_date,'DD-MM-YYYY')='"+u_depDate+"' "+
				"order by "+orderBy; //price asc or stops asc


		ResultSet rs = null;
		try{
			stmt.executeUpdate("drop view twoflights");
			stmt.executeUpdate(twoFlightsView);
			rs = stmt.executeQuery(twoFlightsQ);
		}catch(SQLException g){
			System.err.println("SQLException in searchFlightsStandard :"+g.getMessage());
		}

		//return resultset to display on gui
		return rs;

	}

	public static ResultSet searchFlightsModified(String u_src, String u_dst, String u_depDate, String orderBy) throws SQLException{

		createViews();
		String threeFlightsView = "create view threeflights as ( "+
				"select src, dst, dep_date, flightno1, flightno2, flightno3, layover, layover2, price, stops, seats, dep_time, arr_time, dep_date2, dep_date3, "+
				"fare, fare2, fare3 "+
				"from (select gf.src, gf.dst, gf.dep_date, gf.flightno1, gf.flightno2, '' flightno3, gf.layover, 0 layover2, gf.price, 1 stops, gf.seats, "+ 
				"gf.dep_time, gf.arr_time, gf.dep_date2, null dep_date3, gf.fare, gf.fare2, '' fare3 "+
				"from good_connections_full gf, good_connections g "+
				"where gf.flightno1 = g.flightno1 and gf.flightno2 = g.flightno2 and gf.dep_date = g.dep_date and gf.price = g.price "+
				"union "+
				"select af.src, af.dst, af.dep_date, af.flightno flightno1, '' flightno2, '' flightno3, 0 layover, 0 layover2, af.price, 0 stops, af.seats, af.dep_time, "+ 
				"af.arr_time, null dep_date2, null dep_date3, af.fare, '' fare2, '' fare3 "+
				"from available_flights a, available_flights_full af "+
				"where af.flightno = a.flightno and af.dep_date = a.dep_date and af.price = a.price "+
				"union "+
				"select g2f.src, g2f.dst, g2f.dep_date, g2f.flightno1, g2f.flightno2, g2f.flightno3, g2f.layover, g2f.layover2, "+ 
				"g2f.price, 2 stops, g2f.seats, g2f.dep_time, g2f.arr_time, g2f.dep_date2, g2f.dep_date3, g2f.fare, g2f.fare2, g2f.fare3 "+
				"from good_connections2_full g2f, good_connections2 g2 "+
				"where g2f.flightno1 = g2.flightno1 and g2f.flightno2 = g2.flightno2 and g2f.flightno3 = g2.flightno3 and "+ 
				"g2f.dep_date = g2.dep_date and g2f.price = g2.price))";
		String threeFlightsQ = "select * from threeflights "+
				"where src='YEG' and dst='LAX' and to_char(dep_date,'DD/MM/YYYY')='15/10/2015' "+
				"order by stops asc, price asc";

		ResultSet rs = null;
		try{
			stmt.executeUpdate("drop view twoflights");
			stmt.executeUpdate(threeFlightsView);
			rs = stmt.executeQuery(threeFlightsQ);
		}catch(SQLException g){
			System.err.println("SQLException in searchFlightsStandard :"+g.getMessage());
		}

		//return resultset to display on gui
		return rs;
	}

	public static ResultSet searchReturnFlight(String u_src, String u_dst, String u_depDate) {
		ResultSet rs = null; //TODO
		return rs;
	}

	public static void updatePassengers(String u_name, String u_country) throws SQLException{
		// check name in passenger table
		String psgTable = 
				"select name, email from passengers where name='"+u_name+"' and email='"+Main.currentuser.getEmail()+"'";
		String insertPass =		
				"insert into passengers values('"+Main.currentuser.getEmail()+"','"+u_name+"','"+u_country+"')";
		ResultSet p_rs = stmt.executeQuery(psgTable);
		// Look for passenger's name in passengers table. Add if not exist
		if (!p_rs.next()) {
			System.out.println("yay added");
			stmt.executeUpdate(insertPass);
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
		int n = rn.nextInt(999) + 1;
		while (checkTicket(n))
			n = rn.nextInt();
		return n;
	}

	private static Boolean checkBooking(String flightno, String u_depDate, String u_fare) throws SQLException {
		// check if flight still available
		String checkFlightQ = "select seats "+
				"from available_flights "+
				"where flightno='"+flightno+"' "+
				"and to_char(dep_date, 'yyyy-mm-dd')='"+u_depDate+"' "+
				"and fare='"+u_fare+"' "+
				"and seats>0";
		System.out.println(checkFlightQ);
		ResultSet checkFlight = stmt.executeQuery(checkFlightQ);

		return (checkFlight.next());
	}

	public static int makeBookings(String u_name, String email, String flightno, Float u_price, 
			String u_depDate, String u_seat, String u_country) throws SQLException {

		// make sure you're calling this a multiple number of times for multiple flights in a trip
		String fareTypeQ = "select distinct fare from flight_fares where flightno ='"+flightno+"' "+
				"and price="+String.valueOf(u_price);
		ResultSet rs = stmt.executeQuery(fareTypeQ);
		String u_fare = "";
		if (rs.first()) {
			u_fare = rs.getString("fare").trim();
		} else {
			System.out.println("no fare");
		}
		int tno = generateTix();

		System.out.println("made it to commit");
		//		ResultSet rs = stmt.executeQuery(tickets_rs);
		//		rs.next();
		//		rs = stmt.executeQuery(bookings_rs);
		//		rs.next();
		if(checkBooking(flightno, u_depDate, u_fare)) {
			updatePassengers(u_name, u_country);
			String tickets = "insert into tickets values("+tno+",'"+u_name+"','"
					+Main.currentuser.getEmail()+"',"+u_price+")";
			String booking = "insert into bookings values("+tno+",'"+flightno+"','"+u_fare+"',"
					+"to_date('"+ u_depDate +"', 'yyyy-mm-dd'),'"+u_seat+"')";
			stmt.executeUpdate(tickets);
			stmt.executeUpdate(booking);
			//TODO: confirmation message in the GUI
			return tno;
		} else {
			System.out.println("Something went wrong. Booking failed");
			// return whether the booking failed, what we do hereafter depends on design choice
			return 0;
		}

	}
	public static ResultSet listBookings() throws SQLException {
		String bookingInfoQ = "select b.tno, b.flightno, t.name, b.dep_date, t.paid_price "+
				"from bookings b, tickets t "+
				"where b.tno=t.tno "+
				"and t.email='"+Main.currentuser.getEmail()+"'";
		System.out.println(bookingInfoQ);
		ResultSet rs = stmt.executeQuery(bookingInfoQ);
		// return resultset to display on gui
		return rs;
	}

	public static ResultSet bookingDetail() throws SQLException {
		String detailQ = "select b.tno, t.name, b.flightno, f.src, f.dst, b.fare, b.dep_date, b.seat, t.paid_price "+
				"from bookings b, tickets t, flights f "+
				"where b.tno=t.tno "+
				"and f.flightno=b.flightno "+
				"and t.email='"+Main.currentuser.getEmail()+"'";// TODO: display more information, design choice

		ResultSet rs = stmt.executeQuery(detailQ);
		// return resultset to display on gui
		return rs;
	}

	public static void cancelBooking(int u_tno, String u_flightno, String u_dep_date) throws SQLException {
		String bookingsQ = "delete from bookings "+
				" where flightno= '"+u_flightno+"'"+
				" and to_char(dep_date, 'yyyy-mm-dd') = '"+u_dep_date+"'"+
				" and tno="+u_tno;
		String ticketsQ = "delete from tickets "+
				"where tno="+u_tno;

		stmt.executeUpdate(bookingsQ);
		stmt.executeUpdate(ticketsQ);
	}

	public static void recordDepTime(String flightno, String act_dep_time) throws SQLException {
		if (act_dep_time.equals("")) return;
		System.out.println(act_dep_time);
		String depTimeQ = "UPDATE sch_flights "+
				"SET act_dep_time=to_date('"+act_dep_time+"', 'hh24:mi') "+
				"WHERE flightno ='"+flightno+"'";
		stmt.executeUpdate(depTimeQ);
	}
	public static void recordArrTime(String flightno, String act_arr_time) throws SQLException {
		if (act_arr_time.equals("")) return;
		System.out.println(act_arr_time);
		String arrTimeQ = "UPDATE sch_flights "+
				"SET act_arr_time=to_date('"+act_arr_time+"', 'hh24:mi') "+
				"WHERE flightno ='"+flightno+"'";
		stmt.executeUpdate(arrTimeQ);
	}

}