import java.awt.event.*;

public class SQLLoginScreen extends LoginScreen{
	
	private UserLoginScreen userlogin;
	
	public void init(){
		super.init();
		super.ulabel.setText("SQL Login");
		
		login.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e){
					 AirlineSystem flights = new AirlineSystem();
					 flights.makeConnection(user.getText(),
						 String.valueOf(pwrd.getPassword()));

				 clear();
				 userlogin = new UserLoginScreen();
				 userlogin.init();
					Main.frame.validate();
					Main.frame.repaint();
			 }
		});

	}
	
	

}
