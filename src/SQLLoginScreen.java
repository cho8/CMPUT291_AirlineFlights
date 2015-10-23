import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SQLLoginScreen extends LoginScreen{
	
	private UserLoginScreen userlogin;
	
	public void init(){
		super.ulabel.setText("SQL Login");
		panel.add(ulabel);
		panel.add(user);
		panel.add(plabel);
		panel.add(pwrd);
		panel.add(login);
		super.login.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e){
				 try{
					 AirlineFlights flights = new AirlineFlights();
					 flights.makeConnection(user.getText(),
						 String.valueOf(pwrd.getPassword()));
				 	} catch(SQLException err) {
					 System.out.println("fail");
				 	}
				 clear();
				 userlogin = new UserLoginScreen();
				 userlogin.init();
					Main.frame.validate();
					Main.frame.repaint();
			 }
		});

	}
	
	

}
