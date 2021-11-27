package ok.schedule;

import static ok.schedule.ViewPanel.COLOR_BACKGROUND;
import static ok.schedule.ViewPanel.COLOR_LIGHT_BACKGROUND;
import static ok.schedule.ViewPanel.MAIN_FONT;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import ok.schedule.model.Employee;
import ok.schedule.model.EmployeeRoster;

import static ok.schedule.Constants.*;

public class EmployeeView {

  JPanel editPanel;
  JPanel employeePanel;
  JScrollPane employeePane;
  private HashMap<Employee, EmployeeRowPanel> employeeRowPanels = new HashMap<>();
  
  private EmployeeRoster roster;
  private JFrame frame;
  private Runnable backButtonCallback;
  
  public EmployeeView(EmployeeRoster roster, JFrame frame, Runnable backButtonCallback) {
    this.roster = roster;
    this.frame = frame;
    this.backButtonCallback = backButtonCallback;
    editPanel = new JPanel();
    editPanel.setBackground(COLOR_BACKGROUND);
    editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.LINE_AXIS));
    editPanel.setBorder(BorderFactory.createEmptyBorder(BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING));

    employeePanel = new JPanel();
    employeePanel.setLayout(new BoxLayout(employeePanel, BoxLayout.PAGE_AXIS));
    employeePanel.setBackground(COLOR_LIGHT_BACKGROUND);
    employeePanel.setBorder(BorderFactory.createEmptyBorder(BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING));
    employeePanel.add(Box.createVerticalGlue());
    for (Employee e : roster.employees) {
      addedEmployee(e);
    }
    employeePane = new JScrollPane(employeePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    ImageIcon icon = Utils.loadImageIconResource("/add_employee_icon.png", 32, 32);
    JButton newEmployeeButton = new JButton("New Employee", icon);
    newEmployeeButton.setFont(MAIN_FONT);
    newEmployeeButton.addActionListener(e -> addButtonPressed());
    newEmployeeButton.setFocusable(false);
    JButton backButton = new JButton("Back");
    backButton.setFont(MAIN_FONT);
    backButton.addActionListener(e -> backButtonCallback.run());
    backButton.setFocusable(false);
    FontRenderContext frc = new FontRenderContext(null,true,true);  
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
  
  private void addButtonPressed() {
    String enteredName = (String) JOptionPane
        .showInputDialog(frame, "Enter name of employee:", "Adding employee to list", JOptionPane.PLAIN_MESSAGE,
                  null, null, "John Smith");
    if (enteredName != null) {
      Employee newEmployee = new Employee(enteredName);
      roster.employees.add(newEmployee);
      addedEmployee(newEmployee);
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
  private void rightClickedEmployeeRowButton(Employee employee, int dayClicked, MouseEvent e) {

    if (dayClicked >= 0 && dayClicked < 5) {
        JPopupMenu rightClickMenu = new JPopupMenu();
      JMenuItem none = new JMenuItem("Any Position");
      none.addActionListener(event -> {
        employee.clearLockedPosition(dayClicked);
        Preferences.writeEmployees(roster.employees);
        frame.repaint();
      });
      rightClickMenu.add(none);
      for (int position = 0; position < Constants.NUM_POSITIONS; position++) {
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
          Preferences.writeEmployees(roster.employees);
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
        roster.employees.remove(employee);
        removedEmployee(employee);
      });
      JMenuItem cancel = new JMenuItem("Cancel");
      rightClickMenu.add(cancel);
      rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
    }
  }
  private void leftClickedEmployeeRowButton(Employee employee, int dayClicked, MouseEvent e) {
      employee.toggleAvailable(dayClicked);
    Preferences.writeEmployees(roster.employees);
    frame.repaint();
  }

  private void removedEmployee(Employee employee) {
    if(employeeRowPanels.containsKey(employee)) {
      employeePanel.remove(employeeRowPanels.get(employee));
      frame.validate();
      frame.repaint();
    }
  }
}
