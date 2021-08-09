package ok.schedule;

import java.awt.*;
import java.awt.image.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import main.*;

public class Utils {

	public static final int getDefaultMonth() {
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		if (month > 11) {
			month = 0;
		}
		return month;
	}
	public static final ImageIcon loadImageIconResource(String location, int width, int height) {
		URL iconURL = Driver.class.getResource(location);
		if (iconURL == null) {
			System.err.println("ERROR failed to get resource URL: \"" + location + "\"");
			return null;
		}
		return resizeImageIcon(new ImageIcon(iconURL), width, height);
	}
	
	public static final ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
		Image image = icon.getImage();
		Image newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		return new ImageIcon(newimg);
	}
}
