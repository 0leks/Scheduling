package ok.schedule;

import java.awt.*;

import javax.swing.*;

import main.*;

public class EmployeeRowPanel extends JPanel {
	
	public static final int MIN_WIDTH = 700;
	public static final int MIN_HEIGHT = Driver.MEDIUM_FONT.getSize() * 2;
	private Employee employee;

	public EmployeeRowPanel(Employee employee) {
		this.employee = employee;
	}
	@Override
	public Dimension getMinimumSize() {
		return getMaximumSize();
	}
	@Override
	public Dimension getPreferredSize() {
		return getMaximumSize();
	}
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(MIN_WIDTH, MIN_HEIGHT);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int cellw = 200;
		g.setFont(Driver.MEDIUM_FONT);
		g.drawString(employee.getName(), 10, Driver.MEDIUM_FONT.getSize() + 5);
		g.drawRect(0, 0, cellw, getHeight()-1);
		for (int day = 0; day < 5; day++) {
			int x = cellw + day * (cellw / 2);
			if (employee.available(day)) {
				g.setColor(Driver.COLOR_AVAILABLE);
			} else {
				g.setColor(Driver.COLOR_NOT_AVAILABLE);
			}
			int offset = 1;
			g.fillRect(x + offset, offset, cellw / 2 - offset, Driver.MEDIUM_FONT.getSize() * 2 - offset);
			if (employee.available(day)) {
				g.setColor(Color.black);
				g.setFont(Driver.TINY_FONT);
				g.drawString(Day.getNameofDay(day), x + 5, (int) (Driver.MEDIUM_FONT.getSize() * 1.5));
			}
			if (employee.isPositionLocked(day)) {
				g.setColor(Color.black);
				if (employee.available(day)) {
					g.setFont(Driver.TINY_FONT.deriveFont(Font.BOLD));
				} else {
					g.setFont(Driver.TINY_FONT);
				}
				String str = "Pos " + (employee.getLockedPosition(day) + 1);
				int strWidth = g.getFontMetrics().stringWidth(str);
				g.drawString(str, x + cellw / 2 - strWidth, (int) (Driver.TINY_FONT.getSize()));
			}
			g.setColor(Color.black);
			
			g.drawRect(x, 0, day == 4 ? cellw/2-1 : cellw/2, getHeight()-1);
		}
	}
}
