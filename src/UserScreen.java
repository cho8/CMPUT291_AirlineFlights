import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserScreen{
	//The width of the application window
	public static final int APPLICATION_WIDTH = 800;
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
	private JPanel leftp = new JPanel();
	private JPanel rightp = new JPanel();
	private JPanel centrep = new JPanel();
	private JList<String> myList;
	private DefaultListModel<String> model;
	private JScrollPane scrollPane;
	private JTextField src;
	private JTextField dest;
	private JTextField passenger;
	private JTextField country;
	private JButton searchf;
	private JButton logout;
	private JButton create;
	private JButton existing;
	private JButton clear;
	private JButton deletebooking;
	private JLabel passengerlab;
	private JLabel countrylab;
	private ResultSet results;

	public UserScreen(){
		src = new JTextField(TEXT_FIELD_SIZE);
		dest = new JTextField(TEXT_FIELD_SIZE);
		model = new DefaultListModel<String>();
		myList = new JList<String>(model);
		scrollPane = new JScrollPane(myList);
		searchf = new JButton("Search Flights");
		create = new JButton("Create Booking");
		existing = new JButton("Existing Bookings");
		logout = new JButton("Logout");
		clear = new JButton("Clear Results");
		passengerlab = new JLabel("Passenger Name");
		countrylab = new JLabel("Country");
		passenger = new JTextField(TEXT_FIELD_SIZE);
		country = new JTextField(TEXT_FIELD_SIZE);
		deletebooking = new JButton("Cancel Booking");
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

		leftp.setLayout(new GridLayout(10,1));
		rightp.setLayout(new GridLayout(10,1));
		centrep.setLayout(new GridLayout(1,1));

	}

	public void init(){
		Main.frame.setSize(APPLICATION_WIDTH+20, APPLICATION_HEIGHT+40);//I have to add to the dimensions here for some reason to get it to display properly.
		centrep.setPreferredSize(new Dimension(APPLICATION_WIDTH/2,APPLICATION_HEIGHT));
		leftp.setPreferredSize(new Dimension(APPLICATION_WIDTH/4,APPLICATION_HEIGHT));
		rightp.setPreferredSize(new Dimension(APPLICATION_WIDTH/4,APPLICATION_HEIGHT));
		Main.mainpanel.setLayout(new FlowLayout());

		leftp.add(new JLabel("Source"));
		leftp.add(src);
		leftp.add(new JLabel("Destination"));
		leftp.add(dest);
		leftp.add(new JLabel("Departure Date"));
		leftp.add(monthbox);
		leftp.add(datebox);
		leftp.add(yearbox);
		leftp.add(searchf);
		leftp.add(logout);

		centrep.add(scrollPane);

		rightp.add(create);
		rightp.add(passengerlab);
		rightp.add(passenger);
		rightp.add(countrylab);
		rightp.add(country);
		passengerlab.setVisible(false);
		countrylab.setVisible(false);
		passenger.setVisible(false);
		country.setVisible(false);
		rightp.add(existing);
		rightp.add(deletebooking);
		deletebooking.setVisible(false);
		rightp.add(clear);

		addListeners();
		Main.mainpanel.add(leftp);
		Main.mainpanel.add(centrep);
		Main.mainpanel.add(rightp);
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
				displayBookings();
				System.out.println("Existing");
			}
		});
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				model.clear();
			}
		});
	}

	private void createBooking(){
		try{
		getFlights();
		results.absolute(myList.getSelectedIndex()+1);
		AirlineSystem.makeBookings(passenger.getText(),Main.currentuser.getEmail(),
				results.getString("flightno1").trim(),results.getFloat("price"),
				results.getDate("dep_date").toString(),"",country.getText()); 
//		AirlineSystem.makeBookings(passenger.getText(),Main.currentuser.getEmail(),
//				results.getString("flightno2").trim(),results.getFloat("price"),
//				results.getDate("dep_date").toString(),"null");
		}catch(SQLException e){
			System.out.println("createBooking: " + e.getMessage());
		}

	}
	private void getFlights(){
		String date = datebox.getSelectedItem()+"-"+(monthbox.getSelectedIndex()+1)+"-"+yearbox.getSelectedItem();
		System.out.println(date+" "+src.getText()+" "+dest.getText());

		results = AirlineSystem.searchFlightsStandard(src.getText(),dest.getText(),date,"price asc");


	}

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

	private void displayDetailBooking(){
		//More Details about booking.
	}
	private boolean multipleConnections(){
		return false;
	}
	private void displayFlights(){
		model.clear();
		boolean areflights = false;

		try{

			while(results.next()){
				String flight1 = "Flight 1: " + results.getString("flightno1")+", ";
				String flight2 = "Flight 2: " + results.getString("flightno2") + ", ";
				String flight3;
				if(multipleConnections()){
					flight3 = "Flight 3: " + results.getString("flightno3") + ", ";
				}else{
					flight3 = "";
				}
				areflights = true;
				Integer stops = results.getInt("stops");
				String layover;
				if(stops == 0){
					layover = "";
				}else{
					layover = "Layover Time: "  
							+ String.valueOf(results.getFloat("layover")) + ", ";
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
