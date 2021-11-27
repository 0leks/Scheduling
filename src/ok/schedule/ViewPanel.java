package ok.schedule;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

import ok.schedule.model.MyCalendar;

public class ViewPanel extends JPanel {

  public static final Color COLOR_CALENDAR = new Color(255, 255, 255);
  public static final Color COLOR_LIGHT_BACKGROUND = new Color(130, 220, 220);
  public static final Color COLOR_BACKGROUND = new Color(110, 210, 210);
  public static final Color COLOR_TEXTFIELD = Color.yellow;
  public static final Color COLOR_HOVER = new Color(220, 220, 220);
  public static final Color COLOR_AVAILABLE = new Color(100, 240, 100);
  public static final Color COLOR_NOT_AVAILABLE = COLOR_HOVER;

  public static final Font MAIN_FONT = new Font("Nyala", Font.PLAIN, 30);
  public static final Font TINY_FONT = new Font("Nyala", Font.PLAIN, 15);
  public static final Font MEDIUM_FONT = new Font("Nyala", Font.PLAIN, 20);
  
  private int dayHovered = -1;
  private int monthHovered = -1;
  
  private MyCalendar calendar;
  
  public ViewPanel(MyCalendar calendar) {
    this.calendar = calendar;
    this.setBackground(COLOR_BACKGROUND);
    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        dayHovered = e.getX() / getCellWidth();
        monthHovered = e.getY() / getCellHeight();
        repaint();
      }
    });
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseExited(MouseEvent e) {
        dayHovered = -1;
        monthHovered = -1;
        repaint();
      }
    });
  }

  public int getCellWidth() {
    return this.getWidth() / 5;
  }

  public int getCellHeight() {
    return this.getHeight() / 5;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    int cellwidth = getCellWidth();
    int cellheight = getCellHeight();
    // leave space for date/day and month/year
    int spacePerName = (int)((cellheight - TINY_FONT.getSize()*3)/(Constants.NUM_POSITIONS + 1));
    // don't let font size get too small
    spacePerName = Math.max(TINY_FONT.getSize(), spacePerName);
    Font employeeFont = TINY_FONT.deriveFont((float)spacePerName);
    
    for (int week = 0; week < calendar.days.length; week++) {
      for (int day = 0; day < calendar.days[week].length; day++) {
        int cellx = cellwidth * day;
        int celly = cellheight * week;
        g.setColor(COLOR_CALENDAR);
        if( day == dayHovered && week == monthHovered ) {
          g.setColor(COLOR_HOVER);
        }
        g.fillRect(cellx, celly, cellwidth, cellheight);
        g.setColor(Color.black);
        g.drawRect(cellx, celly, cellwidth, cellheight);
        if (calendar.days[week][day].getOfficialDate() != 0) {
          g.setColor(Color.GRAY);
          g.setFont(TINY_FONT);
          g.drawString(calendar.days[week][day].getOfficialDate() + " " + calendar.days[week][day].getName(), cellx + 3, celly + TINY_FONT.getSize() + 2);
          g.drawString(calendar.days[week][day].getMonth() + " " + calendar.days[week][day].getYear(), cellx + 3, celly + cellheight-4);
          g.setColor(Color.black);
          g.drawString(calendar.days[week][day].getText(), cellx + cellwidth - g.getFontMetrics().stringWidth(calendar.days[week][day].getText()) - 2, celly + 2*TINY_FONT.getSize() + 2);
          if (calendar.days[week][day].isHoliday()) {
            g.setColor(Color.GRAY);
            g.setFont(TINY_FONT);
            g.drawString("Holiday", cellx + cellwidth - 2 - g.getFontMetrics().stringWidth("Holiday"), celly + cellheight-4);
          }
          else if( !calendar.days[week][day].isUnused() && calendar.days[week][day].hasAssignments() ) {
            for( int index = 0; index < calendar.days[week][day].getAssignments().size(); index+=2 ) {
              g.setColor(Color.black);
              g.setFont(employeeFont);
              int ypos = celly + TINY_FONT.getSize() + employeeFont.getSize() + 4 + index/2 * (employeeFont.getSize()+4);
              String toDraw1 = (1 + index/2) + "";
              if(calendar.days[week][day].getAssignments().size() > index) {
                toDraw1 += " " + calendar.days[week][day].getAssignments().get(index).getName();
              }
              g.drawString(toDraw1, cellx + 3, ypos);
            }
          }
        }
      }
    }
    g.setColor(Color.black);
    g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
  }
}
