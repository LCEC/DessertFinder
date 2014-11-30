package org.jointheleague.le.recipes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class DessertGUI implements Runnable {

	private JTextField nameTf;
	private JTextField calTf;
	private JTextField ingredientTf;
	private JList resultList;
	private ListModel resultListModel;
	private DataInterface db = new DataAccessObject();

	// private ActionListener goButtonListener = new ActionListener(){
	// @Override
	// public void actionPerformed(ActionEvent arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// };
	//
	
	private JLabel loadImageFromJavaProject(String fileName) {
		URL imageURL = getClass().getResource(fileName);
		Icon icon = new ImageIcon(imageURL);
		return new JLabel(icon);
	}

	public static void main(String[] args) {
		DessertGUI gui = new DessertGUI();
		gui.db.open();
		SwingUtilities.invokeLater(gui);
	}

	private void createGUI() {
		JFrame jframe = new JFrame("Dessert Finder");
		jframe.setLayout(new BorderLayout());

		
		//Display Panel
		JPanel displayPanel = new JPanel();
		/*JLabel imageLabel = loadImageFromJavaProject("res/original.jpg");
		displayPanel.add(imageLabel);*/
		resultList = new JList();
		resultList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		resultListModel = resultList.getModel();
		displayPanel.add(resultList);
		jframe.add(displayPanel, BorderLayout.CENTER);
		//Control Panel
		JPanel controlPanel = new JPanel();
		JLabel nameLabel = new JLabel("name");
		controlPanel.add(nameLabel);
		nameTf = new JTextField(15);
		controlPanel.add(nameTf);
		JLabel calLabel = new JLabel("calories");
		calTf = new JTextField(15);
		controlPanel.add(calLabel);
		controlPanel.add(calTf);
		JLabel ingLabel = new JLabel("ingredient");
		controlPanel.add(ingLabel);
		ingredientTf = new JTextField(15);
		controlPanel.add(ingredientTf);
		//Go Button
		JButton goButton = new JButton("Go!");
		goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				go();
			}
		});
		controlPanel.add(goButton);
		jframe.add(controlPanel, BorderLayout.SOUTH);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.pack();
		jframe.setVisible(true);
	}



	private void go() {
		// TODO Auto-generated method stub
		String name = nameTf.getText();
		String ingredient = ingredientTf.getText();
		String calString = calTf.getText();
		int cal;
		try{
		cal = Integer.parseInt(calString);
		}
		catch(NumberFormatException e ){
			cal = 1000000;
		}
		List <Recipe> result = db.searchMultiple(name, cal, ingredient);
	for (Recipe recipe : result) {
		System.out.println(recipe.getName());
		resultListModel.addElement(recipe.getName());
	}
				
	}

	@Override
	public void run() {
		createGUI();

	}

}
