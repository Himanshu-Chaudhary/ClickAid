/* Enhancements :
 * - Colors have been associated with each button. There are 4 different color used. 0, 1-3 , 4-6 , 7-9 have similar color 
 *   they differ in the contrast. For example, 9 is dark red in color while 7 is light red.
 *   
 * - Undo button has been added. Any player in the game can use the undo button to get previous values. The undo button cannot be 
 * 	 used for two successive times.
 * 
 * - CTRL+Z keystore can be used to perform undo while playing the game.
 * 
 * - Number of moves is displayed and gets updated after package is provided. High score is also displayed which is the lowest
 *   number of moves the game is completed in.
 *   
 * - Timer has been added which displays time spent on the game in Minutes: Seconds format. The time is started when the first
 * 	 aid is provided.
 * 
 * - Game Rules can be viewed by clicking the "Rules" button.  
 * 
*/

package com.putable.clickaid;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class ClickAid extends JApplet implements ActionListener {
	// declaring Row, Column and assigning default values
	int gridRow = 6, gridColumn = 4;
	int count = 0, timeCount = 0, lowestMoves = 0;
	int undoRow, undoColumn, undoCount = 0;
	// undoValues array will be used to store previous index of 5 countries who received aid
	String[] undoValues = new String[5];

	// declaring GUI elements
	JPanel startPanel = new JPanel(new GridBagLayout());
	JPanel ButtonPanel = new JPanel();
	JPanel bottomPanel = new JPanel(new GridBagLayout());
	JPanel blankPanel = new JPanel();
	JPanel blankPanel2 = new JPanel();

	JButton start = new JButton("Restart");
	JButton[][] Button = new JButton[gridRow][gridColumn];
	JButton rules = new JButton("Game Rules");
	JButton undo = new JButton("Undo");

	JLabel blank = new JLabel("  ");
	JLabel time = new JLabel();
	JLabel moves = new JLabel();
	JLabel highScore = new JLabel("High Score" + ": " + lowestMoves + " moves");

	GridBagConstraints c = new GridBagConstraints();
	Color[] color = new Color[10];

	// creating double dimensional array with row and columns increment for
	// surrounding values
	int[][] surroundingLocation = { { 0, 1 }, { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 0 } };
	int[][] surroundingLocation2 = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

	Timer timer = new Timer(1000, new MyListener());

	@Override
	public void init() {
		getRowsandColumns();
		generateLayout();
		initializeCountries();
	}

	void getRowsandColumns() {
		// taking values from the html file and using try,catch to set default
		// and minimum values
		try {
			gridRow = Integer.parseInt(getParameter("ROWS"));
			gridColumn = Integer.parseInt(getParameter("COLUMNS"));
			if (gridRow < 3 || gridColumn < 3) {
				gridRow = 3;
				gridColumn = 3;
			}
		} catch (NumberFormatException n) {
			// setting default values
			gridRow = 6;
			gridColumn = 4;
		}
		// re-initialing elements with the values supplied in dimly
		ButtonPanel = new JPanel(new GridLayout(gridRow, gridColumn));
		Button = new JButton[gridRow][gridColumn];
		// calling methods to perform mentioned tasks
	}

	void generateLayout() {
		// setting size of window according to rows and columns
		this.setSize(gridColumn * 80, gridRow * 80);
		// displaying time in minutes:seconds format
		time.setText("Time Passed : " + (timeCount / 60) + ":" + (timeCount % 60));
		moves.setText("Moves" + ": " + count);

		blankPanel.add(blank);
		blankPanel2.add(blank);
		ButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 5));

		// adding elements to startpanel and bottompanel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.insets = new Insets(0, 20, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		startPanel.add(undo, c);
		bottomPanel.add(highScore, c);
		c.insets = new Insets(0, 5, 5, 10);
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 20, 5, 10);
		startPanel.add(start, c);
		c.weighty = 0;
		bottomPanel.add(rules, c);
		c.gridx = 0;
		c.gridy = 1;
		startPanel.add(time, c);
		c.gridx = 1;
		c.gridy = 1;
		startPanel.add(moves, c);
		ButtonPanel.setSize(gridRow, gridColumn);

		// adding action listeners
		start.addActionListener(this);
		rules.addActionListener(this);
		undo.addActionListener(this);
		undo.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		// adding panels to Border Layout
		this.add(ButtonPanel, BorderLayout.CENTER);
		this.add(startPanel, BorderLayout.NORTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.add(blankPanel, BorderLayout.WEST);
		this.add(blankPanel2, BorderLayout.EAST);
		ButtonPanel.grabFocus();
	}

	void initializeCountries() {
		generateColors();
		for (int i = 0; i < gridRow; i++) {
			for (int j = 0; j < gridColumn; j++) {
				// generates initial values and applied defualt settings to each
				// button
				Button[i][j] = new JButton();
				Button[i][j].setText(Integer.toString((i + j) % 10));
				Button[i][j].setBackground(color[Integer.parseInt(Button[i][j].getText())]);
				ButtonPanel.add(Button[i][j]);
				Button[i][j].addActionListener(this);

			}
		}
	}

	// actionlistner methos
	@Override
	public void actionPerformed(ActionEvent e) {

		JButton who = (JButton) e.getSource();
		// if restart is pressed
		if (who == start) {
			newGame();
		} else if (who == rules) {
			// displays game tules
			JOptionPane.showMessageDialog(startPanel,
					" The buttons are counties and the numbers from 0-9 is a representation \n"
							+ " of misery index of the country. Clicking a country provides a aid package \n"
							+ " decresing the misery index of the country and its neighbours by 1 but \n"
							+ "providing an aid package to a country with 0 index will backfire and its  \n"
							+ "misery index will be increased to number of non zero countries of its \n"
							+ " neighbours");
		} else if (who == undo) {
			undoValues(undoRow, undoColumn);
		} else
		// if buttons are pressed
		{
			undoCount++;
			count++;
			moves.setText("Moves" + ": " + count);
			if (timeCount == 0)
				timer.start();
			int r = 0, c = 0;
			for (int i = 0; i < gridRow; i++) {

				for (int j = 0; j < gridColumn; j++) {
					if (Button[i][j] == who) {
						r = i;
						c = j;
						break;
					}
				}
			}
			// stores rows and columns of the recently clicked country
			undoRow = r;
			undoColumn = c;
			providepackage(r, c);
		}
		if (checkedSolved()) {
			// checks if the game is solved
			JOptionPane.showMessageDialog(startPanel, "Congratulaions!!! You Solved this Puzzle");
			newGame();
		}
	}

	void providepackage(int row, int column) {
		int temprow, tempcolumn;
		// the aid backfires if the misery index is 0
		if (Integer.parseInt(Button[row][column].getText()) == 0) {
			int tempint = checkneighbours(row, column);
			Button[row][column].setText(Integer.toString(tempint));
			Button[row][column].setBackground(color[tempint]);
		} else {
			for (int i = 0; i < 5; i++) {
				String temp = null;
				int tempint = 0;
				// performs wraparound then changes the misery-index
				temprow = (row != 0) ? (row + surroundingLocation[i][0]) % gridRow
						: (gridRow + surroundingLocation[i][0]) % gridRow;
				tempcolumn = (column != 0) ? (column + surroundingLocation[i][1]) % gridColumn
						: (gridColumn + surroundingLocation[i][1]) % gridColumn;
				temp = Button[temprow][tempcolumn].getText();
				undoValues[i] = temp;
				tempint = Integer.parseInt(temp);
				// reduces by 1 if value is non-zero or increases the misery
				// index to non-zero surrounding countries
				tempint = (tempint == 0) ? checkneighbours(temprow, tempcolumn) : Integer.parseInt(temp) - 1;
				Button[temprow][tempcolumn].setBackground(color[tempint]);
				Button[temprow][tempcolumn].setText(Integer.toString(tempint));
			}
		}
	}

	int checkneighbours(int row, int column) {
		int count = 0, temprow, tempcolumn;
		for (int i = 0; i < 4; i++) {
			// performs wraparound
			temprow = (row != 0) ? (row + surroundingLocation2[i][0]) % gridRow
					: (gridRow + surroundingLocation2[i][0]) % gridRow;
			tempcolumn = (column != 0) ? (column + surroundingLocation2[i][1]) % gridColumn
					: (gridColumn + surroundingLocation2[i][1]) % gridColumn;
			if (Integer.parseInt(Button[temprow][tempcolumn].getText()) != 0)
				count++;
		}
		return count;
	}

	void generateColors() {
		// assigning color values to each misery index.
		color[0] = Color.white;
		color[1] = new Color(102, 255, 102);
		color[2] = new Color(0, 255, 0);
		color[3] = new Color(0, 204, 0);
		color[4] = new Color(51, 153, 255);
		color[5] = new Color(0, 128, 255);
		color[6] = new Color(0, 102, 204);
		color[7] = new Color(251, 51, 51);
		color[8] = new Color(251, 0, 0);
		color[9] = new Color(204, 0, 0);
	}

	private class MyListener implements ActionListener {

		public void actionPerformed(ActionEvent e) { // Timer ticked

			timeCount++;
			time.setText("Time Passed : " + (timeCount / 60) + ":" + (timeCount % 60));

		}

	}

	boolean checkedSolved() {
		// loops through the buttons and checks for 0
		for (int i = 0; i < Button.length; i++) {
			for (int j = 0; j < Button[0].length; j++) {
				// returns false if non-zero country is found
				if (!(Button[i][j].getText().equals("0")))
					return false;
			}
		}
		// sets high score if no non-zero country is found 
		lowestMoves = count;
		highScore = new JLabel("High Score" + ": " + lowestMoves + " moves");
		return true;
	}

	void newGame() {
		// starts a new game and initializes all variables
		timeCount = 0;
		count = 0;
		undoCount = 0;
		timer.stop();
		time.setText("Time Passed : " + (timeCount / 60) + ":" + (timeCount % 60));
		moves.setText("Moves" + ": " + count);
		// initializes misery index of all countires
		for (int i = 0; i < gridRow; i++) {
			for (int j = 0; j < gridColumn; j++) {
				Button[i][j].setText(Integer.toString((i + j) % 10));
				Button[i][j].setBackground(color[(i + j) % 10]);

			}
		}
	}

	void undoValues(int row, int column) {
		// loops through 5 elements who recently got and aid
		if (count == 0) {
			JOptionPane.showMessageDialog(startPanel, "Undo Cannot be used before providing any package");
		} else if (undoCount == 0) {
			JOptionPane.showMessageDialog(startPanel, "Undo Cannot be used for two Succesive times");
		} else {
			int temprow, tempcolumn;
			for (int i = 0; i < 5; i++) {
				temprow = (row != 0) ? (row + surroundingLocation[i][0]) % gridRow
						: (gridRow + surroundingLocation[i][0]) % gridRow;
				tempcolumn = (column != 0) ? (column + surroundingLocation[i][1]) % gridColumn
						: (gridColumn + surroundingLocation[i][1]) % gridColumn;
				// changes color and misery index to previous values
				Button[temprow][tempcolumn].setText(undoValues[i]);
				Button[temprow][tempcolumn].setBackground(color[Integer.parseInt(undoValues[i])]);
			}
			undoCount = 0;
			count--;
			moves.setText("Moves" + ": " + count);
		}
	
	}

}
