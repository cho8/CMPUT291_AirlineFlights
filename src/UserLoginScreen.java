import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

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
				 	}
				 } catch(SQLException f){
					 System.err.println("Bad things are happening " + f.getMessage());
				 }

			 }
		});
		
		super.signup.addActionListener(new ActionListener(){
			
			 public void actionPerformed(ActionEvent e){
				 
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
