import java.awt.event.*;
import java.sql.SQLException;

public class SQLLoginScreen extends LoginScreen{
	
	private UserLoginScreen userlogin;
	
	public void init(){
		super.init();
		super.ulabel.setText("SQL Login");
		
		login.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e){
					 AirlineSystem flights = new AirlineSystem();
					 try {
						flights.makeConnection(user.getText(),
							 String.valueOf(pwrd.getPassword()));
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						System.err.println("WAHGSD "+e1.getMessage());
						message.setText("Invalid login!!");
						return;
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
