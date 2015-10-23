import javax.swing.*;


public class Main{


	public static SQLLoginScreen sqllogin;
	public static JFrame frame;
	public static JPanel mainpanel;
	//public static User currentuser;

	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				init();
			}
		});
	}
	
	private static void init(){
		frame = new JFrame("Airport Bookings");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		mainpanel = new JPanel();
		frame.add(mainpanel);		
		sqllogin = new SQLLoginScreen();
		sqllogin.init();
	}
    

}
