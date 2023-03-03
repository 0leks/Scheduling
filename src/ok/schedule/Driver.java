package ok.schedule;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import ok.launcher.Updater;
import ok.schedule.model.Day;
import ok.schedule.model.Employee;
import ok.schedule.model.EmployeeRoster;
import ok.schedule.model.MyCalendar;

import static ok.schedule.ViewPanel.*;

public class Driver {
	
	private static final String projectName = "scheduling";

	public static final int BIG_NUMBER = 99999;
  private JFrame frame;

  private JPanel mainPanel;
  private ViewPanel viewPanel;
  private JPanel buttonPanel;

  private JLabel monthLabel;
  private JPanel timeframe;
  private JButton generateButton;
  private JButton save;
  private JCheckBox numberedOrBulletPositions;
  private JButton editEmployees;
  private EmployeeView employeeView;

  private MyCalendar calendar = new MyCalendar();
  private EmployeeRoster roster = new EmployeeRoster();
  private int monthNumber;
  private String monthName;
  private int year;
  
  private boolean customEdits = false;

  private int monthOffset = 0;
  
  	private void checkForUpdates() {
  		Thread updateThread = new Thread(() -> new Updater(projectName).update());
  		updateThread.start();
  	}

	public void run() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Preferences.readEmployees(roster);
		initializeFrame();
		checkForUpdates();
		initializePanels();
		
		employeeView = new EmployeeView(roster, frame, () -> switchtoMainPanel());
	  setUpCalendar();
	    
