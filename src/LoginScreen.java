
import javax.swing.*;
import java.awt.*;

public abstract class LoginScreen {
	
	/** The width of the application window */
	public static final int LOGIN_WIDTH = 300;

	/** The height of the application window */
	public static final int LOGIN_HEIGHT =200;
	/** Number of characters for each of the text input fields */
	public static final int TEXT_FIELD_SIZE = 15;
	
	protected JPanel panel = Main.mainpanel;
	JPasswordField pwrd;
	JTextField user;
	JButton login;
	JButton signup;
	JLabel ulabel;
	JLabel plabel;
	UserScreen uscreen;
	

	public LoginScreen(){
		Main.frame.setSize(LOGIN_WIDTH, LOGIN_HEIGHT);
		panel.setLayout(new GridLayout(4,3,5,5));
		Main.frame.setMinimumSize(Main.frame.getMinimumSize());

		user = new JTextField(TEXT_FIELD_SIZE);
		pwrd = new JPasswordField(TEXT_FIELD_SIZE);
		login = new JButton("Login");
		signup = new JButton("Sign Up");
		ulabel = new JLabel("Username: ");
		plabel = new JLabel("Password: ");
		
	}
	
	public void init(){

	}
	
	public void clear(){
		panel.removeAll();
		
	}

	
}
