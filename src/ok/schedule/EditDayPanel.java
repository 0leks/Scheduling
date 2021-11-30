package ok.schedule;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ok.schedule.model.Day;
import ok.schedule.model.Employee;

public class EditDayPanel extends JPanel {
  private static final Font font = new Font("Comic Sans MS", Font.PLAIN, 18);
  private static final Font smallFont = new Font("Comic Sans MS", Font.PLAIN, 12);
  private JTextField[] names;
  private JLabel[] nameLabels;
	private JCheckBox isHolidayCheckBox;
	private JTextField noteText;
	
	private static final int GUI_WIDTH = 220;

	public EditDayPanel(Day day) {
		
		noteText = new JTextField(day.getText());
		noteText.setFont(font);
		noteText.setToolTipText("Holiday note");
		noteText.setPreferredSize(new Dimension(GUI_WIDTH, 50));
		noteText.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 2), "Note", TitledBorder.TRAILING, TitledBorder.TOP, smallFont, Color.gray));
		noteText.setBackground(this.getBackground());
		noteText.getDocument().addDocumentListener(new DocumentListener() {
      @Override public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
      @Override public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
      @Override
      public void changedUpdate(DocumentEvent e) {
        day.setText(noteText.getText());
      }
    });
		this.add(noteText);
		
		isHolidayCheckBox = new JCheckBox("Is Holiday", day.isHoliday());
		isHolidayCheckBox.setFont(font);
		isHolidayCheckBox.setPreferredSize(new Dimension(GUI_WIDTH, 30));
		isHolidayCheckBox.setFocusable(false);
    isHolidayCheckBox.setForeground(isHolidayCheckBox.isSelected() ? Color.black : Color.LIGHT_GRAY);
		isHolidayCheckBox.addActionListener(e -> {
		  day.setIsHoliday(isHolidayCheckBox.isSelected());
		  isHolidayCheckBox.setForeground(isHolidayCheckBox.isSelected() ? Color.black : Color.LIGHT_GRAY);
		  for(JTextField nameField : names) {
		    nameField.setEnabled(!isHolidayCheckBox.isSelected());
		  }
		  for(JLabel nameLabel : nameLabels) {
		    nameLabel.setForeground(isHolidayCheckBox.isSelected() ? Color.LIGHT_GRAY : Color.black);
		  }
		});
		this.add(isHolidayCheckBox);

    names = new JTextField[Constants.NUM_POSITIONS];
    nameLabels = new JLabel[Constants.NUM_POSITIONS];
		List<Employee> assigned = day.getAssignments();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int i = 0; i < names.length; i++) {
			String name = "";
			if (assigned != null && i < assigned.size()) {
				name = assigned.get(i).getName();
			}
			names[i] = new JTextField(name);
			names[i].setFont(font);
			names[i].setEnabled(!isHolidayCheckBox.isSelected());
			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BorderLayout());
			nameLabels[i] = new JLabel((i+1) + ": ");
			nameLabels[i].setFont(font);
      nameLabels[i].setForeground(isHolidayCheckBox.isSelected() ? Color.LIGHT_GRAY : Color.black);
			namePanel.add(nameLabels[i], BorderLayout.WEST);
			namePanel.add(names[i], BorderLayout.CENTER);
			namePanel.setPreferredSize(new Dimension(GUI_WIDTH, 30));
			this.add(namePanel);
		}
	}

	public String[] getAssignments() {
		String[] assignments = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			assignments[i] = names[i].getText();
		}
		return assignments;
	}
}
