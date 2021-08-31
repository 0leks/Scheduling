package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.net.URL;
import java.util.*;
import java.util.List;

import javax.swing.*;

import ok.launcher.*;
import ok.schedule.*;
import ok.schedule.Utils;

public class Driver {
	
	private static final String projectName = "scheduling";

	public static final int BIG_NUMBER = 99999;
  public static final int BUTTON_PADDING = 10;
    
  public static final Color COLOR_CALENDAR = new Color(255, 255, 255);
  public static final Color COLOR_LIGHT_BACKGROUND = new Color(130, 220, 220);
  public static final Color COLOR_BACKGROUND = new Color(110, 210, 210);
  public static final Color COLOR_TEXTFIELD = Color.yellow;
  public static final Color COLOR_HOVER = new Color(220, 220, 220);
  public static final Color COLOR_AVAILABLE = new Color(100, 240, 100);
  public static final Color COLOR_NOT_AVAILABLE = COLOR_HOVER;

  public static final Font MAIN_FONT = new Font("Nyala", Font.PLAIN, 30);
  public static final Font TINY_FONT = new Font("Nyala", Font.PLAIN, 15);
  public static final Font MEDIUM_FONT = new Font("Nyala", Font.PLAIN, 20);

  private JFrame frame;

  private JPanel mainPanel;
  private ViewPanel viewPanel;
  private JPanel buttonPanel;

  private JPanel timeframe;
  private JButton generateButton;
  private JButton save;
  private JButton editEmployees;
  private String previousHolidayName = "Holiday";

  private JPanel employeePanel;
  private HashMap<Employee, EmployeeRowPanel> employeeRowPanels = new HashMap<>();
  private JPanel editPanel;
  private JScrollPane employeePane;

  private Day[][] days;
  private int monthNumber;
  private String monthName;
  private int year;
  
  private boolean customEdits = false;

  private List<Employee> employees = new ArrayList<>();

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
		Preferences.readEmployees(employees);
		initializeFrame();
		checkForUpdates();
		initializePanels();
		initEditPanel();

	    setUpCalendar();
	    
		frame.setVisible(true);
		frame.validate();
	}

	private void initializeFrame() {
		frame = new JFrame("Scheduling 2.0.5");
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
	    URL iconURL = getClass().getResource("icon.png");
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
		mainPanel.setBorder(BorderFactory.createEmptyBorder(BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING));
		mainPanel.add(Box.createHorizontalGlue());

		buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, BUTTON_PADDING));

		viewPanel = new ViewPanel();
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

		timeframe = new JPanel();
		timeframe.setOpaque(false);
		timeframe.setLayout(new BoxLayout(timeframe, BoxLayout.LINE_AXIS));
		JButton leftShift = new JButton("<<<");
		JButton rightShift = new JButton(">>>");
		leftShift.setFont(MAIN_FONT);
		rightShift.setFont(MAIN_FONT);
		leftShift.addActionListener(e -> shiftMonth(-1));
		rightShift.addActionListener(e -> shiftMonth(1));
		timeframe.add(leftShift);
		timeframe.add(Box.createRigidArea(new Dimension(BUTTON_PADDING, 0)));
		timeframe.add(rightShift);

	    editEmployees = new JButton("Edit Employees", Utils.loadImageIconResource("/edit_employee_icon.png", 32, 32));
	    editEmployees.setFont(MAIN_FONT);
	    editEmployees.setMaximumSize(new Dimension(BIG_NUMBER, 0));
	    editEmployees.addActionListener(e -> switchtoEditPanel());
	    generateButton = new JButton("Generate");
	    generateButton.setFont(MAIN_FONT);
	    generateButton.setMaximumSize(new Dimension(BIG_NUMBER, 0));
	    generateButton.addActionListener(e -> generateButtonPressed());

	    save = new JButton("Save", Utils.loadImageIconResource("/save_icon.png", 32, 32));
	    save.setFont(MAIN_FONT);
	    save.setMaximumSize(new Dimension(BIG_NUMBER, 0));
	    save.addActionListener(e -> writeToFile());
	    
	    buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(timeframe);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, BUTTON_PADDING)));
	    buttonPanel.add(editEmployees);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, BUTTON_PADDING)));
	    buttonPanel.add(generateButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, BUTTON_PADDING)));
	    buttonPanel.add(save);
	    buttonPanel.add(Box.createVerticalGlue());
	    
