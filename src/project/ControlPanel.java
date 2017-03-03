package project;

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class ControlPanel implements Observer {

	private JButton step = new JButton("Step");
	private JButton clear = new JButton("Clear");
	private JButton run = new JButton("Run/Pause");
	private JButton reload = new JButton("Reload");
	private ViewsOrganizer view;

	public ControlPanel(ViewsOrganizer gui) {
		view = gui;
		gui.addObserver(this);
	}

	public JComponent createControlDisplay() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 0));

		step.addActionListener(x -> view.step());
		clear.addActionListener(x -> view.clearJob());
		run.addActionListener(x -> view.toggleAutoStep());
		reload.addActionListener(x -> view.reload());

		panel.add(step);
		panel.add(clear);
		panel.add(run);
		panel.add(reload);

		JSlider slider = new JSlider(5, 1000);
		slider.addChangeListener(e -> view.setPeriod(slider.getValue()));
		panel.add(slider);

		return panel;
	}

	@Override
	public void update(Observable o, Object arg) {
		run.setEnabled(view.getCurrentState().getRunPauseActive());
		step.setEnabled(view.getCurrentState().getStepActive());
		clear.setEnabled(view.getCurrentState().getClearActive());
		reload.setEnabled(view.getCurrentState().getReloadActive());
	}

}
