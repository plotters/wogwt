package wogwt.tool;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WOGWTToolMain extends JFrame {

	public static void main(String[] args) {
		WOGWTToolMain frame = new WOGWTToolMain();
		frame.setTitle("WOGWT Tool");
		frame.pack();
		frame.setSize(frame.getWidth() + 50, frame.getHeight() + 50);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public WOGWTToolMain() {
		super();
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints mainConstraints = new GridBagConstraints();
		mainConstraints.gridx = 0;
		mainConstraints.anchor = GridBagConstraints.WEST;
		
		JLabel label = new JLabel("<html>WOGWT Tool - this will add WOGWT to an existing WO application.<br><br>" +
				"Before you run this app you should do these things:<br><br>" +
				"Step 1: Download and install WOGWT.framework into /Library/Frameworks/<br>" +
				"You must install this even if you plan on using the WOGWT source.<br><br>" +
				"Step 2: Install the Google Eclipse Plugin by following their instructions at:<br>" +
				"&nbsp;&nbsp;&nbsp;&nbsp;http://code.google.com/eclipse <br>" +
				"Configure the GWT SDK location in eclipse preferences to point to the folder <br>" +
				"&nbsp;&nbsp;&nbsp;&nbsp;/Library/Frameworks/WOGWT.framework/Resources/Java/<br><br>" + 
				"Step 3: Commit or backup your project<br><br>" +
				"Step 4: Add the frameworks \"WOGWT\" and \"JavaWOJSPServlet\" to your project's build path.<br><br>" +
				"Step 5: Enter your project directory and click the button below.<br><br></html>");
		this.add(label, mainConstraints);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		this.add(topPanel, mainConstraints);
		
		JLabel pathLabel = new JLabel("Project folder: ");
		topPanel.add(pathLabel);
		
		final JTextField projectPath = new JTextField(30);
		topPanel.add(projectPath);
		
		final JFileChooser dialog = new JFileChooser();
		dialog.setDialogTitle("Select the project directory");
		dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = dialog.showOpenDialog(WOGWTToolMain.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					projectPath.setText(dialog.getSelectedFile().getAbsolutePath());
				}
			}
		});
		topPanel.add(browseButton);
		
		
		JButton runButton = new JButton("Add WOGWT to Project");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ProjectCreator.main(new String[] {projectPath.getText()});
					JOptionPane.showMessageDialog(WOGWTToolMain.this, "WOGWT has been added to your project. Continue with step 6 below.");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
				
		mainConstraints.anchor = GridBagConstraints.CENTER;
		this.add(runButton, mainConstraints);
		
		JLabel bottomLabel = new JLabel("<html><br>" +
			"Step 6: In your project properties go to Google->\"Web Toolkit\".<br>" +
			"Check the box that says \"Use Google Web Toolkit\".<br>" +
			"You must do this AFTER clicking the button above.<br><br>" +
			"Step 7: Launch your project in GWT Development Mode by running the generated <br>" +
			"&lt;ProjectName&gt;_GWTDevMode.launch file in your project folder.<br><br>" +
			"Step 8: In the \"Development Mode\" tab in Eclipse copy the url displayed and paste it into the browser. The URL is like:<br>" +
			"http://localhost:8888/MyApp/WebObjects/MyApp.woa?gwt.codesvr=127.0.0.1:9997<br>" +
			"Step 9: In your EOModel, you need to set the Client Class Name for any Entities that want to transfer to/from the client.<br>" +
			"The classes need to live inside the gwt client package or in a subpackage.</html>");
		mainConstraints.anchor = GridBagConstraints.WEST;
		this.add(bottomLabel, mainConstraints);
	}
}
