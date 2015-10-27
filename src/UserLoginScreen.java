import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class UserLoginScreen extends LoginScreen{
	
	private UserScreen uscreen;
	
	public void init(){
		super.init();
		signup.setVisible(true);
		ulabel.setText("Email:");
		
		login.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e){
				 Main.currentuser = new User(user.getText());
				 try{
					if( Main.currentuser.isUser(user.getText()) && 
						Main.currentuser.checkPassword( user.getText(),String.valueOf(pwrd.getPassword()))){
						 clear();
						 uscreen = new UserScreen();
						 uscreen.init();
						 Main.frame.validate();
						 Main.frame.repaint();	
					 
				 	} else {
				 		message.setText("Invalid Login");
				 		message.repaint();
				 	}
				 } catch(SQLException f){
					 System.err.println("Bad things are happening " + f.getMessage());
				 }

			 }
		});
		
		signup.addActionListener(new ActionListener(){
			
			 public void actionPerformed(ActionEvent e){
				 Main.currentuser = new User(user.getText());
				 try{
					 if( Main.currentuser.isUser(user.getText()) && 
						Main.currentuser.checkPassword( user.getText(),String.valueOf(pwrd.getPassword()))){
							message.setText("User Already Exists");
					 } else {
						Main.currentuser.createNewUser(String.valueOf(pwrd.getPassword())); 
				 		clear();
				 		uscreen = new UserScreen();
				 		uscreen.init();
				 		Main.frame.validate();
				 		Main.frame.repaint();
					}
				 }catch(SQLException f){
					 System.out.println("Bad Things...So Many Bad Things...");
				 }
			}
		});
	}

}
