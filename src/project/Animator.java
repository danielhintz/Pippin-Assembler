package project;

import javax.swing.Timer;

public class Animator {
	private static final int TICK = 500;
	private boolean autoStepOn = false;
	private Timer timer;
	private ViewsOrganizer view;

	public Animator(ViewsOrganizer view) {
		this.view = view;
	}

	public void setPeriod(int p) {
		timer.setDelay(p);
	}

	public boolean isAutoStepOn() {
		return autoStepOn;
	}

	public void toggleAutoStep() {
		autoStepOn = !autoStepOn;
	}

	public void setAutoStepOn(boolean autoStepOn) {
		this.autoStepOn = autoStepOn;
	}

	public void start() {
		timer = new Timer(TICK, e -> {
			if (autoStepOn) view.step();
		});
		timer.start();
	}
}
