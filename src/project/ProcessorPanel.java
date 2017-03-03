package project;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProcessorPanel implements Observer {
	private MachineModel model;
	private JTextField acc = new JTextField();
	private JTextField ip = new JTextField();
	private JTextField mb = new JTextField();

	public ProcessorPanel(ViewsOrganizer gui, MachineModel model) {
		this.model = model;
		gui.addObserver(this);
	}

	public JComponent createProcessorDisplay() {
		JPanel panel = new JPanel();

		acc.setEditable(false);
		ip.setEditable(false);
		mb.setEditable(false);

		acc.setBackground(Color.WHITE);
		ip.setBackground(Color.WHITE);
		mb.setBackground(Color.WHITE);

		panel.setLayout(new GridLayout(1, 0));
		panel.add(new JLabel("Accumulator: ", JLabel.RIGHT));
		panel.add(acc);

		panel.setLayout(new GridLayout(1, 0));
		panel.add(new JLabel("Instruction Pointer: ", JLabel.RIGHT));
		panel.add(ip);

		panel.setLayout(new GridLayout(1, 0));
		panel.add(new JLabel("Memory Base: ", JLabel.RIGHT));
		panel.add(mb);

		return panel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (model != null) {
			acc.setText("" + model.getAccumulator());
			ip.setText("" + model.getInstructionPointer());
			mb.setText("" + model.getMemoryBase());
		}
	}

	public static void main(String[] args) {
		ViewsOrganizer view = new ViewsOrganizer();
		MachineModel model = new MachineModel();
		ProcessorPanel panel = new ProcessorPanel(view, model);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 60);
		frame.setLocationRelativeTo(null);
		frame.add(panel.createProcessorDisplay());
		frame.setVisible(true);
	}
}