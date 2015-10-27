import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserScreen{
	//The width of the application window
	public static final int PANEL_WIDTH = 300;
	//The width of the application window
	public static final int APPLICATION_WIDTH = PANEL_WIDTH*2+PANEL_WIDTH/2;
	//The height of the application window 
	public static final int APPLICATION_HEIGHT = 400;
	//Number of characters for each of the text input fields
	public static final int TEXT_FIELD_SIZE = 15;
	//Month strings for drop-down menu.
	private static final String[] months = {"January","February","March","April",
			"May","June","July","August","September","October","November","December"};

	private UserLoginScreen lscreen;
	private JComboBox<String> monthbox;
	private JComboBox<Integer> datebox;
	private JComboBox<Integer> yearbox;
	private JComboBox<String> rmonthbox;
	private JComboBox<Integer> rdatebox;
	private JComboBox<Integer> ryearbox;
	private JPanel leftp = new JPanel();
	private JPanel rightp = new JPanel();
	private JPanel centrep = new JPanel();
	private JPanel agentp = new JPanel();
	private JList<String> myList;
	private DefaultListModel<String> model;
	private JScrollPane scrollPane;
	private JTextField src;
	private JTextField dest;
	private JTextField passenger;
	private JTextField country;
	private JTextField flightno;
	private JTextField act_dep;
	private JTextField act_arr;
	private JButton updateflight;
	private JButton searchf;
	private JButton logout;
	private JButton create;
	private JButton existing;
	private JButton clear;
	private JButton deletebooking;
	private JButton bookingdetail;
	private JLabel passengerlab;
	private JLabel countrylab;
	private JLabel returndate;
	private JLabel bookingmessage;
	private JCheckBox roundtrip;
	private ResultSet results;
	private boolean isroundtrip;
	private JRadioButton sortbyprice;
	private JRadioButton sortbyconn;

	public UserScreen(){
		src = new JTextField(TEXT_FIELD_SIZE);
		dest = new JTextField(TEXT_FIELD_SIZE);
		flightno = new JTextField(TEXT_FIELD_SIZE);
		act_dep = new JTextField(TEXT_FIELD_SIZE);
		act_arr = new JTextField(TEXT_FIELD_SIZE);
		bookingmessage = new JLabel("");
		sortbyprice = new JRadioButton("Price");
		sortbyconn = new JRadioButton("Connections");
		returndate = new JLabel("Return Date");
		roundtrip = new JCheckBox("Roundtrip");
		model = new DefaultListModel<String>();
		myList = new JList<String>(model);
		scrollPane = new JScrollPane(myList);
		searchf = new JButton("Search Flights");
		create = new JButton("Create Booking");
		existing = new JButton("Existing Bookings");
		logout = new JButton("Logout");
		clear = new JButton("Clear Results");
		bookingdetail = new JButton("Booking Details");
		passengerlab = new JLabel("Passenger Name");
		countrylab = new JLabel("Country");
		passenger = new JTextField(TEXT_FIELD_SIZE);
		country = new JTextField(TEXT_FIELD_SIZE);
		deletebooking = new JButton("Cancel Booking");
		updateflight = new JButton("Update Flight Times");
		Integer[] s = new Integer[31];
		for(int i = 0;i<31;i++){
			s[i] = i+1;
		}
		Integer[] y = new Integer[20];
		for(int i = 0;i<20;i++){
			y[i] = i+2014;
		}
		monthbox = new JComboBox<String>(months);
		datebox = new JComboBox<Integer>(s);
		yearbox = new JComboBox<Integer>(y);
		rmonthbox = new JComboBox<String>(months);
		rdatebox = new JComboBox<Integer>(s);
		ryearbox = new JComboBox<Integer>(y);

		agentp.setLayout(new GridLayout(10,1));
		leftp.setLayout(new GridLayout(10,3));
		rightp.setLayout(new GridLayout(10,1));
		centrep.setLayout(new GridLayout(1,1));

	}

	public void init(){
		centrep.setPreferredSize(new Dimension(PANEL_WIDTH,APPLICATION_HEIGHT));
		leftp.setPreferredSize(new Dimension(PANEL_WIDTH,APPLICATION_HEIGHT));
		rightp.setPreferredSize(new Dimension(PANEL_WIDTH/2,APPLICATION_HEIGHT));
		agentp.setPreferredSize(new Dimension(PANEL_WIDTH/2,APPLICATION_HEIGHT));
		Main.mainpanel.setLayout(new FlowLayout());

		leftp.add(new JLabel("Source"));
		leftp.add(src);
		leftp.add(new JLabel());
		leftp.add(new JLabel("Destination"));
		leftp.add(dest);
		leftp.add(new JLabel());
		leftp.add(new JLabel("Dep Date"));
		leftp.add(new JLabel());
		leftp.add(new JLabel());
		leftp.add(monthbox);
		leftp.add(datebox);
		leftp.add(yearbox);
		leftp.add(roundtrip);
		leftp.add(new JLabel());
		leftp.add(new JLabel());
		leftp.add(returndate);
		leftp.add(new JLabel());
		leftp.add(new JLabel());
		leftp.add(rmonthbox);
		leftp.add(rdatebox);
		leftp.add(ryearbox);
		returndate.setVisible(false);
		rmonthbox.setVisible(false);
		rdatebox.setVisible(false);
		ryearbox.setVisible(false);
		isroundtrip = false;
		leftp.add(new JLabel("Sort By:"));
		leftp.add(sortbyprice);
		leftp.add(sortbyconn);
		leftp.add(searchf);
		leftp.add(logout);

		centrep.add(scrollPane);

		rightp.add(create);
		rightp.add(passengerlab);
		rightp.add(passenger);
		rightp.add(countrylab);
		rightp.add(country);
		rightp.add(bookingdetail);
		passengerlab.setVisible(false);
		countrylab.setVisible(false);
		passenger.setVisible(false);
		country.setVisible(false);
		bookingdetail.setVisible(false);
		rightp.add(existing);
		rightp.add(deletebooking);
		deletebooking.setVisible(false);
		rightp.add(clear);

		agentp.add(new JLabel("Flight Number"));
		agentp.add(flightno);
		agentp.add(new JLabel("Actual Departure Time"));
		agentp.add(act_dep);
		agentp.add(new JLabel("Actual Arrival Time"));
		agentp.add(act_arr);
		agentp.add(updateflight);


		addListeners();
		Main.mainpanel.add(leftp);
		Main.mainpanel.add(centrep);
		Main.mainpanel.add(rightp);
		if(User.isAgent()){
			Main.frame.setSize(APPLICATION_WIDTH+30+(PANEL_WIDTH/2), APPLICATION_HEIGHT+40);//I have to add to the dimensions here for some reason to get it to display properly.
			Main.mainpanel.add(agentp);
		} else{
			Main.frame.setSize(APPLICATION_WIDTH+20, APPLICATION_HEIGHT+40);
		}
		Main.frame.validate();
		Main.frame.repaint();
	}

	public void clear(){
		Main.mainpanel.removeAll();

	}

	private void addListeners(){
		searchf.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deletebooking.setVisible(false);
				bookingdetail.setVisible(false);
				create.setVisible(true);
				getFlights();
				displayFlights();
			}
		});
		logout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					Main.currentuser.setLastLogin();
				}catch(SQLException err){
					System.out.println("Cannot logout...you're stuck here forever. "
							+ err.getMessage());
				}
				clear();
				lscreen = new UserLoginScreen();
				lscreen.init();
			}
		});
		create.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(create.getText() == "Create Booking"){
					deletebooking.setVisible(false);
					passengerlab.setVisible(true);
					countrylab.setVisible(true);
					passenger.setVisible(true);
					country.setVisible(true);
					create.setText("Confirm Booking");
				} else {
					create.setText("Create Booking");
					createBooking();
				}

				//				System.out.println(myList.getSelectedValue());
			}
		});
		existing.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				model.clear();
				passengerlab.setVisible(false);
				countrylab.setVisible(false);
				passenger.setVisible(false);
				country.setVisible(false);
				create.setVisible(false);
				deletebooking.setVisible(true);
				bookingdetail.setVisible(true);
				displayBookings();
				System.out.println("Existing");
			}
		});
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				model.clear();
			}
		});
		deletebooking.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancelBooking();
			}
		});
		bookingdetail.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				displayDetailBooking();
			}
		});
		updateflight.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					AirlineSystem.recordDepTime(flightno.getText(),act_dep.getText());
					AirlineSystem.recordArrTime(flightno.getText(),act_arr.getText());

				}catch(SQLException f){
					System.out.println("update Flight: " + f.getMessage());
				}
			}
		});
		sortbyprice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sortbyconn.setSelected(false);
			}
		});
		sortbyconn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sortbyprice.setSelected(false);
			}
		});
		roundtrip.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(roundtrip.isSelected()){
					returndate.setVisible(true);
					rmonthbox.setVisible(true);
					rdatebox.setVisible(true);
					ryearbox.setVisible(true);
					isroundtrip = true;
				}else{
					returndate.setVisible(false);
					rmonthbox.setVisible(false);
					rdatebox.setVisible(false);
					ryearbox.setVisible(false);
					isroundtrip = false;
				}
			}
		});
	}

	private void createBooking(){
		String tno = "";
		try{
			getFlights();
			results.absolute(myList.getSelectedIndex()+1);
			int stops = results.getInt("stops");

			for(int i = 0; i<=stops; i++){
				tno += AirlineSystem.makeBookings(passenger.getText(),Main.currentuser.getEmail(),
						results.getString("flightno"+(i+1)).trim(),results.getFloat("price"+(i+1)),
						results.getDate("dep_date").toString(),"",country.getText()) + " "; 

			}
			
			if(isroundtrip){
				//book returntrip here.
			}
			bookingmessage.setText("Booking Confirmed-Ticket No(s): "+tno);

		}catch(SQLException e){
			System.out.println("createBooking: " + e.getMessage());
			bookingmessage.setText("Could Not Confirm Booking");
		}

	}
	private void getFlights(){
		String sortby;
		if(sortbyprice.isSelected()){
			sortby = "price asc";
		}else{
			sortby = "stops asc";
		}
		String date = datebox.getSelectedItem()+"-"+(monthbox.getSelectedIndex()+1)+"-"+yearbox.getSelectedItem();
		
		//Added a cityQuery in AirlineSystem that takes in lowercase airport codes and partial city matches

		//TODO: give the user option to pick a matching city
		System.out.println(date+" "+src.getText()+" "+dest.getText());
		// pass the result (an airport code) into searchFlightsX
<<<<<<< HEAD
		results = AirlineSystem.searchFlightsModified(src.getText(),dest.getText(),date,"price asc");
		//TODO: change "price asc" to something the user can select
		//TODO: GUI Option to search for 3 flights;
=======
		results = AirlineSystem.searchFlightsStandard(src.getText(),dest.getText(),date,sortby);
>>>>>>> 4cc601b819549b86c33ca37e32dfe1bebe7600f3

	}

	//TODO: Round Trip

	private void displayBookings(){
		model.clear();
		try{
			ResultSet bookings = AirlineSystem.listBookings();

			while(bookings.next()){
				String booking = "Ticket: " + bookings.getString("tno") 
				+ ", Name: " + bookings.getString("name").trim()
				+ ", Departure Date: " + bookings.getDate("dep_date").toString()
				+ ", Paid Price: " + bookings.getFloat("paid_price");
				model.addElement(booking);
			}
		}catch(SQLException e){
			System.out.println("Can't Fetch Bookings " + e.getMessage());
		}

	}

	private void cancelBooking(){
		try{
			ResultSet bookings = AirlineSystem.listBookings();

			bookings.absolute(myList.getSelectedIndex()+1);
			AirlineSystem.cancelBooking(bookings.getInt("tno"),
					bookings.getString("flightno"),
					bookings.getDate("dep_date").toString());
			model.remove(myList.getSelectedIndex());
		}catch(SQLException e){
			System.out.println("cancelbooking: "+e.getMessage());
		}
	}

	private void displayDetailBooking(){
		//More Details about booking.
		//Added this, I included additional fields
		//I'm thinking  for simplicity we just display everything-- Christina
		model.clear();
		try{
			ResultSet bookings = AirlineSystem.listBookings();

			while(bookings.next()){
				String booking = "Ticket: " + bookings.getString("tno") 
				+ ", Name: " + bookings.getString("name").trim()
				+ ", Flight No.: " + bookings.getString("flightno")
				+ ", Source : " + bookings.getString("src")
				+ ", Destination : " + bookings.getString("dst")
				+ ", Departure Date: " + bookings.getDate("dep_date").toString()
				+ ", Seat: " + bookings.getString("seat").toString()
				+ ", Paid Price: " + bookings.getFloat("paid_price");
				model.addElement(booking);
			}
		}catch(SQLException e){
			System.out.println("Can't Fetch Bookings " + e.getMessage());
		}
	}

	private void displayFlights(){
		model.clear();
		boolean areflights = false;

		try{

			while(results.next()){
				areflights = true;
				int stops = results.getInt("stops");
				String flight1 = "";
				String flight2 = "";
				String flight3 = "";
				String layover;
				if(stops == 0){
					layover = "";
				}else{
					layover = "Layover Time: "  
							+ String.valueOf(results.getFloat("layover")) + ", ";
				}

				for(int i = 0; i<= stops; i++){
					switch(i){
						case 0: flight1 = "Flight 1: " + results.getString("flightno1") + ", ";
								break;
						case 1: flight1 = "Flight 2: " + results.getString("flightno2") + ", ";
								break;
						case 2: flight1 = "Flight 3: " + results.getString("flightno3") + ", ";
								break;
					}

				}
				String resultitem = "Source: " + results.getString("src")
				+", Destination: "+ results.getString("dst") + ", "
				+ flight1 + flight2 + flight3
				+ "Stops: " + stops + ", " + layover + "Price: " 
				+ results.getFloat("price") + ", Departure Time: "
				+ results.getDate("dep_time").toString() + ", Arrival Time: "
				+ results.getDate("arr_time").toString() + ", Available Seats: "
				+ results.getInt("seats");
				model.addElement(resultitem);
			}
		} catch(SQLException e){
			System.out.println("Can't get Flight" + e.getMessage());
		}

		if (!areflights){
			model.addElement("No Available Flights");
		}
	}

}
