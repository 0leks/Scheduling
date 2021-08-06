package ok.schedule;

import java.util.*;

public class Utils {

	public static final int getDefaultMonth() {
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		if (month > 11) {
			month = 0;
		}
		return month;
	}
}
