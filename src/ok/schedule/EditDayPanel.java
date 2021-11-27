package ok.schedule;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.*;

public class EditDayPanel extends JPanel {
	private Day day;
	private JTextField[] names;
	private JCheckBox holidayToggle;
	private JTextField noteText;

	public EditDayPanel(Day day) {
		this.day = day;
		
		noteText = new JTextField(day.getText());
		noteText.setToolTipText("Holiday note");
		noteText.setPreferredSize(new Dimension(200, 30));
		noteText.setBorder(BorderFactory.createLineBorder(Color.black, 4));
		noteText.getDocument().addDocumentListener(new DocumentListener() {
      @Override public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
      @Override public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
      @Override
      public void changedUpdate(DocumentEvent e) {
        day.setText(noteText.getText());
      }
    });
		this.add(noteText);
		
		holidayToggle = new JCheckBox("Is Holiday");
		holidayToggle.setSelected(day.isHoliday());
		holidayToggle.setPreferredSize(new Dimension(200, 30));
		holidayToggle.setFocusable(false);
		holidayToggle.addActionListener(e -> {
		  day.setIsHoliday(holidayToggle.isSelected());
		});
		this.add(holidayToggle);
		
		names = new JTextField[Assigner.NUM_POSITIONS];
		List<Employee> assigned = day.getAssignments();
		this.setPreferredSize(new Dimension(200, 35 * (Assigner.NUM_POSITIONS/2+2))); // 180
		for (int i = 0; i < names.length; i++) {
			String name = "";
			if (assigned != null && i < assigned.size()) {
				name = assigned.get(i).getName();
			}
			names[i] = new JTextField(name);
			names[i].setPreferredSize(new Dimension(200, 30));
			if(i%2 == 0)
			  this.add(names[i]);
		}
		names[0].requestFocusInWindow();
	}

	public String[] getAssignments() {
		String[] assignments = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			assignments[i] = names[i].getText();
		}
		return assignments;
	}
}
