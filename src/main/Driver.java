package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import main.Driver.ViewPanel.HolidayField;

public class Driver {

  public static final Color COLOR_CALENDAR = new Color(255, 255, 255);
  public static final Color COLOR_BACKGROUND = new Color(110, 210, 210);
  public static final Color COLOR_TEXTFIELD = Color.yellow;
  public static final Color COLOR_HOVER = new Color(220, 220, 220);
  public static final Color COLOR_AVAILABLE = new Color(100, 240, 100);
  public static final Color COLOR_NOT_AVAILABLE = COLOR_HOVER;
  
  private Font mainFont;
  private Font tinyFont;
  private Font mediumFont;

  private JFrame frame;

  private JPanel mainPanel;
  private ViewPanel viewPanel;
  private JPanel buttonPanel;

  private JPanel timeframe;
  private JButton create;
  private JButton save;
  private JButton editEmployees;
  private String previousHolidayName = "Holiday";

  private JPanel employeePanel;
  private JPanel editPanel;
  private JScrollPane employeePane;

  private Day[][] days;
  private int monthNumber;
  private String monthName;
  private int year;

  private List<Employee> employees;

  private int monthOffset = 0;
  
  public Driver() {

    employees = new ArrayList<Employee>();
    Preferences.readEmployees(employees);

    mainFont = new Font("Nyala", Font.PLAIN, 30);
    tinyFont = new Font("Nyala", Font.PLAIN, 15);
    mediumFont = new Font("Nyala", Font.PLAIN, 20);
    // Set up the frame
    frame = new SchedulingFrame();

    URL iconURL = getClass().getResource("icon.png");
    if( iconURL != null ) {
      ImageIcon icon = new ImageIcon(iconURL);
      frame.setIconImage(icon.getImage());
    }
    System.err.println(iconURL);
    frame.setLayout(new BorderLayout());

    // Set up the Main Panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
    mainPanel.setPreferredSize(new Dimension(300, 600));
    mainPanel.setMinimumSize(new Dimension(300, 600));
    mainPanel.setMaximumSize(new Dimension(500, 600));
    mainPanel.setBackground(COLOR_BACKGROUND);

    buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
    buttonPanel.setPreferredSize(new Dimension(300, 500));
    mainPanel.add(buttonPanel);

    viewPanel = new ViewPanel();
    viewPanel.setPreferredSize(new Dimension(800, 800));
    viewPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
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
//              if( days[weekpressed][daypressed].getHolidayField() != null ) {
//                viewPanel.remove(days[weekpressed][daypressed].getTextField());
//              }
//              days[weekpressed][daypressed].setHolidayField(null);
            }
            frame.repaint();
          }
          else if( e.getButton() == MouseEvent.BUTTON3 ) {
            EditDayPanel editPanel = new EditDayPanel(days[weekpressed][daypressed]);
            Object[] options = {"Confirm", "Cancel" };
            int choice = JOptionPane.showOptionDialog(frame, editPanel, "Edit Day", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, null);
            if( choice == 0 ) {
              String[] newAssignments = editPanel.getAssignments();
              days[weekpressed][daypressed].clearAssignments();
              for( int i = 0; i < editPanel.names.length; i++ ) {
                days[weekpressed][daypressed].assign(new Employee(newAssignments[i]));
              }
              frame.repaint();
            }
          }
        }
      }
    });
    mainPanel.add(viewPanel);

    // Set up the month selection spinner
//    String[] monthStrings = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
//        "September", "October", "November", "December" };
//    months = new JComboBox(monthStrings);
//    months.setFont(mainFont);
//    months.setSelectedIndex(getDefaultMonth());
//    months.addActionListener(new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent arg0) {
//        setUpCalendar();
//        viewPanel.removeAll();
//        frame.repaint();
//      }
//    });
//    buttonPanel.add(months);
    
