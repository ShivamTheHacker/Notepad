package Notepad;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Notepad {

	private String tabLabel = "Tab ";
	private int startingTabs = 1;
	private int tabCount = 1;
	private static int rows = 19;
	private static int columns = 36;
	private static int scrollBarInt = TextArea.SCROLLBARS_VERTICAL_ONLY;
	private static List<TextArea> notes = new ArrayList<TextArea>();

	private JFrame frame;
	private JTabbedPane tabbedPane;
	private JMenu mFile;
	private static JMenuItem open;
	private JMenuItem save;
	private JMenuItem exit;
	private JMenuItem newTab;
	private JFileChooser fileChooser;

	private static void add(JTabbedPane tabbedPane, String label) {
		JPanel tabContent = new JPanel();
		TextArea textArea = new TextArea("", rows, columns, scrollBarInt);
		notes.add(textArea);
		tabContent.add(textArea);
		tabbedPane.addTab(label, null, tabContent, label);
	}

	private Notepad() {
		frame = new JFrame("Notepad Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		fileChooser = new JFileChooser();

		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		for (int i = 0, n = startingTabs; i < n; i++) {
			add(tabbedPane, tabLabel + tabCount);
			tabCount++;
		}

		JMenuBar mb = new JMenuBar();
		mFile = new JMenu("File");
		open = new JMenuItem("Open");
		save = new JMenuItem("Save");
		exit = new JMenuItem("Exit");
		newTab = new JMenuItem("Add Tab");
		ActionListener addTabl = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add(tabbedPane, tabLabel + tabCount);
				tabCount++;
			}
		};
		newTab.addActionListener(addTabl);
		mFile.add(open);
		mFile.add(save);
		mFile.add(exit);
		mFile.add(newTab);
		mb.add(mFile);
		frame.setJMenuBar(mb);

		OpenListener openL = new OpenListener();
		SaveListener saveL = new SaveListener();
		ExitListener exitL = new ExitListener();
		open.addActionListener(openL);
		save.addActionListener(saveL);
		exit.addActionListener(exitL);

		frame.add(tabbedPane, BorderLayout.CENTER);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	public static void main(String args[]) {
		Notepad notepad = new Notepad();
	}

	private class OpenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (JFileChooser.APPROVE_OPTION == fileChooser
					.showOpenDialog(frame)) {
				File file = fileChooser.getSelectedFile();
				notes.get(tabbedPane.getSelectedIndex()).setText("");
				Scanner in = null;
				try {
					in = new Scanner(file);
					while (in.hasNext()) {
						String line = in.nextLine();
						notes.get(tabbedPane.getSelectedIndex()).append(
								line + "\n");
					}
				} catch (Exception ex) {
					System.out.println("Could not open.");
				} finally {
					in.close();
				}
			}
		}
	}

	private class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (JFileChooser.APPROVE_OPTION == fileChooser
					.showSaveDialog(frame)) {
				File file = fileChooser.getSelectedFile();
				PrintWriter out = null;
				try {
					out = new PrintWriter(file);
					String output = notes.get(tabbedPane.getSelectedIndex())
							.getText();
					out.println(output);
				} catch (Exception ex) {
					System.out.println("Could not save document.");
				} finally {
					try {
						out.flush();
					} catch (Exception ex1) {
						System.out.println("Could not flush out.");
					}
					try {
						out.close();
					} catch (Exception ex1) {
						System.out.println("Could not close out.");
					}
				}
			}
		}
	}

	private class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
}
