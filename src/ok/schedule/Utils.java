package ok.schedule;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import ok.schedule.model.Day;

public class Utils {
  
  public static final String[] monthStrings = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
      "September", "October", "November", "December" };
  public static String getNameofMonth(int monthofyear) {
    return monthStrings[monthofyear%monthStrings.length];
  }
  
  public static String getNameofDay(int dayofweek) {
    if( dayofweek == 0 ) {
      return "Monday";
    }
    else if( dayofweek == 1 ) {
      return "Tuesday";
    }
    else if( dayofweek == 2 ) {
      return "Wednesday";
    }
    else if( dayofweek == 3 ) {
      return "Thursday";
    }
    else if( dayofweek == 4 ) {
      return "Friday";
    }
    return "unknown";
  }
  
  public static Day getDayByDate(Day[][] days, int date) {
    for (int week = 0; week < days.length; week++) {
      for (int day = 0; day < days[week].length; day++) {
        if( days[week][day] != null && !days[week][day].isUnused() && days[week][day].getOfficialDate() == date ) {
          return days[week][day];
        }
      }
    }
    return null;
  }

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
	
	public static final void makeSampleFonts() {

    String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    int size = 20;
    BufferedImage image = new BufferedImage(400, (size+1)*fonts.length, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D g = image.createGraphics();
    for (int i = 0; i < fonts.length; i++) {
        g.setFont(new Font(fonts[i], Font.PLAIN, 20));
        g.drawString("Font " + i + ":" + fonts[i], 2, (i+1)*20);
    }
    g.dispose();
    try {
      ImageIO.write(image, "png", new File("fonts.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
	}
	
}