//    JComboBox<String> weekSelector = new JComboBox<String>(new String[] { "1 week", "2 weeks", "3 weeks", "4 weeks", "5 weeks"});
//    weekSelector.setSelectedIndex(4);
//    weekSelector.addActionListener(new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        numWeeks = weekSelector.getSelectedIndex() + 1;
//        setUpCalendar();
//        frame.repaint();
//      }
//    });
//    weekSelector.setFont(mediumFont);
//    buttonPanel.add(weekSelector);
    
    timeframe = new JPanel();
    JButton leftShift = new JButton("<<<");
    JButton rightShift = new JButton(">>>");
    leftShift.setFont(mainFont);
    rightShift.setFont(mainFont);
    leftShift.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyHolidays();
        monthOffset -= 1;
        setUpCalendar();
        frame.repaint();
      }
    });
    rightShift.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyHolidays();
        monthOffset += 1;
        setUpCalendar();
        frame.repaint();
      }
    });
    timeframe.add(leftShift);
    timeframe.add(rightShift);
    buttonPanel.add(timeframe);
    setUpCalendar();

    // Set up the Edit Employees Button
    editEmployees = new JButton("Edit Employees");
    editEmployees.setFont(mainFont);
    editEmployees.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        switchtoEditPanel();
        frame.repaint();
      }
    });
    buttonPanel.add(editEmployees);

    // Set up the Edit Holidays Button
    // editHolidays = new JButton("Edit Holidays");
    // editHolidays.setFont(mainFont);
    // buttonPanel.add(editHolidays);

    // Set up the Create Button
    create = new JButton("Generate");
    create.setFont(mainFont);
    create.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        System.err.println(days[0].length + " positions");
        applyHolidays();
        Assigner assigner = new Assigner();
        String possibleString = assigner.checkIfPossible(days, employees);
        if(possibleString != null) {
          JOptionPane.showMessageDialog(frame, possibleString);
          return;
        }
        Day[][] newDays = assigner.generateSchedule(days, employees);
        if( days != null ) {
          days = newDays;
        }
        frame.repaint();
      }
    });
    Integer[] positionsOptions = new Integer[] { 3, 4, 5, 6, 7, 8, 9, 10};
    JComboBox<Integer> numberPositions = new JComboBox<Integer>(positionsOptions);
    numberPositions.setFont(mainFont);
    numberPositions.setToolTipText("Number of positions per day.");
    numberPositions.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        Assigner.NUM_POSITIONS = (Integer)numberPositions.getSelectedItem();
      }
    });
    numberPositions.setSelectedIndex(5); // 8 positions is default
    
    JPanel generatePanel = new JPanel();
    generatePanel.add(create);
    generatePanel.add(numberPositions);
    buttonPanel.add(generatePanel);
    save = new JButton("Save");
    save.setFont(mainFont);
    save.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        writeToFile();
      }
    });
    buttonPanel.add(save);
    