		frame.setVisible(true);
		frame.validate();
	}

	private void initializeFrame() {
		frame = new JFrame("Scheduling 2.0.7");
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension appsize = new Dimension(screensize.width * 9 / 10, screensize.height * 9 / 10);
		frame.setSize(appsize);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame = null;
			}
			@Override
			public void windowClosed(WindowEvent e) {
				frame = null;
			}
		});
	    URL iconURL = Driver.class.getResource("/icon.png");
	    if( iconURL != null ) {
	      ImageIcon icon = new ImageIcon(iconURL);
	      frame.setIconImage(icon.getImage());
	    }
	    frame.setLayout(new BorderLayout());
	}
	
	private void initializePanels() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		mainPanel.setBackground(COLOR_BACKGROUND);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(Constants.BUTTON_PADDING, Constants.BUTTON_PADDING, Constants.BUTTON_PADDING, Constants.BUTTON_PADDING));
		mainPanel.add(Box.createHorizontalGlue());

		buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, Constants.BUTTON_PADDING));

		viewPanel = new ViewPanel(calendar);
		viewPanel.setPreferredSize(new Dimension(BIG_NUMBER, BIG_NUMBER));
		viewPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				viewPanelMousePressed(e);
			}
		});
		mainPanel.add(Box.createHorizontalGlue());
		mainPanel.add(buttonPanel);
		mainPanel.add(viewPanel);

    monthLabel = new JLabel("asdf", SwingConstants.CENTER);
    monthLabel.setFont(MAIN_FONT);
    monthLabel.setMaximumSize(new Dimension(BIG_NUMBER, 0));
    monthLabel.setFocusable(false);
    
		timeframe = new JPanel();
		timeframe.setOpaque(false);
		timeframe.setLayout(new BoxLayout(timeframe, BoxLayout.LINE_AXIS));
		JButton leftShift = new JButton("<<<");
		JButton rightShift = new JButton(">>>");
		leftShift.setFont(MAIN_FONT);
		rightShift.setFont(MAIN_FONT);
		leftShift.addActionListener(e -> shiftMonth(-1));
		rightShift.addActionListener(e -> shiftMonth(1));
		leftShift.setFocusable(false);
		rightShift.setFocusable(false);
		timeframe.add(leftShift);
		timeframe.add(Box.createRigidArea(new Dimension(Constants.BUTTON_PADDING, 0)));
		timeframe.add(rightShift);

    editEmployees = new JButton("Edit Employees", Utils.loadImageIconResource("/edit_employee_icon.png", 32, 32));
    editEmployees.setFont(MAIN_FONT);
    editEmployees.setMaximumSize(new Dimension(BIG_NUMBER, 0));
    editEmployees.addActionListener(e -> switchtoEditPanel());
    editEmployees.setFocusable(false);
    generateButton = new JButton("Generate");
    generateButton.setFont(MAIN_FONT);
    generateButton.setMaximumSize(new Dimension(BIG_NUMBER, 0));
    generateButton.addActionListener(e -> generateButtonPressed());
    generateButton.setFocusable(false);

    save = new JButton("Save", Utils.loadImageIconResource("/save_icon.png", 32, 32));
    save.setFont(MAIN_FONT);
    save.setMaximumSize(new Dimension(BIG_NUMBER, 0));
    save.addActionListener(e -> writeToFile());
    save.setFocusable(false);
    
    numberedOrBulletPositions = new JCheckBox("Numbered Positions");
    numberedOrBulletPositions.setFont(MAIN_FONT);
    numberedOrBulletPositions.setMaximumSize(new Dimension(BIG_NUMBER, 0));
    numberedOrBulletPositions.addActionListener(e -> roster.useNumberedPositions = numberedOrBulletPositions.isSelected());
    numberedOrBulletPositions.setFocusable(false);
    numberedOrBulletPositions.setSelected(roster.useNumberedPositions);
    
    buttonPanel.add(Box.createVerticalGlue());
    buttonPanel.add(monthLabel);
    buttonPanel.add(Box.createRigidArea(new Dimension(0, Constants.BUTTON_PADDING)));
		buttonPanel.add(timeframe);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, Constants.BUTTON_PADDING)));
    buttonPanel.add(editEmployees);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, Constants.BUTTON_PADDING)));
    buttonPanel.add(generateButton);
    buttonPanel.add(Box.createRigidArea(new Dimension(0, Constants.BUTTON_PADDING)));
    buttonPanel.add(numberedOrBulletPositions);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, Constants.BUTTON_PADDING)));
    buttonPanel.add(save);
    buttonPanel.add(Box.createVerticalGlue());
	    
	    for(Component c : buttonPanel.getComponents()) {
	    	if(c instanceof JButton) {
	    		((JButton)c).setAlignmentX(Component.CENTER_ALIGNMENT);
	    	}
	    	else if(c instanceof JLabel) {
	    	  ((JLabel)c).setAlignmentX(Component.CENTER_ALIGNMENT);
	    	}
	    	else if (c instanceof JCheckBox) {
	    		((JCheckBox)c).setAlignmentX(Component.CENTER_ALIGNMENT);
	    	}
	    }
	    frame.add(mainPanel, BorderLayout.CENTER);
	}

	private void generateButtonPressed() {
		Preferences.writeEmployees(roster);
		if(!loseChangesConfirmPrompt()) {
			return;
		}
		Assigner assigner = new Assigner();
		Day[][] newDays = assigner.generateSchedule(calendar.days, roster.employees);
		if (calendar.days != null) {
		  calendar.days = newDays;
			customEdits = false;
		}
		frame.repaint();
	}
	private void shiftMonth(int delta) {
		if(!loseChangesConfirmPrompt()) {
			return;
		}
		monthOffset += delta;
		setUpCalendar();
	}
	
	/** @return true if user okays losing the changes 
	 * or there are no custom edits. Otherwise false. */
	private boolean loseChangesConfirmPrompt() {
		if(!customEdits) {
			return true;
		}
		int response = JOptionPane.showConfirmDialog(
                     frame, 
                     "You have made some custom edits.\n" +
                     "Are you sure you would like to overwrite your changes?",
                     "Discard changes?",
                     JOptionPane.OK_CANCEL_OPTION);
		return response == JOptionPane.OK_OPTION;
	}

  public void switchtoEditPanel() {
    frame.remove(mainPanel);
    frame.add(employeeView.editPanel, BorderLayout.CENTER);
    frame.validate();
    frame.repaint();
  }

  public void switchtoMainPanel() {
    
    Preferences.writeEmployees(roster);
    
    frame.remove(employeeView.editPanel);
    frame.add(mainPanel, BorderLayout.CENTER);
    frame.validate();
    frame.repaint();
  }

  public void setUpCalendar() {
    int maxNumberWeeks = 5;
    calendar.days = new Day[maxNumberWeeks][5]; // TODO

    Calendar cal = Calendar.getInstance();
    // set day of month to 1
    // if current day of month is 31 and next month only has 30 days, 
    // Calendar will automatically roll over to next month.
    cal.set( Calendar.DAY_OF_MONTH, 1);
    
    int targetMonth = cal.get(Calendar.MONTH) + 1 + monthOffset;
    cal.set( Calendar.MONTH, targetMonth );
    
    monthNumber = cal.get(Calendar.MONTH);
    monthName = Utils.getNameofMonth(monthNumber);
    year = cal.get(Calendar.YEAR);
    
    while( cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ) {
      cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1);
    }
    while( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ) {
      cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)-1);
    }
    
    int numWeeks = 0;
    for (int week = 0; week < calendar.days.length; week++) {
      int currentMonth = cal.get(Calendar.MONTH);
      boolean added = false;
      if( currentMonth == monthNumber ) {
        numWeeks++;
        added = true;
      }
      for (int day = 0; day < calendar.days[week].length; day++) {
        int currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);
        
        Day newDay = new Day(currentDayOfWeek-2, currentDayOfMonth, currentDayOfYear);
        newDay.setMonth(currentMonth);
        newDay.setYear(currentYear);
        if( currentMonth != monthNumber ) {
          newDay.setUnused(true);
        }
        calendar.days[week][day] = newDay;
        
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1);
        while( cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ) {
          cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1);
        }
      }
      if( currentMonth == monthNumber && !added) {
        numWeeks++;
      }
    }
    
    Day[][] newDays = new Day[numWeeks][5];
    for(int week = 0; week < newDays.length; week++ ) {
      for( int day = 0; day < newDays[week].length; day++ ) {
        newDays[week][day] = calendar.days[week][day];
      }
    }
    calendar.days = newDays;
    customEdits = false;
    
    WebsiteScraper.querySchoolCalendar(calendar, year, monthNumber + 1);
    monthLabel.setText(monthName + " " + year);
    generateButtonPressed();
    frame.repaint();
  }

	public void writeToFile() {
		Preferences.writeEmployees(roster);
		String fileName = "Mohr_" + year + "_" + monthName + "_Schedule";
		fileName = JOptionPane.showInputDialog(frame, "Choose File Name", fileName);
		if (fileName == null) {
			return;
		}
		
		fileName = fileName + ".html";
		boolean useOrderedList = numberedOrBulletPositions.isSelected();
		boolean success = HtmlWriter.writeToFile(fileName, year, monthName, calendar.days, useOrderedList);
		if(success) {
			JOptionPane.showMessageDialog(frame, "Saved " + fileName);
		}
		else {
			JOptionPane.showMessageDialog(frame, "Failed to save to " + fileName, "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
//	  Utils.makeSampleFonts();
//	  new TestAssignment(null);
		new Driver().run();
	}

	private void viewPanelMousePressed(MouseEvent e) {
      int daypressed = e.getX() / viewPanel.getCellWidth();
      int weekpressed = e.getY() / viewPanel.getCellHeight();
      if( weekpressed < 0 || weekpressed >= calendar.days.length ) {
        return;
      }
      Day day = calendar.days[weekpressed][daypressed];
      if(day.isUnused()) {
    	  return;
      }
      EditDayPanel editPanel = new EditDayPanel(day);
      
      Object[] options = {};
      int choice = JOptionPane.showOptionDialog(frame, editPanel, "Editing " + day.getMonth() + " " + day.getOfficialDate(), JOptionPane.OK_OPTION,
          JOptionPane.INFORMATION_MESSAGE, null, options, null);
      customEdits = true;
      String[] newAssignments = editPanel.getAssignments();
      calendar.days[weekpressed][daypressed].clearAssignments();
      for( int i = 0; i < newAssignments.length; i++ ) {
        calendar.days[weekpressed][daypressed].assign(new Employee(newAssignments[i]));
      }
      frame.repaint();
	}

//windows + semicolon opens up emoji/symbol insert menu
}
