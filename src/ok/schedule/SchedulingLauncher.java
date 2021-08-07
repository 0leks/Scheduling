package ok.schedule;

import ok.launcher.*;

public class SchedulingLauncher {
	
	private static final String appName = "scheduling";
	private static final String projectName = "scheduling";
	
	private void run() {

		Launcher launch = new Launcher(appName, projectName);
	}

	public static void main(String[] args) {
		new SchedulingLauncher().run();
	}
}
