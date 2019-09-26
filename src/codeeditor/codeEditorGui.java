package codeeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class codeEditorGui extends JFrame {

	private JPanel contentPane;
	String filename;
	private JTextArea textArea;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					codeeditor.codeEditorGui frame = new codeeditor.codeEditorGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public codeEditorGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 859, 542);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(SystemColor.activeCaptionBorder);
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem openFile = new JMenuItem("Open Project");
		codeeditor.codeEditorGui.openFileAction ofAction = new codeeditor.codeEditorGui.openFileAction();
		openFile.addActionListener(ofAction);

		JMenuItem mntmCreateProject = new JMenuItem("Create Project");
		mntmCreateProject.addActionListener(new codeeditor.codeEditorGui.createProjectAction());

		JMenuItem saveFileAs = new JMenuItem("Save Project");
		saveFileAs.addActionListener(new codeeditor.codeEditorGui.saveFileAction());

		JMenuItem closeFile = new JMenuItem("Close Project");
		closeFile.addActionListener(new codeeditor.codeEditorGui.CloseProjectAction());
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem removeFile = new JMenuItem("Remove File");
		//removeFile.addActionListener(new codeeditor.codeEditorGui.CloseProjectAction());

		mnFile.add(mntmCreateProject);
		mnFile.add(openFile);
		mnFile.add(saveFileAs);
		mnFile.add(closeFile);
		
		mnEdit.add(removeFile);


		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
// contentPane.setLayout(null);

		textArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(textArea);
		contentPane.add(scroll, BorderLayout.CENTER);


	}

	private class openFileAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			FileDialog fileDialog = new FileDialog(codeeditor.codeEditorGui.this, "Choose a File", FileDialog.LOAD);
			fileDialog.setVisible(true);

			if(fileDialog.getFile() != null) {
				filename = fileDialog.getDirectory() + fileDialog.getFile();
				setTitle(filename);
			}

			try {
				BufferedReader reader = new BufferedReader(new FileReader(filename));
				StringBuilder sb = new StringBuilder();

				String line = null;

				while((line = reader.readLine()) != null ) {
					sb.append(line + "\n");
					textArea.setText(sb.toString());
				}

				reader.close();

			} catch(IOException err) {
				System.out.println("File Not Found");
			}
		}
	}

	private class saveFileAction implements ActionListener {

		public void actionPerformed (ActionEvent e) {
			FileDialog fileDialog = new FileDialog(codeeditor.codeEditorGui.this, "Save File", FileDialog.SAVE);
			fileDialog.setVisible(true);

			if(fileDialog.getFile() != null) {
				filename = fileDialog.getDirectory() + fileDialog.getFile();
				setTitle(filename);
			}

			try {
				FileWriter fw = new FileWriter(filename);
				fw.write(textArea.getText());
				setTitle(filename);
				fw.close();
			} catch(IOException err) {
				System.out.println("File not Found.");
			}
		}
	}




	private class CloseProjectAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			setTitle("Home-Made Code Editor");
			textArea.setText(null);
			//System.exit(0);
		}
	}






	private class createProjectAction implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			String projName = JOptionPane.showInputDialog("Enter project name:");

			Path dir = Paths.get(System.getProperty("user.home"), projName);

			if(!Files.exists(dir)) {
				try {
					Files.createDirectories(dir);
					filename = Paths.get(dir.toString(), "unnamed").toString();
					setTitle(filename);
					textArea.setText("");
				} catch(IOException e) {
					JOptionPane.showMessageDialog(codeeditor.codeEditorGui.this, "Project folder could not be made!");
				}
			} else {
				JOptionPane.showMessageDialog(codeeditor.codeEditorGui.this, "Project Folder exists already!");
			}

		}
	}



}