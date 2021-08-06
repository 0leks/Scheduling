package ok.schedule;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import main.*;

public class EditDayPanel extends JPanel {
	private Day day;
	private JTextField[] names;

	public EditDayPanel(Day day) {
		this.day = day;
		names = new JTextField[Assigner.NUM_POSITIONS];
		List<Employee> assigned = day.getAssignments();
		this.setPreferredSize(new Dimension(200, 35 * Assigner.NUM_POSITIONS)); // 180
		for (int i = 0; i < names.length; i++) {
			String name = "";
			if (assigned != null && i < assigned.size()) {
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
		for (int i = 0; i < names.length; i++) {
			assignments[i] = names[i].getText();
		}
		return assignments;
	}
}
