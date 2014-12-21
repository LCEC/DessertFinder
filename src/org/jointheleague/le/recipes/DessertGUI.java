package org.jointheleague.le.recipes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DessertGUI implements Runnable {
	private JFrame jframe = new JFrame("Dessert Finder");
	private JTextField nameTf;
	private JTextField calTf;
	private JTextField ingredientTf;
	private JList resultList;
	private DefaultListModel resultListModel = new DefaultListModel();
	private DataInterface db = new DataAccessObject();
	private static final Logger LOGGER = Logger
			.getLogger(DataAccessObject.class.getCanonicalName());
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
		JLabel l = new JLabel(icon);
		l.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		return l;
	}

	public static void main(String[] args) {
		DessertGUI gui = new DessertGUI();
		gui.db.open();
		LOGGER.log(Level.INFO,"Loading CSV file ...");
		try {
			new CsvLoader(gui.db).loadRecipes();
		} catch (URISyntaxException e) {
			LOGGER.log(Level.SEVERE, "Loading CSV file failed.");
		}
		SwingUtilities.invokeLater(gui);
	}

	private void createGUI() {
		jframe.setLayout(new BorderLayout());

		// Display Panel
		JLayeredPane displayPanel = new JLayeredPane();
		displayPanel.setPreferredSize(new Dimension(900, 600));
		JLabel imageLabel = loadImageFromJavaProject("res/original.jpg");
		displayPanel.add(imageLabel);
		JScrollPane resultPane = buildResultPane();
		displayPanel.add(resultPane);
		jframe.add(displayPanel, BorderLayout.CENTER);
		// Control Panel
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
		// Go Button
		JButton goButton = new JButton("Go!");
		goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				go();
			}
		});
		controlPanel.add(goButton);
		jframe.add(controlPanel, BorderLayout.SOUTH);
		jframe.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				db.close();
			}

		});
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.pack();
		jframe.setResizable(false);
		jframe.setVisible(true);
	}

	private JScrollPane buildResultPane() {
		JScrollPane scrollPane = new JScrollPane();
		resultList = new JList();
		// scrollPane.setPreferredSize(new Dimension(200, 200));
		resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultList.setToolTipText("Click to see recipe");
		resultList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				resultListValueChanged(evt);
			}
		});
		resultList.setModel(resultListModel);
		scrollPane.setViewportView(resultList);
		scrollPane.setBounds(300, 50, 300, 500);
		resultList.setFixedCellHeight(40);
		return scrollPane;
	}

	private void resultListValueChanged(ListSelectionEvent evt) {
		if (evt.getValueIsAdjusting()) {
			return;
		}
		String entry = (String) resultList.getSelectedValue();
		if(entry!=null){
		List<Recipe> result = db.searchByName(entry);
		Recipe recipe = result.get(0);
		createRecipeDialog(recipe).setVisible(true);
		}
		
	}

	private JDialog createRecipeDialog(Recipe recipe) {
		JDialog dialog = new JDialog(jframe);
		dialog.setTitle(recipe.getName());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setModal(false);
		dialog.setLayout(new BorderLayout());
		JPanel jp = new JPanel();
		dialog.add(jp,BorderLayout.NORTH);
		//JLabels
		JLabel cal = new JLabel("Calories: "+recipe.getCalories()+"  ");
		JLabel preptime = new JLabel("Prep Time: "+recipe.getPrepTime()+"  ");
		JLabel cooktime = new JLabel("Cook Time: "+recipe.getCookTime()+"  ");
		JLabel rating = new JLabel("Rating: "+recipe.getRating()+"  ");
		JLabel difficulty = new JLabel("Difficulty: "+recipe.getDifficulty()+"  ");
		jp.add(cal);
		jp.add(preptime);
		jp.add(cooktime);
		jp.add(rating);
		jp.add(difficulty);
		//Text Area
		JTextArea ingText = new JTextArea();
		ingText.setText(recipe.getIngredients());
		ingText.setEditable(false);
		ingText.setLineWrap(true);
		ingText.setWrapStyleWord(true);
		ingText.setPreferredSize(new Dimension(200,500));
		JTextArea instText = new JTextArea();
		instText.setText(recipe.getInstructions());
		instText.setEditable(false);
		instText.setLineWrap(true);
		instText.setWrapStyleWord(true);
		//Scroll Panes
		JScrollPane ingredients = new JScrollPane(ingText);
		ingredients.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		ingredients.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane instructions = new JScrollPane(instText);
		instructions.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		instructions.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//Split Pane
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,ingredients,instructions);
		jsp.setPreferredSize(new Dimension(500,500));
		dialog.add(jsp,BorderLayout.CENTER);
		jsp.setDividerLocation(0.33);
		dialog.pack();
		
		
		
		
		
		
		
		return dialog;
	}

	private void go() {
		// TODO Auto-generated method stub
		String name = nameTf.getText();
		String ingredient = ingredientTf.getText();
		String calString = calTf.getText();
		int cal;
		try {
			cal = Integer.parseInt(calString);
		} catch (NumberFormatException e) {
			cal = 1000000;
		}
		List<Recipe> result = db.searchMultiple(name, cal, ingredient);
		resultListModel.clear();
		for (Recipe recipe : result) {
			resultListModel.addElement(recipe.getName());
		}

	}

	@Override
	public void run() {
		createGUI();

	}

}