//	    Integer[] positionsOptions = new Integer[] { 5, 6, 7, 8, 9, 10, 11, 12};
//	    JComboBox<Integer> numberPositions = new JComboBox<Integer>(positionsOptions);
//	    numberPositions.setFont(mainFont);
//	    numberPositions.setToolTipText("Number of positions per day.");
//	    numberPositions.addItemListener(e -> {
//	        Assigner.NUM_POSITIONS = (Integer)numberPositions.getSelectedItem();
//	    });
//	    numberPositions.setSelectedIndex(5); // 8 positions is default
	    
	    for(Component c : buttonPanel.getComponents()) {
	    	if(c instanceof JButton) {
	    		((JButton)c).setAlignmentX(Component.CENTER_ALIGNMENT);
	    	}
	    }
	    frame.add(mainPanel, BorderLayout.CENTER);
	}

	private void generateButtonPressed() {
		if(customEdits) {
			int response = JOptionPane.showConfirmDialog(
                         frame, 
                         "You have made some custom edits.\n" +
                         "Are you sure you would like to overwrite your changes?",
                         "Discard changes?",
                         JOptionPane.OK_CANCEL_OPTION);
			if(response != JOptionPane.OK_OPTION) {
				return;
			}
		}
		System.err.println(days[0].length + " positions");
		applyHolidays();
		Assigner assigner = new Assigner();
		String possibleString = assigner.checkIfPossible(days, employees);
		if (possibleString != null) {
			JOptionPane.showMessageDialog(frame, possibleString);
			return;
		}
		Day[][] newDays = assigner.generateSchedule(days, employees);
		if (days != null) {
			days = newDays;
			customEdits = false;
		}
		frame.repaint();
	}
	private void shiftMonth(int delta) {
		applyHolidays();
		monthOffset += delta;
		setUpCalendar();
	}

  public void switchtoMainPanel() {
    
    Preferences.writeEmployees(employees);
    
    frame.remove(editPanel);
    frame.add(mainPanel, BorderLayout.CENTER);
    frame.validate();
    frame.repaint();
  }
  	private void rightClickedEmployeeRowButton(Employee employee, int dayClicked, MouseEvent e) {

		if (dayClicked >= 0 && dayClicked < 5) {
	  		JPopupMenu rightClickMenu = new JPopupMenu();
			JMenuItem none = new JMenuItem("Any Position");
			none.addActionListener(event -> {
				employee.clearLockedPosition(dayClicked);
				Preferences.writeEmployees(employees);
				frame.repaint();
			});
			rightClickMenu.add(none);
			for (int position = 0; position < Assigner.NUM_POSITIONS/2; position++) {
				JMenuItem lockPos = new JMenuItem("Lock Position " + (position + 1));
				lockPos.setActionCommand("" + position);
				lockPos.addActionListener(event -> {
					try {
						int pos = Integer.parseInt(event.getActionCommand());
						employee.lockedPosition(dayClicked, pos);
					} catch (NumberFormatException error) {
						error.printStackTrace();
						JOptionPane.showMessageDialog(null, "Num format error at lock pos");
					}
					Preferences.writeEmployees(employees);
					frame.repaint();
				});
				rightClickMenu.add(lockPos);
			}
			JMenuItem cancel = new JMenuItem("Cancel");
			rightClickMenu.add(cancel);
			rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
		} else if (dayClicked == -1 || dayClicked == -2) {
			JPopupMenu rightClickMenu = new JPopupMenu();
			JMenuItem delete = new JMenuItem("Remove Employee");
			rightClickMenu.add(delete);
			delete.addActionListener(ee -> {
				employees.remove(employee);
				removedEmployee(employee);
			});
			JMenuItem cancel = new JMenuItem("Cancel");
			rightClickMenu.add(cancel);
			rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
		}
  	}
  	private void leftClickedEmployeeRowButton(Employee employee, int dayClicked, MouseEvent e) {
  		employee.toggleAvailable(dayClicked);
		Preferences.writeEmployees(employees);
		frame.repaint();
  	}
	private void employeePanelMousePressed(MouseEvent e) {
		int indexClicked = e.getY() / getEmployeeRowHeight();
		if (employees.size() > indexClicked) {
			final int dayClicked = e.getX() / 100 - 2;
			Employee emp = employees.get(indexClicked);
			if (e.getButton() == MouseEvent.BUTTON3) {
				rightClickedEmployeeRowButton(emp, dayClicked, e);
			} else {
				leftClickedEmployeeRowButton(emp, dayClicked, e);
			}
		}
	}
	private void employeeRowPanelMousePressed(MouseEvent e, Employee employee) {
		final int dayClicked = e.getX() / 100 - 2;
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightClickedEmployeeRowButton(employee, dayClicked, e);
		} else {
			leftClickedEmployeeRowButton(employee, dayClicked, e);
		}
	}

	private void addedEmployee(Employee employee) {
		EmployeeRowPanel row = new EmployeeRowPanel(employee);
		row.setAlignmentX(Box.LEFT_ALIGNMENT);
		row.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				employeeRowPanelMousePressed(e, employee);
			}
		});
		employeeRowPanels.put(employee, row);
		employeePanel.add(row, employeePanel.getComponentCount()-1);
		frame.validate();
		frame.repaint();
	}
	private void removedEmployee(Employee employee) {
		if(employeeRowPanels.containsKey(employee)) {
			employeePanel.remove(employeeRowPanels.get(employee));
			frame.validate();
			frame.repaint();
		}
	}
	private void addButtonPressed() {
		String enteredName = (String) JOptionPane
				.showInputDialog(	frame, "Enter name of employee:", "Adding employee to list", JOptionPane.PLAIN_MESSAGE,
									null, null, "John Smith");
		if (enteredName != null) {
			Employee newEmployee = new Employee(enteredName);
			employees.add(newEmployee);
			addedEmployee(newEmployee);
		}
	}
	private void initEditPanel() {
	    editPanel = new JPanel();
	    editPanel.setBackground(COLOR_BACKGROUND);
	    editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.LINE_AXIS));
	    editPanel.setBorder(BorderFactory.createEmptyBorder(BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING));

	    employeePanel = new JPanel();
	    employeePanel.setLayout(new BoxLayout(employeePanel, BoxLayout.PAGE_AXIS));
	    employeePanel.setBackground(COLOR_LIGHT_BACKGROUND);
	    employeePanel.setBorder(BorderFactory.createEmptyBorder(BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING));
		employeePanel.add(Box.createVerticalGlue());
	    for (Employee e : employees) {
			addedEmployee(e);
		}
	    employeePane = new JScrollPane(employeePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	                       	        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	    ImageIcon icon = Utils.loadImageIconResource("/add_employee_icon.png", 32, 32);
	    JButton newEmployeeButton = new JButton("New Employee", icon);
	    newEmployeeButton.setFont(MAIN_FONT);
	    newEmployeeButton.addActionListener(e -> addButtonPressed());
	    JButton backButton = new JButton("Back");
	    backButton.setFont(MAIN_FONT);
	    backButton.addActionListener(e -> switchtoMainPanel());
	    AffineTransform affinetransform = new AffineTransform();     
	    FontRenderContext frc = new FontRenderContext(affinetransform,true,true);  
	    int maxSize = 100 + (int)MAIN_FONT.getStringBounds(newEmployeeButton.getText(), frc).getWidth();
	    newEmployeeButton.setMaximumSize(new Dimension(maxSize, 0));
	    backButton.setMaximumSize(new Dimension(maxSize, 0));

	    JPanel editPanelButtons = new JPanel();
	    editPanelButtons.setOpaque(false);
	    editPanelButtons.setLayout(new BoxLayout(editPanelButtons, BoxLayout.PAGE_AXIS));
	    editPanelButtons.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, BUTTON_PADDING));

	    editPanelButtons.add(Box.createVerticalGlue());
	    editPanelButtons.add(newEmployeeButton);
	    editPanelButtons.add(Box.createRigidArea(new Dimension(0, BUTTON_PADDING)));
	    editPanelButtons.add(backButton);
	    editPanelButtons.add(Box.createVerticalGlue());
	    
	    editPanel.add(editPanelButtons);
	    editPanel.add(Box.createHorizontalGlue());
	    editPanel.add(employeePane);
	    editPanel.add(Box.createHorizontalGlue());
	}
  public void switchtoEditPanel() {
    frame.remove(mainPanel);
    frame.add(editPanel, BorderLayout.CENTER);
    frame.validate();
    frame.repaint();
  }



  public void applyHolidays() {
    for (int week = 0; week < days.length; week++) {
      for (int day = 0; day < days[week].length; day++) {
        days[week][day].absorbTextField();
      }
    }
  }
  public void setUpCalendar() {
    int maxNumberWeeks = 5;
    days = new Day[maxNumberWeeks][5]; // TODO
    monthName = null;
    monthNumber = 0;
    year = 0;

    Calendar cal = Calendar.getInstance();
    // set day of month to 1
    // if current day of month is 31 and next month only has 30 days, 
    // Calendar will automatically roll over to next month.
    cal.set( Calendar.DAY_OF_MONTH, 1);
    
    int targetMonth = cal.get(Calendar.MONTH) + 1 + monthOffset;
    cal.set( Calendar.MONTH, targetMonth );
    
    monthNumber = cal.get(Calendar.MONTH);
    monthName = Day.getNameofMonth(monthNumber);
    year = cal.get(Calendar.YEAR);
    
    while( cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ) {
      cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1);
    }
    while( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ) {
      cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)-1);
    }
    
    int numWeeks = 0;
    for (int week = 0; week < days.length; week++) {
      int currentMonth = cal.get(Calendar.MONTH);
      boolean added = false;
      if( currentMonth == monthNumber ) {
        numWeeks++;
        added = true;
      }
      for (int day = 0; day < days[week].length; day++) {
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
        days[week][day] = newDay;
        
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
        newDays[week][day] = days[week][day];
      }
    }
    days = newDays;
	frame.repaint();
  }
  
  public Day getDaybyID(Day[][] cal, int id) {
    if( cal == null ) {
      return null;
    }
    for (int week = 0; week < days.length; week++) {
      for (int day = 0; day < days[week].length; day++) {
        
        if( cal[week][day] != null && cal[week][day].getID() == id ) {
          return cal[week][day];
        }
      }
    }
    return null;
  }

  public class ViewPanel extends JPanel {
    private int dayHovered;
    private int monthHovered;
    public ViewPanel() {
      dayHovered = -1;
      monthHovered = -1;
      this.addMouseMotionListener(new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
          dayHovered = e.getX() / viewPanel.getCellWidth();
          monthHovered = e.getY() / viewPanel.getCellHeight();
          repaint();
        }
      });
      this.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseExited(MouseEvent e) {
          dayHovered = -1;
          monthHovered = -1;
          repaint();
        }
      });
    }
    public void updateHolidayName(String text) {
    	previousHolidayName = text;
    }
    public HolidayField addHolidayField(int week, int day, String holidayName) {

      HolidayField field = new HolidayField(holidayName, days[week][day], this);
      field.setFont(MEDIUM_FONT);
      this.add(field);
      field.setBounds(day * getCellWidth() + 2, week * getCellHeight() + MEDIUM_FONT.getSize() + 6, getCellWidth() - 4,
          MEDIUM_FONT.getSize() + 8);
      field.addKeyListener(field);
      field.addFocusListener(field);
      return field;
    }

    public int getCellWidth() {
      return this.getWidth() / 5;
    }

    public int getCellHeight() {
      return this.getHeight() / 5;
    }

    @Override
    public void paintComponent(Graphics g) {
      // 5 days per the week
      int cellwidth = this.getWidth() / 5;
      // maximum 5 weeks per month
      int cellheight = this.getHeight() / 5;
      
      
      // leave space for date/day and month/year
      int spacePerName = (int)((cellheight - TINY_FONT.getSize()*3)/(Assigner.NUM_POSITIONS/2 + 1));
      
      // don't let font size get too small
      spacePerName = Math.max(TINY_FONT.getSize(), spacePerName);
      
      Font employeeFont = TINY_FONT.deriveFont((float)spacePerName);
      
      g.setColor(COLOR_BACKGROUND);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      
      for (int week = 0; week < days.length; week++) {
        for (int day = 0; day < days[week].length; day++) {
//          if( days[week][day].isUnclickable() ) {
//            continue;
//          }
          int cellx = cellwidth * day;
          int celly = cellheight * week;
          g.setColor(COLOR_CALENDAR);
          if( day == dayHovered && week == monthHovered ) {
            g.setColor(COLOR_HOVER);
          }
          g.fillRect(cellx, celly, cellwidth, cellheight);
          g.setColor(Color.black);
          g.drawRect(cellx, celly, cellwidth, cellheight);
          if (days[week][day].getOfficialDate() != 0) {
            g.setColor(Color.GRAY);
            g.setFont(TINY_FONT);
            g.drawString(days[week][day].getOfficialDate() + " " + days[week][day].getName(), cellx + 3, celly + TINY_FONT.getSize() + 2);
            g.drawString(days[week][day].getMonth() + " " + days[week][day].getYear(), cellx + 3, celly + cellheight-4);
            if (days[week][day].isHoliday()) {
              g.setColor(Color.black);
              g.setFont(MEDIUM_FONT);
              g.drawString(days[week][day].getText(), cellx + 3, celly + 2 * TINY_FONT.getSize() + 8);
            }
            else if( !days[week][day].isUnused() && days[week][day].hasAssignments() ) {
              for( int index = 0; index < days[week][day].getAssignments().size(); index+=2 ) {
                // pos 2 and 5 have 2 people assigned
                
                g.setColor(Color.black);
                g.setFont(employeeFont);
                int ypos = celly + TINY_FONT.getSize() + employeeFont.getSize() + 4 + index/2 * (employeeFont.getSize()+4);
                String toDraw1 = (1 + index/2) + "";
                if(days[week][day].getAssignments().size() > index) {
                  toDraw1 += " " + days[week][day].getAssignments().get(index).getName();
                }
//                if(days[week][day].getAssignments().size() > index+1) {
//                  toDraw1 += ", " + days[week][day].getAssignments().get(index + 1).getName();
//                }
                g.drawString(toDraw1, cellx + 3, ypos);
              
              }
            }
          }
        }
      }
      g.setColor(Color.black);
      g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
    }
  }
  
	public void writeToFile() {
		applyHolidays();
		String fileName = "Mohr_" + year + "_" + monthName + "_Schedule";
		String chosenFileName = JOptionPane.showInputDialog(frame, "Choose File Name", fileName);
		if (chosenFileName == null) {
			return;
		}
		fileName = chosenFileName + ".html";
		boolean success = HtmlWriter.writeToFile(fileName, year, monthName, days);

		if(success) {
			JOptionPane.showMessageDialog(frame, "Saved " + fileName);
		}
		else {
			JOptionPane.showMessageDialog(frame, "Failed to save to " + fileName, "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		// new TestAssignment(null);
		new Driver().run();
	}

	private void viewPanelMousePressed(MouseEvent e) {
      int daypressed = e.getX() / viewPanel.getCellWidth();
      int weekpressed = e.getY() / viewPanel.getCellHeight();
      if( weekpressed >= 0 && weekpressed < days.length ) {
        if( e.getButton() == MouseEvent.BUTTON1 ) {
          days[weekpressed][daypressed].toggleHoliday();
          if (days[weekpressed][daypressed].isHoliday()) {
            applyHolidays();
            days[weekpressed][daypressed].setText("");
            HolidayField field = viewPanel.addHolidayField(weekpressed, daypressed, previousHolidayName);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setBackground(COLOR_TEXTFIELD);
            days[weekpressed][daypressed].setHolidayField(field);
            field.selectAll();
            field.requestFocus();
          } else {
            days[weekpressed][daypressed].removeTextField();
//            if( days[weekpressed][daypressed].getHolidayField() != null ) {
//              viewPanel.remove(days[weekpressed][daypressed].getTextField());
//            }
//            days[weekpressed][daypressed].setHolidayField(null);
          }
          frame.repaint();
        }
        else if( e.getButton() == MouseEvent.BUTTON3 ) {
          EditDayPanel editPanel = new EditDayPanel(days[weekpressed][daypressed]);
          Object[] options = {"Confirm", "Cancel" };
          int choice = JOptionPane.showOptionDialog(frame, editPanel, "Edit Day", JOptionPane.YES_NO_OPTION,
              JOptionPane.INFORMATION_MESSAGE, null, options, null);
          if( choice == 0 ) {
        	customEdits = true;
            String[] newAssignments = editPanel.getAssignments();
            days[weekpressed][daypressed].clearAssignments();
            for( int i = 0; i < newAssignments.length; i++ ) {
              days[weekpressed][daypressed].assign(new Employee(newAssignments[i]));
            }
            frame.repaint();
          }
        }
      }
	}

	public int getDrawY(int index) {
		return getEmployeeRowHeight() * (index);
	}
//windows + semicolon opens up emoji/symbol insert menu
	public int getEmployeeRowHeight() {
		return Driver.MEDIUM_FONT.getSize() * 2;
	}
}
