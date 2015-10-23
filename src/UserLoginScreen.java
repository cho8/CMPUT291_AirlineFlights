import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class UserLoginScreen extends LoginScreen{
	
private UserScreen uscreen;
private JLabel message = new JLabel("");
	
	public void init(){
		super.ulabel.setText("Email:");
		panel.add(ulabel);
		panel.add(user);
		panel.add(plabel);
		panel.add(pwrd);
		panel.add(login);
		panel.add(signup);
		panel.add(message);
		message.setFont(new Font("Sans Serif", Font.BOLD, 14));
		message.setForeground(Color.RED);
		super.login.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e){
//				 if(User.isUser(user.getText() && 
//						 User.checkPassword(user.getText(),String.valueOf(pwrd.getPassword())))){
//					 
//					 	Main.currentuser = new User(user.getText());
//				 clear();
//				 uscreen = new UserScreen();
//				 uscreen.init();
//					Main.frame.validate();
//					Main.frame.repaint();
//					 
//				 } else {
					 message.setText("Invalid Login");
//				 }

			 }
		});
		super.signup.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e){
//				 if(User.isUser(user.getText() && 
//				 User.checkPassword(user.getText(),String.valueOf(pwrd.getPassword())))){
				 message.setText("User Already Exists");
//		 } else {		
				 clear();
				 uscreen = new UserScreen();
				 uscreen.init();
					Main.frame.validate();
					Main.frame.repaint();
			 }
		});

	}

}
