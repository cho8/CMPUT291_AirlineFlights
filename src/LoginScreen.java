
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class LoginScreen {
	
	/** The width of the login window */
	public static final int LOGIN_WIDTH = 300;
	/** The height of the login window */
	public static final int LOGIN_HEIGHT =200;
	/** Number of characters for each of the text input fields */
	public static final int TEXT_FIELD_SIZE = 15;
	
	protected JPanel panel = Main.mainpanel;
	JPasswordField pwrd;
	JTextField user;
	JButton login;
	JLabel ulabel;
	JLabel plabel;
	JButton signup;
	JLabel message;
	UserScreen uscreen;
	

	public LoginScreen(){
		Main.frame.setSize(LOGIN_WIDTH, LOGIN_HEIGHT);
		panel.setLayout(new GridLayout(4,3,5,5));
		Main.frame.setMinimumSize(Main.frame.getMinimumSize());

		user = new JTextField(TEXT_FIELD_SIZE);
		pwrd = new JPasswordField(TEXT_FIELD_SIZE);
		login = new JButton("Login");
		ulabel = new JLabel("Username: ");
		plabel = new JLabel("Password: ");
		signup = new JButton("Sign Up");
		message = new JLabel("");
		
	}
	
	public void init(){
		panel.add(ulabel);
		panel.add(user);
		panel.add(plabel);
		panel.add(pwrd);
		panel.add(login);
		panel.add(signup);
		signup.setVisible(false);
		panel.add(message);
		pwrd.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){

				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					login.doClick();
				}
			}
		});
		message.setFont(new Font("Sans Serif", Font.BOLD, 14));
		message.setForeground(Color.RED);
		//Subclasses must implement their own ActionListeners.
	}
	
	public void clear(){
		panel.removeAll();
		
	}

	
}
