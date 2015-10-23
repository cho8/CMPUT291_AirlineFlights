import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserScreen{
	public static final int APPLICATION_WIDTH = 750;

	/** The height of the application window */
	public static final int APPLICATION_HEIGHT = 400;
	public static final int TEXT_FIELD_SIZE = 15;
	private static final String[] months = {"January","February","March","April",
			"May","June","July","August","September","October","November","December"};
	
	private UserLoginScreen lscreen;
	private static JComboBox<String> monthbox;
	private static JComboBox<Integer> datebox;
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
		Main.frame.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		Main.mainpanel.setLayout(new GridLayout(1,3,5,5));
		
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
				 getFlights();
				 displayFlights();
			 }
		});
		logout.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e){
				 Main.currentuser.logout();
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
						//Try to create booking
					}

				 System.out.println(myList.getSelectedValue());
			 }
		});
		existing.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e){
				 	model.clear();
					passengerlab.setVisible(false);
					countrylab.setVisible(false);
				 	passenger.setVisible(false);
					country.setVisible(false);
					deletebooking.setVisible(true);
				 System.out.println("Existing");
			 }
		});
		clear.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e){
				 model.clear();
			 }
		});
	}
	
	private void getFlights(){
		String date = yearbox.getSelectedItem()+"-"+(monthbox.getSelectedIndex()+1)+"-"+datebox.getSelectedIndex();
		System.out.println(date);
		//results = searchFlights(src.getText(),dest.getText(),date);
		
	}
	
	private boolean multipleConnections(){
		return false;
	}
	private void displayFlights(){
		
		try{
		String flight1 = results.getString("flightno1");
		String flight2 = results.getString("flightno2");
		String flight3;
		if(multipleConnections()){
			flight3 = results.getString("flightno3");
		}else{
			flight3 = "";
		}
		
		while(results.next()){
			Integer stops = results.getInt("stops");
			String layover;
			if(stops == 0){
				layover = "";
			}else{
				layover = String.valueOf(results.getFloat("layover"));
			}
			String resultitem = results.getString("src")+","
					+results.getString("dest")+","
					+flight1 + flight2 + flight3
					+stops+layover+results.getFloat("price")
					+results.getDate("dep_time").toString()
					+results.getDate("arr_time").toString()
					+results.getInt("seats");
			model.addElement(resultitem);
		}
		} catch(SQLException e){
			System.out.println("Can't get Flights");
		}
	}
	

}
