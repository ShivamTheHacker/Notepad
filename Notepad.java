package Notepad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class Notepad {

	private String tabLabel = "Tab ";
	private int startingTabs = 1;
	private static int tabCount = 1;
	private static int rows = 19;
	private static int columns = 36;
	private static int scrollBarInt = TextArea.SCROLLBARS_VERTICAL_ONLY;
	private static List<TextArea> notes = new ArrayList<TextArea>();
	private String savedFileName;
	private List<String> recentFiles = new ArrayList<String>();
	private boolean justAdded = false;
	// private Map<String, String> allFiles = new HashMap<String, String>();

	// System.out.println(new File(".").getAbsoluteFile()); - To test directory

	private JFrame frame;
	private static JTabbedPane tabbedPane;
	private JMenu mFile;
	private JMenuItem open, save, exit, newTab;
	private JFileChooser fileChooser;
	private JTree tree;
	private DefaultMutableTreeNode root;

	private static Color color1 = Color.ORANGE;
	private static Color color2 = Color.CYAN;

	private File file;

	private Notepad() {

		System.out.println(new File(".").getAbsoluteFile());

		frame = new JFrame("Notepad Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		createMenu();
		createTabs();
		createTree();

		frame.setBackground(color1);
		frame.setSize(700, 400);
		frame.setVisible(true);
	}

	private void createMenu() {
		fileChooser = new JFileChooser();
		JMenuBar mb = new JMenuBar();
		mb.setBackground(color2);
		mFile = new JMenu("File");
		mFile.setBackground(color2);
		open = new JMenuItem("Open");
		open.setBackground(color2);
		save = new JMenuItem("Save");
		save.setBackground(color2);
		exit = new JMenuItem("Exit");
		exit.setBackground(color2);
		newTab = new JMenuItem("New");
		newTab.setBackground(color2);
		ActionListener addTabl = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add(tabLabel + tabCount, "");
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
	}

	private void createTabs() {
		tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(color2);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		for (int i = 0, n = startingTabs; i < n; i++) {
			add(tabLabel + tabCount, "");
			tabCount++;
		}
		frame.add(tabbedPane, BorderLayout.WEST);
	}

	private static void add(String label, String text) {
		JPanel tabContent = new JPanel();
		tabContent.setBackground(color2);
		TextArea textArea = new TextArea(text, rows, columns, scrollBarInt);
		textArea.setBackground(color2);
		notes.add(textArea);
		tabContent.add(textArea);
		tabbedPane.addTab(label, null, tabContent, label);
	}

	private void createTree() {
		saveFileName();
		root = new DefaultMutableTreeNode("Recently saved");
		for (int i = 0; i < recentFiles.size(); i++) {
			DefaultMutableTreeNode nodeToAdd = new DefaultMutableTreeNode(
					recentFiles.get(i));
			root.add(nodeToAdd);
		}
		tree = new JTree(root);
		tree.setBackground(color1);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setBackgroundSelectionColor(color2);
		renderer.setBackgroundNonSelectionColor(color1);
		tree.setCellRenderer(renderer);
		TreeListener treeListener = new TreeListener();
		tree.addTreeSelectionListener(treeListener);
		frame.add(tree);
	}

	private void addNewTreeNode() {
		DefaultMutableTreeNode nodeToAdd = new DefaultMutableTreeNode(
				savedFileName);
		root.add(nodeToAdd);
		((DefaultTreeModel) tree.getModel()).reload();
	}

	private void saveFileName() {
		String fileSaveList = "recentList.txt";
		try {
			FileWriter fileWriter = new FileWriter(fileSaveList, true);
			FileReader fileReader = new FileReader(fileSaveList);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			if (savedFileName != null) {
				String path = savedFileName;
				bufferedWriter.write(path);
				bufferedWriter.newLine();
				justAdded = false;
			}

			if (justAdded == false) {
				String sCurrentLine;
				while ((sCurrentLine = bufferedReader.readLine()) != null) {
					recentFiles.add(sCurrentLine);
				}
			}

			for (int i = 0; i < recentFiles.size(); i++) {
				System.out.println(i + recentFiles.get(i));
			}

			bufferedWriter.close();
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileSaveList + "'");
		} catch (IOException ex) {
			System.out.println("Error writing to file '" + fileSaveList + "'");
		}
	}

	private void openFromTree(String path) {
		if (path != null) {
			try {
				FileReader fileReader = new FileReader(path);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String sCurrentLine;
				String text = "";
				while ((sCurrentLine = bufferedReader.readLine()) != null) {
					text += sCurrentLine + "\n";
				}
				add(tabLabel + tabCount, text);
				tabCount++;
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(frame, "File does not exit.");
		}
	}

	private class TreeListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			TreePath[] paths = e.getPaths();
			for (int i = 0; i < paths.length; i++) {
				if (e.isAddedPath(i)) {
					System.out.println("This node has been selected: "
							+ paths[i]);
					TreePath path = paths[i];
					String toPath = path.toString();
					String[] toPath2 = toPath.split(", ");
					String finalPath = toPath2[1].replace("]", "");
					openFromTree(finalPath);
				} else {
					System.out.println("This node has been deselected: "
							+ paths[i]);
				}
			}
		}
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
				file = fileChooser.getSelectedFile();
				savedFileName = file.getAbsolutePath();
				recentFiles.add(savedFileName);
				saveFileName();
				addNewTreeNode();
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

	public static void main(String args[]) {
		Notepad notepad = new Notepad();
	}
}
