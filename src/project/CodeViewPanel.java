package project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class CodeViewPanel implements Observer {

	private MachineModel model;
	private Code code;
	private JScrollPane scroller;
	private JTextField[] codeHex = new JTextField[Code.CODE_MAX / 2];
	private JTextField[] codeDecimal = new JTextField[Code.CODE_MAX / 2];
	private int previousColor = -1;

	public CodeViewPanel(ViewsOrganizer gui, MachineModel mdl) {
		model = mdl;
		gui.addObserver(this);
	}

	public JComponent createCodeDisplay() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		Border border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Code Memory View", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);

		panel.setBorder(border);

		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BorderLayout());

		JPanel numPanel = new JPanel(), decimalPanel = new JPanel(), hexPanel = new JPanel();
		numPanel.setLayout(new GridLayout(0, 1));
		decimalPanel.setLayout(new GridLayout(0, 1));
		hexPanel.setLayout(new GridLayout(0, 1));

		innerPanel.add(numPanel, BorderLayout.LINE_START);
		innerPanel.add(decimalPanel, BorderLayout.CENTER);
		innerPanel.add(hexPanel, BorderLayout.LINE_END);

		for (int i = 0; i < Code.CODE_MAX / 2; i++) {
			numPanel.add(new JLabel(i + ": ", JLabel.RIGHT));
			codeDecimal[i] = new JTextField(10);
			codeHex[i] = new JTextField(10);
			codeDecimal[i].setEditable(false);
			codeHex[i].setEditable(false);
			codeHex[i].setBackground(Color.WHITE);
			codeDecimal[i].setBackground(Color.WHITE);
			decimalPanel.add(codeDecimal[i]);
			hexPanel.add(codeHex[i]);
		}

		scroller = new JScrollPane(innerPanel);
		panel.add(scroller);
		return panel;

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 != null && arg1.equals("Load Code")) {
			code = model.getCode();
			int offset = model.getCurrentJob().getStartcodeIndex();
			for (int i = offset; i < offset + model.getCurrentJob().getCodeSize(); i++) {
				codeHex[i].setText(code.getHex(i));
				codeDecimal[i].setText(code.getDecimal(i));
			}
			previousColor = model.getInstructionPointer();
			codeHex[previousColor].setBackground(Color.YELLOW);
			codeDecimal[previousColor].setBackground(Color.YELLOW);
		} else if (arg1 != null && arg1.equals("Clear")) {
			int offset = model.getCurrentJob().getStartcodeIndex();
			code = null;
			for (int i = offset; i < offset + model.getCurrentJob().getCodeSize(); i++) {
				if (code == null) {
					codeHex[i].setText("");
					codeDecimal[i].setText("");
				} else {
					codeHex[i].setText(code.getHex(i));
					codeDecimal[i].setText(code.getDecimal(i));
				}
			}
			if (previousColor >= 0 && previousColor < Code.CODE_MAX / 2) {
				codeHex[previousColor].setBackground(Color.WHITE);
				codeDecimal[previousColor].setBackground(Color.WHITE);
			}
			previousColor = -1;
		}
		if (this.previousColor >= 0 && previousColor < Code.CODE_MAX / 2) {
			codeHex[previousColor].setBackground(Color.WHITE);
			codeDecimal[previousColor].setBackground(Color.WHITE);
		}
		previousColor = model.getInstructionPointer();
		if (this.previousColor >= 0 && previousColor < Code.CODE_MAX / 2) {
			codeHex[previousColor].setBackground(Color.YELLOW);
			codeDecimal[previousColor].setBackground(Color.YELLOW);
		}

		if (scroller != null && code != null && model != null) {
			JScrollBar bar = scroller.getVerticalScrollBar();
			int pc = model.getInstructionPointer();
			// if(pc < Code.CODE_MAX && codeHex[pc] != null) { << CHANGE THIS
			// LINE TO THE NEXT LINE
			if (pc > 0 && pc < Code.CODE_MAX && codeHex[pc] != null) {
				Rectangle bounds = codeHex[pc].getBounds();
				bar.setValue(Math.max(0, bounds.y - 15 * bounds.height));
			}
		}
	}

	public static void main(String[] args) {
		ViewsOrganizer view = new ViewsOrganizer();
		MachineModel model = new MachineModel();
		CodeViewPanel panel = new CodeViewPanel(view, model);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 700);
		frame.setLocationRelativeTo(null);
		frame.add(panel.createCodeDisplay());
		frame.setVisible(true);
		int size = Integer.parseInt(Loader.load(model, new File("merge.pexe"), 0, 0));
		model.getCurrentJob().setCodeSize(size);
		panel.update(view, "Load Code");
	}

}
