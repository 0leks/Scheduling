package ok.schedule;

import java.awt.*;

import javax.swing.*;

interface WidthGet {
	public int getWidth();
}
public class SquareSizedPanel extends JPanel {
	private WidthGet minusWidth;
	public SquareSizedPanel(WidthGet minusWidth) {
		this.minusWidth = minusWidth;
	}
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		System.out.println("pref:" + d);
		return getMaximumSize();
	}
	@Override
	public Dimension getMaximumSize() {
		Dimension parentSize = getParent().getSize();
		int buttonWidth = minusWidth.getWidth();
		int maxWidth = parentSize.width - buttonWidth;
		int maxHeight = parentSize.height;
		int maxDim = Math.min(maxHeight, maxWidth);
		Dimension d = super.getMaximumSize();
		System.out.println("max:" + d);
		System.out.println(maxDim);
		return new Dimension(maxDim, maxDim);
	}
	@Override
	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		System.out.println("min:" + d);
		return getMaximumSize();
	}
}
