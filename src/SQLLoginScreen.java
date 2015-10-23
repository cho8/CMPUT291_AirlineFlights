import java.awt.event.*;

public class SQLLoginScreen extends LoginScreen{
	
	private UserLoginScreen userlogin;
	
	public void init(){
		super.ulabel.setText("SQL Login");
		panel.add(ulabel);
		panel.add(user);
		panel.add(plabel);
		panel.add(pwrd);
		panel.add(login);
		pwrd.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				System.out.println("Some key pressed");
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					login.doClick();
				}
			}
		});
		
		super.login.addActionListener(new ActionListener(){
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
