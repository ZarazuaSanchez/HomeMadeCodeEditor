package codeeditor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import java.awt.Color;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
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
					codeEditorGui frame = new codeEditorGui();
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
		openFileAction ofAction = new openFileAction();
		openFile.addActionListener(ofAction);
		
		JMenuItem mntmCreateProject = new JMenuItem("Create Project");
		mntmCreateProject.addActionListener(new createProjectAction());
		
		JMenuItem saveFileAs = new JMenuItem("Save Project");
		saveFileAs.addActionListener(new saveFileAction());
		
		mnFile.add(mntmCreateProject);
		mnFile.add(openFile);
		mnFile.add(saveFileAs);
		
		
	
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
//		contentPane.setLayout(null);
		
		textArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(textArea);
		contentPane.add(scroll, BorderLayout.CENTER);
		
		
	}
	
	private class openFileAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			FileDialog fileDialog = new FileDialog(codeEditorGui.this, "Choose a File", FileDialog.LOAD);
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
			FileDialog fileDialog = new FileDialog(codeEditorGui.this, "Save File", FileDialog.SAVE);
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
					JOptionPane.showMessageDialog(codeEditorGui.this, "Project folder could not be made!");
				}
			} else {
				JOptionPane.showMessageDialog(codeEditorGui.this, "Project Folder exists already!");
			}
					
		}
	}
	
	
	
}