//    holidayNames = new JTextField("Holiday");
//    holidayNames.setPreferredSize(new Dimension(200, 40));
//    holidayNames.setFont(mediumFont);
//    holidayNames.setHorizontalAlignment(JTextField.CENTER);
//    buttonPanel.add(holidayNames);

    frame.add(mainPanel, BorderLayout.CENTER);
    frame.setSize(1500, 940);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  public void switchtoMainPanel() {
    
    Preferences.writeEmployees(employees);
    
    frame.remove(editPanel);
    frame.add(mainPanel, BorderLayout.CENTER);
  }

  public void switchtoEditPanel() {
    frame.remove(mainPanel);

    editPanel = new JPanel() {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(COLOR_BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
      }
    };
    editPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 500, 50));
    employeePanel = new ScrollablePanel();
    employeePanel.setBackground(COLOR_NOT_AVAILABLE);
    employeePanel.setPreferredSize(new Dimension(720, getDrawY(employees.size()) + 10));
    employeePanel.setAutoscrolls(true);
    employeePanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int indexClicked = e.getY() / getEmployeeRowHeight();
        if (employees.size() > indexClicked) {
          final int dayClicked = e.getX() / 100 - 2;
          if (dayClicked >= 0 && dayClicked < 5) {
            Employee emp = employees.get(indexClicked);
            if( e.getButton() == MouseEvent.BUTTON3 ) {
              JPopupMenu rightClickMenu = new JPopupMenu();
              JMenuItem none = new JMenuItem("Any Position");
              none.addActionListener(event -> {
                emp.clearLockedPosition(dayClicked);
                Preferences.writeEmployees(employees);
                frame.repaint();
              });
              rightClickMenu.add(none);
              for(int position = 0; position < Assigner.NUM_POSITIONS; position++) {
                JMenuItem lockPos = new JMenuItem("Lock Position " + (position+1));
                lockPos.setActionCommand("" + position);
                lockPos.addActionListener(event -> {
                  try {
                    int pos = Integer.parseInt(event.getActionCommand());
                    emp.lockedPosition(dayClicked, pos);
                  } catch(NumberFormatException error) {
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
            }
            else {
              emp.toggleAvailable(dayClicked);
              Preferences.writeEmployees(employees);
              frame.repaint();
            }
          }
          else if( dayClicked == -1 || dayClicked == -2 ) {
            if( e.getButton() == MouseEvent.BUTTON3 ) {
              JPopupMenu rightClickMenu = new JPopupMenu();
              JMenuItem delete = new JMenuItem("Remove Employee");
              rightClickMenu.add(delete);
              delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  employees.remove(indexClicked);
                  frame.repaint();
                }
              });
              JMenuItem cancel = new JMenuItem("Cancel");
              rightClickMenu.add(cancel);
              rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
            }
          }
        }
      }
    });
    employeePane = new JScrollPane(employeePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    employeePane.setPreferredSize(new Dimension(720, 600));
    editPanel.add(employeePane);

    JButton add = new JButton("Add");
    add.setFont(mainFont);
    add.setPreferredSize(new Dimension(200, 100));
    add.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        String enteredName = (String) JOptionPane.showInputDialog(frame, "Enter name of employee:",
            "Adding employee to list", JOptionPane.PLAIN_MESSAGE, null, null, "John Smith");

        if( enteredName != null ) {
          Employee newEmployee = new Employee(enteredName);
          employees.add(newEmployee);
          employeePanel.setPreferredSize(new Dimension(employeePanel.getWidth(), employeePanel.getHeight() + getEmployeeRowHeight()));
          employeePanel.setSize(new Dimension(employeePanel.getWidth(), employeePanel.getHeight() + getEmployeeRowHeight()));
          frame.validate();
          frame.repaint();
        }
      }
    });
    editPanel.add(add);

    JButton save = new JButton("Back");
    save.setFont(mainFont);
    save.setPreferredSize(new Dimension(200, 100));
    save.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        switchtoMainPanel();
        frame.validate();
        frame.repaint();
      }
    });
    editPanel.add(save);

    frame.add(editPanel, BorderLayout.CENTER);

    frame.validate();
  }

  public int getDrawY(int index) {
    return getEmployeeRowHeight() * (index);
  }

  public int getEmployeeRowHeight() {
    return mediumFont.getSize() * 2;
  }

  public class ScrollablePanel extends JPanel implements Scrollable {

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.setColor(COLOR_CALENDAR);
      g.fillRect(0, 0, getWidth(), getHeight());
      int row = 0;
      for (Employee e : employees) {
        int y = getDrawY(row);
        g.setColor(Color.black);
        g.setFont(mediumFont);
        g.drawString(e.getName(), 10, y + mediumFont.getSize() + 5);
        g.drawRect(0, y, 200, mediumFont.getSize() * 2);
        for (int day = 0; day < 5; day++) {
          int x = 200 + day * 100;
          if (e.available(day)) {
            g.setColor(COLOR_AVAILABLE);
          } else {
            g.setColor(COLOR_NOT_AVAILABLE);
          }
          int offset = 1;
          g.fillRect(x + offset, y + offset, 100 - offset, mediumFont.getSize() * 2 - offset);
          if (e.available(day)) {
            g.setColor(Color.black);
            g.setFont(tinyFont);
            g.drawString(Day.getNameofDay(day), x + 5, (int) (y + mediumFont.getSize()*1.5));
          }
          if (e.isPositionLocked(day)) {
            g.setColor(Color.black);
            if(e.available(day)) {
              g.setFont(tinyFont.deriveFont(Font.BOLD));
            }
            else {
              g.setFont(tinyFont);
            }
            String str = "Pos " + (e.getLockedPosition(day) + 1);
            int strWidth = g.getFontMetrics().stringWidth(str);
            g.drawString(str, x + 100 - strWidth, (int) (y + tinyFont.getSize()));
          }
          g.setColor(Color.black);
          g.drawRect(x, y, 100, mediumFont.getSize() * 2);
        }
        row++;
      }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      if (orientation == SwingConstants.HORIZONTAL) {
        return visibleRect.width - 1;
      } else {
        return visibleRect.height - 1;
      }
    }
    @Override
    public boolean getScrollableTracksViewportHeight() {
      return false;
    }
    @Override
    public boolean getScrollableTracksViewportWidth() {
      return false;
    }
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      // Get the current position.
      int currentPosition = 0;
      int maxUnitIncrement = 5;
      if (orientation == SwingConstants.HORIZONTAL) {
        currentPosition = visibleRect.x;
      } else {
        currentPosition = visibleRect.y;
      }

      // Return the number of pixels between currentPosition
      // and the nearest tick mark in the indicated direction.
      if (direction < 0) {
        int newPosition = currentPosition - (currentPosition / maxUnitIncrement) * maxUnitIncrement;
        return (newPosition == 0) ? maxUnitIncrement : newPosition;
      } else {
        return ((currentPosition / maxUnitIncrement) + 1) * maxUnitIncrement - currentPosition;
      }
    }

  }

  public void applyHolidays() {
    for (int week = 0; week < days.length; week++) {
      for (int day = 0; day < days[week].length; day++) {
        days[week][day].absorbTextField();
//        JTextField field = days[week][day].getTextField();
//        if( field != null ) {
//          days[week][day].absorbTextField();
//          viewPanel.remove(field);
//        }
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

  public int getDefaultMonth() {
    int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

    if (month > 11) {
      month = 0;
    }
    return month;
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
    public HolidayField addHolidayField(int week, int day, String holidayName) {

      HolidayField field = new HolidayField(holidayName, days[week][day]);
      field.setFont(mediumFont);
      this.add(field);
      field.setBounds(day * getCellWidth() + 2, week * getCellHeight() + mediumFont.getSize() + 6, getCellWidth() - 4,
          mediumFont.getSize() + 8);
      field.addKeyListener(field);
      field.addFocusListener(field);
      return field;
    }

    public class HolidayField extends JTextField implements KeyListener, FocusListener {

      private boolean removed;
      private Day day;

      public HolidayField(String text, Day day) {
        super(text);
        this.day = day;
      }
      public void remove() {
        if (!removed) {
          day.setHolidayField(null);
          ViewPanel.this.remove(this);
          removed = true;
          frame.repaint();
        }
      }
      public void apply() {
        if (!removed) {
          String text = getText();
          day.setText(text);
          previousHolidayName = text;
          frame.repaint();
        }
      }
      public void applyAndRemove() {
        apply();
        remove();
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          applyAndRemove();
        }
      }

      @Override
      public void keyReleased(KeyEvent arg0) {
      }

      @Override
      public void keyTyped(KeyEvent arg0) {
      }
      @Override
      public void focusGained(FocusEvent e) {
        
      }
      @Override
      public void focusLost(FocusEvent e) {
        applyAndRemove();
      }
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
      int spacePerName = (cellheight - tinyFont.getSize()*3)/(Assigner.NUM_POSITIONS + 1);
      
      // don't let font size get too small
      spacePerName = Math.min(tinyFont.getSize(), spacePerName);
      
      Font employeeFont = tinyFont.deriveFont((float)spacePerName);
      
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
            g.setFont(tinyFont);
            g.drawString(days[week][day].getOfficialDate() + " " + days[week][day].getName(), cellx + 3, celly + tinyFont.getSize() + 2);
            g.drawString(days[week][day].getMonth() + " " + days[week][day].getYear(), cellx + 3, celly + cellheight-4);
            if (days[week][day].isHoliday()) {
              g.setColor(Color.black);
              g.setFont(mediumFont);
              g.drawString(days[week][day].getText(), cellx + 3, celly + 2 * tinyFont.getSize() + 8);
            }
            else if( !days[week][day].isUnused() && days[week][day].hasAssignments() ) {
              for( int index = 0; index < days[week][day].getAssignments().size(); index++ ) {
                // pos 2 and 5 have 2 people assigned
                String preString = "  ";
                if( index <= 1 ) {
                  preString = (index+1) + "";
                }
                if( index >= 3 && index <= 5 ) {
                  preString = index + "";
                }
                if(index >= 7) { 
                  preString = (index-1) + "";
                }
                g.setColor(Color.black);
                g.setFont(employeeFont);
                g.drawString(preString + " " + days[week][day].getAssignments().get(index).getName(), cellx + 3, celly + 2*tinyFont.getSize() + index * employeeFont.getSize() + 3*index+2);
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
    PrintWriter fileOut;
    try {
      String fileName = "Mohr_" + year + "_" + monthName + "_Schedule";
      String chosenFileName = JOptionPane.showInputDialog(frame, "Choose File Name", fileName);
      if( chosenFileName == null ) {
        return;
      }
      fileName = chosenFileName + ".html";
      fileOut = new PrintWriter(new FileWriter(fileName, false));

      //filename = 'Mohr_' + month[0:3] + '_' + month2[0:3] + '_' + yearstr + '_Schedule.html'
      
      fileOut.print("<html>\n");
      fileOut.print("<head>\n");
      String monthHeader = "<title>" + monthName + " " + year;
      fileOut.print(monthHeader);
      fileOut.print("</title>\n");
      fileOut.print("<style type=\"text/css\">\n");
      fileOut.print("th, td { padding:1px; padding-right: 20px; width: 230px; font-weight: bold;font-size: 18px; vertical-align: top;}\n");
      fileOut.print("li { font-weight: normal;font-size: 16px; }\n");
      fileOut.print("td { height: 139px;}\n");
      fileOut.print("h2 {text-align: center;}\n");
      fileOut.print("ol { -webkit-margin-before: 5px; -webkit-margin-after: 5px; margin-block-start: 0.1em; margin-block-end: 0.1em;}\n");
      fileOut.print("table, th, td {border: 1px solid black;border-collapse: collapse;}\n");
      fileOut.print("</style>\n");
      fileOut.print("</head>\n");

      fileOut.print("\n<body>\n");

      fileOut.print("<h2>Noon Supervisor Schedule</h2>\n");
      monthHeader = "<h2>" + monthName + " " + year;
      fileOut.print(monthHeader);
      fileOut.print("</h2>\n");

      fileOut.print("<table border=\"1\">\n");
      fileOut.print("<tr><th>Monday</th><th>Tuesday</th><th>Wednesday</th><th>Thursday</th><th>Friday</th></tr>\n");

      fileOut.print("<tr>\n");
      String lastprinted = "";
      for( int week = 0; week < days.length; week++ ) {
        for( int day = 0; day < days[week].length; day++ ) {
          fileOut.print("<td>\n");
          fileOut.print( days[week][day].getOfficialDate() + " " ) ;
          if( !lastprinted.equals(days[week][day].getMonth()) ) {
            fileOut.print( days[week][day].getMonth() ) ;
            lastprinted = days[week][day].getMonth();
          }
          
          if( days[week][day].isHoliday() ) {
            fileOut.print("<ul>\n");
            fileOut.print(days[week][day].getText());
            fileOut.print("</ul>\n");
          }
          else if( !days[week][day].isUnused() ) {
            fileOut.print("<ol>\n");
            List<Employee> assigned = days[week][day].getAssignments();
            for(int position = 0; position < assigned.size(); position++) {
              Employee e = assigned.get(position);
              fileOut.print("<li>");
              fileOut.print(e.getName());
              if( position == 1 || position == 5 ) {
                position++;
                Employee b = assigned.get(position);
                fileOut.print(" " + b.getName());
              }
              fileOut.print("</li>\n");
            }
            fileOut.print("</ol>\n");
          }
          
          fileOut.print("</td>\n");
        }
        fileOut.print("</tr>\n<tr>\n");
      }

      fileOut.print("</table>\n");
      fileOut.print("</body>\n");
      fileOut.print("</html>\n");
      fileOut.close();
      JOptionPane.showMessageDialog(frame, "Saved " + fileName);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  public class SchedulingFrame extends JFrame {

    public SchedulingFrame() {
      super("Scheduling");
    }
  }

  public static void main(String[] args) {
    //new TestAssignment(null);
    new Driver();
  }
  public class EditDayPanel extends JPanel {
    private Day day;
    private JTextField[] names;
    public EditDayPanel(Day day) {
      this.day = day;
      names = new JTextField[Assigner.NUM_POSITIONS];
      List<Employee> assigned = day.getAssignments();
      this.setPreferredSize(new Dimension(200, 35*Assigner.NUM_POSITIONS)); // 180
      for( int i = 0; i < names.length; i++ ) {
        String name = "";
        if( assigned != null && i < assigned.size() ) {
          name = assigned.get(i).getName();
        }
        names[i] = new JTextField(name);
        names[i].setPreferredSize(new Dimension(200, 30));
        this.add(names[i]);
      }
      names[0].requestFocusInWindow();
    }
    public String[] getAssignments() {
      String[] assignments = new String[names.length];
      for( int i = 0; i < names.length; i++ ) {
        assignments[i] = names[i].getText();
      }
      return assignments;
    }
  }
}
