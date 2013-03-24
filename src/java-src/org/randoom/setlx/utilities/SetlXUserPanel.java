package org.randoom.setlx.utilities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SetlXUserPanel extends JPanel {

	private static final GridBagConstraints messageLabelConstraints 
	= new GridBagConstraints( 0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0  );
	
	private static final GridBagConstraints inputFieldConstraints 
	= new GridBagConstraints( 1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0,0  );
	
	private static final GridBagConstraints inputDescriptionConstraints 
	= new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0  );
	
	private static SetlXUserPanel panel;
	
	private JLabel messageLabel;
	
	private JLabel inputDescription;
	
	private JTextField inputField;
	
	private SetlXUserPanel(){
		super();
		setVisible(false);
		setLayout( new GridBagLayout() );
		messageLabel = new JLabel();
		inputDescription = new JLabel();
		inputField = new JTextField();
		messageLabel.setVisible(false);
		inputDescription.setVisible(false);
		inputField.setVisible(false);
		add(messageLabel,messageLabelConstraints);
		add(inputDescription,inputDescriptionConstraints);
		add(inputField,inputFieldConstraints);
	}
	
	public void addInput( String description ){
		inputDescription.setText(description);
		inputDescription.setVisible(true);
		inputField.setVisible(true);
		setVisible(true);
		StdDraw.pack();
	}
	
	public void setMessage( String message ){
		messageLabel.setVisible(true);
		messageLabel.setText(message);
		setVisible(true);
		StdDraw.pack();
	}
	
	public String getInput(){
		return inputField.getText();
	}
	
	public static SetlXUserPanel getInstance(){
		if ( panel == null ){
			panel = new SetlXUserPanel();
		}
		return panel;
	}
}
