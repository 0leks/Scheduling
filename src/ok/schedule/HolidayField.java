package ok.schedule;

import java.awt.event.*;

import javax.swing.*;

import main.*;
import main.Driver.*;

public class HolidayField extends JTextField implements KeyListener, FocusListener {

	private boolean removed;
	private Day day;
	private ViewPanel parent;

	public HolidayField(String text, Day day, ViewPanel parent) {
		super(text);
		this.day = day;
		this.parent = parent;
	}

	public void remove() {
		if (!removed) {
			day.setHolidayField(null);
			parent.remove(this);
			removed = true;
			parent.repaint();
		}
	}

	public void apply() {
		if (!removed) {
			String text = getText();
			day.setText(text);
			parent.updateHolidayName(text);
			parent.repaint();
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
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		applyAndRemove();
	}
}
