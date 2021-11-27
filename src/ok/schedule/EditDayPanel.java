package ok.schedule;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditDayPanel extends JPanel {
  private static final Font font = new Font("Comic Sans MS", Font.PLAIN, 18);
  private static final Font smallFont = new Font("Comic Sans MS", Font.PLAIN, 12);
	private Day day;
	private JTextField[] names;
	private JCheckBox isHolidayCheckBox;
	private JTextField noteText;
	
	private static final int GUI_WIDTH = 160;

	public EditDayPanel(Day day) {
		this.day = day;
		
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
		});
		this.add(isHolidayCheckBox);
		
		names = new JTextField[Assigner.NUM_POSITIONS];
		List<Employee> assigned = day.getAssignments();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		this.setPreferredSize(new Dimension(200, 35 * (Assigner.NUM_POSITIONS/2+2))); // 180
		for (int i = 0; i < names.length; i++) {
			String name = "";
			if (assigned != null && i < assigned.size()) {
				name = assigned.get(i).getName();
			}
			names[i] = new JTextField(name);
			names[i].setFont(font);
			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BorderLayout());
			JLabel label = new JLabel((i/2+1) + ": ");
			label.setFont(font);
			namePanel.add(label, BorderLayout.WEST);
			namePanel.add(names[i], BorderLayout.CENTER);
			namePanel.setPreferredSize(new Dimension(GUI_WIDTH, 30));
			if(i%2 == 0) {
			  this.add(namePanel);
			}
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
