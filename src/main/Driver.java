package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;

public class Driver {

  public static final Color COLOR_CALENDAR = new Color(255, 255, 255);
  public static final Color COLOR_BACKGROUND = new Color(100, 200, 200);
  public static final Color COLOR_TEXTFIELD = Color.yellow;
  public static final Color COLOR_HOVER = new Color(220, 220, 220);
  
  private Font mainFont;
  private Font tinyFont;
  private Font mediumFont;

  private JFrame frame;

  private JPanel mainPanel;
  private ViewPanel viewPanel;
  private JPanel buttonPanel;

  //private JComboBox months;
  private JPanel timeframe;
  private JButton create;
  private JButton editEmployees;
  private JButton editHolidays;
  private JTextField holidayNames;

  private JPanel employeePanel;
  private JPanel editPanel;
  private JScrollPane employeePane;

  private Day[][] days;
  private String month1;
  private String month2;
  private int year1;
  private int year2;

  private List<Employee> employees;

  private int weekOffset = 0;
  
  public Driver() {

    // setUpCalendar();
    employees = new ArrayList<Employee>();
    Preferences.readEmployees(employees);

    mainFont = new Font("Nyala", Font.PLAIN, 30);
    tinyFont = new Font("Nyala", Font.PLAIN, 15);
    mediumFont = new Font("Nyala", Font.PLAIN, 20);
    // Set up the frame
    frame = new SchedulingFrame();
    frame.setLayout(new BorderLayout());

    // Set up the Main Panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
    mainPanel.setPreferredSize(new Dimension(300, 600));
    mainPanel.setMinimumSize(new Dimension(300, 600));
    mainPanel.setMaximumSize(new Dimension(500, 600));
    mainPanel.setBackground(COLOR_BACKGROUND);

    buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 50));
    buttonPanel.setPreferredSize(new Dimension(300, 500));
    mainPanel.add(buttonPanel);

    viewPanel = new ViewPanel();
    viewPanel.setPreferredSize(new Dimension(800, 800));
    viewPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int daypressed = e.getX() / viewPanel.getCellWidth();
        int weekpressed = e.getY() / viewPanel.getCellHeight();
        days[weekpressed][daypressed].toggleHoliday();
        if (days[weekpressed][daypressed].isHoliday()) {
          JTextField field = viewPanel.addHolidayField(weekpressed, daypressed, holidayNames.getText());
          field.setHorizontalAlignment(JTextField.CENTER);
          field.setBackground(COLOR_TEXTFIELD);
          days[weekpressed][daypressed].setTextField(field);
          field.selectAll();
          field.requestFocus();
        } else {
          if( days[weekpressed][daypressed].getTextField() != null ) {
            viewPanel.remove(days[weekpressed][daypressed].getTextField());
          }
          days[weekpressed][daypressed].setTextField(null);
        }
        frame.repaint();
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
    
    timeframe = new JPanel();
    JButton leftShift = new JButton("<<<");
    JButton rightShift = new JButton(">>>");
    leftShift.setFont(mainFont);
    rightShift.setFont(mainFont);
    leftShift.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyHolidays();
        weekOffset -= 1;
        setUpCalendar();
        frame.repaint();
      }
    });
    rightShift.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyHolidays();
        weekOffset += 1;
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
        Day[][] newDays = (new Assigner().generateSchedule(days, employees));
        if( days != null ) {
          days = newDays;
        }
        writeToFile();
        frame.repaint();
      }
    });
    buttonPanel.add(create);
    
    holidayNames = new JTextField("Holiday");
    holidayNames.setPreferredSize(new Dimension(200, 40));
    holidayNames.setFont(mediumFont);
    holidayNames.setHorizontalAlignment(JTextField.CENTER);
    buttonPanel.add(holidayNames);

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
    employeePanel.setPreferredSize(new Dimension(720, getDrawY(employees.size()) + 10));
    employeePanel.setAutoscrolls(true);
    employeePanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int indexClicked = e.getY() / getEmployeeRowHeight();
        if (employees.size() > indexClicked) {
          int dayClicked = e.getX() / 100 - 2;
          if (dayClicked >= 0 && dayClicked < 5) {
            Employee emp = employees.get(indexClicked);
            emp.toggleAvailable(dayClicked);
            Preferences.writeEmployees(employees);
            frame.repaint();
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
        g.setColor(Color.black);
        g.setFont(mediumFont);
        g.drawString(e.getName(), 10, getDrawY(row) + mediumFont.getSize() + 5);
        g.drawRect(0, getDrawY(row), 200, mediumFont.getSize() * 2);
        for (int day = 0; day < 5; day++) {
          g.setColor(Color.black);
          g.drawRect(200 + day * 100, getDrawY(row), 100, mediumFont.getSize() * 2);
          if (e.available(day)) {
            g.setColor(Color.green);
          } else {
            g.setColor(Color.red);
          }
          int offset = 1;
          g.fillRect(200 + day * 100 + offset, getDrawY(row) + offset, 100 - offset, mediumFont.getSize() * 2 - offset);
          if (e.available(day)) {
            g.setColor(Color.black);
            g.setFont(tinyFont);
            g.drawString(Day.getNameofDay(day), 205 + day * 100, (int) (getDrawY(row) + mediumFont.getSize()*1.5));
          }
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
    for (int week = 0; week < 5; week++) {
      for (int day = 0; day < 5; day++) {
        JTextField field = days[week][day].getTextField();
        if( field != null ) {
          days[week][day].absorbTextField();
          viewPanel.remove(field);
        }
      }
    }
  }
  public void setUpCalendar() {
    Day[][] olddays = days;
    days = new Day[5][5];
    month1 = null;
    month2 = null;
    year1 = 0;
    year2 = 0;

    Calendar cal = Calendar.getInstance();
    int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    int weekofyear = cal.get(Calendar.WEEK_OF_YEAR);
    cal.set(Calendar.WEEK_OF_YEAR, weekofyear + weekOffset);
    while( dayofweek != 2 ) {
      cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1);
      dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    }
    
    weekofyear = cal.get(Calendar.WEEK_OF_YEAR);
    
    for (int week = 0; week < 5; week++) {
      for (int day = 0; day < 5; day++) {
        int date = cal.get(Calendar.DAY_OF_MONTH);
        int dayofyear = cal.get(Calendar.DAY_OF_YEAR);
        Day exists = getDaybyID(olddays, dayofyear);
        
        String nameofmonth =  Day.getNameofMonth(cal.get(Calendar.MONTH));
        if( month1 == null ) {
          month1 = nameofmonth;
        }
        else {
          month2 = nameofmonth;
        }
        
        int year = cal.get(Calendar.YEAR);
        if( year1 == 0 ) {
          year1 = year;
        }
        else {
          year2 = year;
        }
        
        if( exists == null ) {
          Day newDay = new Day(dayofweek-2, date, dayofyear );
          newDay.setMonth(cal.get(Calendar.MONTH));
          newDay.setYear(year);
          days[week][day] = newDay;
        }
        else {
//          JTextField field = exists.getTextField();
//          if( field != null ) {
//            exists.absorbTextField();
//            viewPanel.remove(field);
//          }
          days[week][day] = exists;
          days[week][day].clearAssignments();
        }
        
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1);
        dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        while( dayofweek == 1 || dayofweek == 7 ) {
          cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1);
          dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        }
      }
    }
  }
  
  public void absorbTextFields() {
    for (int week = 0; week < 5; week++) {
      for (int day = 0; day < 5; day++) {
        JTextField field = days[week][day].getTextField();
        if( field != null ) {
          days[week][day].absorbTextField();
          viewPanel.remove(field);
        }
      }
    }
  }
  public Day getDaybyID(Day[][] cal, int id) {
    if( cal == null ) {
      return null;
    }
    for (int week = 0; week < 5; week++) {
      for (int day = 0; day < 5; day++) {
        
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
    public JTextField addHolidayField(int week, int day, String holidayName) {

      JTextField field = new JTextField(holidayName);
      field.setFont(mediumFont);
      this.add(field);
      field.setBounds(day * getCellWidth() + 2, week * getCellHeight() + mediumFont.getSize() + 6, getCellWidth() - 4,
          mediumFont.getSize() + 8);
      field.addKeyListener(new TextFieldListener(field, days[week][day]));
      return field;
    }

    public class TextFieldListener implements KeyListener {

      private JTextField field;
      private Day day;

      public TextFieldListener(JTextField comp, Day day) {
        field = comp;
        this.day = day;
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          if (field != null) {
            day.setText(field.getText());
            ViewPanel.this.remove(field);
            field = null;
            frame.repaint();
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent arg0) {
      }

      @Override
      public void keyTyped(KeyEvent arg0) {
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
      int width = this.getWidth();
      int height = this.getHeight();
      int cellwidth = width / 5;
      int cellheight = height / 5;
      g.setColor(COLOR_CALENDAR);
      g.fillRect(0, 0, width, height);

      int fontsize = tinyFont.getSize();
      g.setFont(tinyFont);
      g.setColor(Color.black);
      for (int week = 0; week < 5; week++) {
        for (int day = 0; day < 5; day++) {
          int x = cellwidth * day;
          int y = cellheight * week;
          if( day == dayHovered && week == monthHovered ) {
            g.setColor(COLOR_HOVER);
            g.fillRect(x, y, cellwidth, cellheight);
            g.setColor(Color.black);
          }
          g.drawRect(x, y, cellwidth, cellheight);
          if (days[week][day].getOfficialDate() != 0) {
            g.drawString(days[week][day].getOfficialDate() + " " + days[week][day].getName(), x + 3, y + fontsize + 2);
            g.setFont(tinyFont);
            g.drawString(days[week][day].getMonth() + " " + days[week][day].getYear(), x + 3, y + cellheight-4);
            if (days[week][day].isHoliday()) {
              g.setFont(mediumFont);
              g.drawString(days[week][day].getText(), x + 3, y + 2 * fontsize + 8);
              g.setFont(tinyFont);
            }
            if( days[week][day].hasAssignments() ) {
              for( int index = 0; index < days[week][day].getAssignments().size(); index++ ) {
                g.setFont(tinyFont);
                g.drawString(days[week][day].getAssignments().get(index).getName(), x + 3, y + (2+index) * fontsize + 3*index+2);
              }
            }
          }

        }
      }
    }

  }
  
  public void writeToFile() {
    absorbTextFields();
    PrintWriter fileOut;
    try {
      fileOut = new PrintWriter(new FileWriter("Mohr_" + month1 + "-" + month2 + "_Schedule.html" , false));

      //filename = 'Mohr_' + month[0:3] + '_' + month2[0:3] + '_' + yearstr + '_Schedule.html'
      
      fileOut.print("<html>\n");
      fileOut.print("<head>\n");
      fileOut.print("<title>" + month1 + "/" + month2 + " " + year1);
      if( year2 != 0 ) {
        fileOut.print("/" + (year2-2000) + "</title>\n");
      }
      else {
        fileOut.print("</title>\n");
      }
      fileOut.print("<style type=\"text/css\">\n");
      fileOut.print("th, td { padding:1px; padding-right: 20px; width: 230px; font-weight: bold;font-size: 18px; vertical-align: top;}\n");
      fileOut.print("td { height: 139px;}\n");
      fileOut.print("h2 {text-align: center;}\n");
      fileOut.print("ol { -webkit-margin-before: 5px; -webkit-margin-after: 5px; }\n");
      fileOut.print("table, th, td {border: 1px solid black;border-collapse: collapse;}\n");
      fileOut.print("</style>\n");
      fileOut.print("</head>\n");

      fileOut.print("\n<body>\n");

      fileOut.print("<h2>Yard Duty Schedule</h2>\n");
      fileOut.print("<h2>" + month1 + "/" + month2 + " " + year1);
      if( year2 != year1 ) {
        fileOut.print("/" + (year2-2000) + "</h2>\n");
      }
      else {
        fileOut.print("</h2>\n");
      }

      fileOut.print("<table border=\"1\">\n");
      fileOut.print("<tr><th>Monday</th><th>Tuesday</th><th>Wednesday</th><th>Thursday</th><th>Friday</th></tr>\n");

      fileOut.print("<tr>\n");
      String lastprinted = "";
      for( int week = 0; week < 5; week++ ) {
        for( int day = 0; day < 5; day++ ) {
          fileOut.print("<td>\n");
          fileOut.print( days[week][day].getOfficialDate() + " " ) ;
          if( !lastprinted.equals(days[week][day].getMonth()) ) {
            fileOut.print( days[week][day].getMonth() ) ;
            lastprinted = days[week][day].getMonth();
          }
          
          if( days[week][day].isHoliday() ) {
            fileOut.print("<ul>\n");
            fileOut.print(days[week][day].getText());
//            for( int a = 1; a < 5; a++ ) {
//              fileOut.print("<li>");
//              if( a == 0 )
//                fileOut.print(days[week][day].getText());
//              fileOut.print("</li>\n");
//            }
            fileOut.print("</ul>\n");
          }
          else {
            fileOut.print("<ol>\n");
            for( Employee e : days[week][day].getAssignments() ) {
              fileOut.print("<li>");
              fileOut.print(e.getName());
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
}
