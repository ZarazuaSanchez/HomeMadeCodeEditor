package codeeditor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class codeEditorGui extends JFrame implements ActionListener, DocumentListener {

	private JFrame f;
	//JTabbedPane tabs;
	JTextPane p;
	private JSplitPane splitPane;  // split the window in top and bottom
	
	//the tab pane
	JTabbedPane TabbedPane;
	JPanel jPanelFirst;	JTextPane consolePane;
	JPanel jPanelSecond;
	JPanel jPanelThird;
    
	private File currentFile = null;
	private File projectDir = null;
	//=================stuff to highlight keywords
	private DefaultStyledDocument document = new DefaultStyledDocument();
	private Pattern keywordPatt = keywordPattern();
	private Pattern arithmeticPatt = arithmeticPattern();
	private StyleContext styleContext = StyleContext.getDefaultStyleContext();
	private AttributeSet keyWords = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
	private AttributeSet arithmetic = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.RED);
	private AttributeSet normal = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLACK);

	public codeEditorGui() {
		f = new JFrame("Code Editor");
		p = new JTextPane(document);
		splitPane = new JSplitPane();
	  
		//Font font = new Font(Font.SANS_SERIF, 3, 20);		//uncomment this and the line below to see the color better
		//p.setFont(font);
		//tabs = new JTabbedPane(JTabbedPane.TOP);

		JMenuBar mb = new JMenuBar();

		// file tab items =======================================================

		JMenu fileMenu = new JMenu("File");
		//create menu items
		JMenuItem mi1 = new JMenuItem("New Project");
		JMenuItem mi2 = new JMenuItem("Open Project");
		JMenuItem mi3 = new JMenuItem("Save Project");
		JMenuItem mi4 = new JMenuItem("Close Project");
		JMenuItem mi5 = new JMenuItem("New File");
		JMenuItem mi6 = new JMenuItem("Open File");
		JMenuItem mi7 = new JMenuItem("Save File");
		JMenuItem mi8 = new JMenuItem("Close File");
		JMenuItem mi9 = new JMenuItem("Print");

		//Add action listener
		mi1.addActionListener(this);
		mi2.addActionListener(this);
		mi3.addActionListener(this);
		mi4.addActionListener(this);
		mi5.addActionListener(this);
		mi6.addActionListener(this);
		mi7.addActionListener(this);
		mi8.addActionListener(this);
		mi9.addActionListener(this);

		fileMenu.add(mi1);
		fileMenu.add(mi2);
		fileMenu.add(mi3);
		fileMenu.add(mi4);
		fileMenu.add(mi5);
		fileMenu.add(mi6);
		fileMenu.add(mi7);
		fileMenu.add(mi8);
		fileMenu.add(mi9);

		// run menu items =================================================

		JMenu runMenu = new JMenu("Run");
		
		JMenuItem com_and_run_button = new JMenuItem("Compile and Run");
		JMenuItem compile_button = new JMenuItem("Compile");
		JMenuItem run_button = new JMenuItem("Run");
		
		com_and_run_button.addActionListener(this);
		compile_button.addActionListener(this);
		run_button.addActionListener(this);
		
		runMenu.add(com_and_run_button);
		runMenu.add(compile_button);
		runMenu.add(run_button);

		// add everything to the frame ========================================

		mb.add(fileMenu);
		mb.add(runMenu);
		f.setJMenuBar(mb);
		f.add(splitPane);
		f.setSize(800, 500);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//creating the tabs
		TabbedPane = new JTabbedPane();
		jPanelFirst = new JPanel();		consolePane = new JTextPane();
		jPanelSecond = new JPanel();
		jPanelThird = new JPanel();
		
		// let's configure our splitPane:
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window verticaly
        splitPane.setDividerLocation(300);                    // the initial position of the divider is 200 (our window is 400 pixels high)
        splitPane.setTopComponent(p);                  // at the top we want our "topPanel"
        splitPane.setBottomComponent(TabbedPane);     // and at the bottom we want our "bottomPanel"

        //adding the different tabs to the tabbedPane
        //jPanelFirst.setLayout(null);
        //jPanelFirst.add(consolePane);
        TabbedPane.addTab("Console", consolePane);

        jPanelSecond.setLayout(null);
        TabbedPane.addTab("tab2", jPanelSecond);

        jPanelThird.setLayout(null);
        TabbedPane.addTab("tab3", jPanelThird);
	}


	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();

		//================================================== Project Events ============================================

		if (s.equals("New Project")) {                    //If user wants to make new project
			// Create an object of JFileChooser class
			JFileChooser j = new JFileChooser(":f");    //create j file chooser
			j.setDialogTitle("Select a project directory");                 //set the text at the top
			j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  //open a dialog to choose a directory
			j.setAcceptAllFileFilterUsed(false);                    //idk what this does
			int r = j.showSaveDialog(null);                 //show the file chooser and save dialogue.

			if (r == JFileChooser.APPROVE_OPTION) {      //if option is approved, i.e. user chose save button
				String path = j.getSelectedFile().getAbsolutePath();       //get the path of the file
				String main = path + "\\Main.java";      //make a path name for main
				String readme = path + "\\README.txt";  //add a path for readme
				String lib = path + "\\lib";            //add a path for lib directory
				//System.out.println(path);

				File mainf = new File(main);            //create main file variable
				File readmef = new File(readme);        //create readme file variable
				File libf = new File(lib);              //create lib directory variable
				try {
					boolean createdMain = mainf.createNewFile();
					boolean createdReadme = readmef.createNewFile();    //create the files and directory
					boolean createdLib = libf.mkdir();
				} catch (IOException ioe) {
					System.out.println("Error while creating project files :" + ioe);
				}

				projectDir = new File(path);            //set the project directory to the one selected.
				currentFile = mainf;
				f.setTitle(projectDir.getPath());
			}


		} else if (s.equals("Open Project")) {          //if user wants to open an existing project
			JFileChooser j = new JFileChooser(":f");
			j.setDialogTitle("Select directory of project to open");
			j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			//j.setAcceptAllFileFilterUsed(false);
			int r = j.showOpenDialog(null);                 //these lines open the save dialog

			if (r == JFileChooser.APPROVE_OPTION) {
				String projDirPath = j.getSelectedFile().getAbsolutePath();  //get path of existing project this
				projectDir = new File(projDirPath);     //set this as the current project
				f.setTitle(projectDir.getPath());
				// this should be a directory, else call open file.
				File projDir = new File(projDirPath);
				File[] filesInDir = projDir.listFiles();        //get all the filenames in the project
				String mainPath = j.getSelectedFile().getAbsolutePath() + "\\Main.java";     //look for main
				//System.out.println(mainPath);
				for (File file : filesInDir) {      //start searching for main
					System.out.println(file.getAbsolutePath());
					if (file.getAbsolutePath().equals(mainPath)) {
						System.out.println("inside");
						try {
							//FileReader
							FileReader fr = new FileReader(file);
							BufferedReader br = new BufferedReader(fr);
							String line = null;
							StringBuilder sb = new StringBuilder();
							while ((line = br.readLine()) != null) {
								sb.append(line + '\n');

							}
							p.setText(sb.toString());
							br.close();

							currentFile = new File(file.getAbsolutePath()); // sets the current open file to Main.
							// this will be used in "save project" function
							System.out.println(file.getAbsolutePath());
						} catch (Exception evt) {
							JOptionPane.showMessageDialog(f, evt.getMessage());
						}
					}
				}
			}
		} else if (s.equals("Save Project")) {      //saves the current file. not really different from save file. idk how to make this different.
			///*                                    //can't have more than 1 file open anyway so..
			if (projectDir != null) {
                /*
                File[] projectFiles = projectDir.listFiles();
                for( File file : projectFiles) {        // for all the files in the project
                    if(file.isFile()) {             // if it is actually a file, i.e, not a directory,

             */
				try {                     //save its contents.
					//JOptionPane.showMessageDialog(f, currentFile.getAbsolutePath());
					FileWriter wr = new FileWriter(currentFile, false);
					BufferedWriter w = new BufferedWriter(wr);

					w.write(p.getText());

					w.flush();
					w.close();
				} catch (Exception evt) {
					JOptionPane.showMessageDialog(f, evt.getMessage());
				}
				//}
				//}
				///*
			} else {
				JOptionPane.showMessageDialog(f, "You need to open or create a project to save one!\n if this is just a file, use save file.");
			}

			//*/
		} else if (s.equals("Close Project")) {
			if (projectDir != null) {
				int option = JOptionPane.showConfirmDialog(f, "Are you sure you want to close project?\n " +
						"any unsaved work will be lost.", "Closing project", JOptionPane.YES_NO_OPTION);
				//.out.println(option);
				if (option == 0) {    //if they say yes they want to close
					p.setText("");
					f.setTitle("Code Editor");
					projectDir = null;
					currentFile = null;
				}
			} else {
				JOptionPane.showMessageDialog(f, "No project is currently opened.");
			}
		}

		//=================================================== File Events ==============================================

		else if (s.equals("New File")) {  //makes a new file, adds it to project if one is open. else asks where to make it.
			//need to have something to save currently open files before making new one.
			String newFileName;
			String newPathName;
			File newFile;
			if (projectDir != null) {     // if we are currently in a project
				newFileName = JOptionPane.showInputDialog(f, "Enter a name for the new file:");
				if (newFileName != null) {    //if they didn't cancel
					newPathName = projectDir.getAbsolutePath() + "\\" + newFileName;    // need to handle bad characters in input.
					//also need to handle duplicate file names.
					newFile = new File(newPathName);   //make new file with appropriate name
					try {
						boolean createdNewFile = newFile.createNewFile();   //create file
						currentFile = newFile;  //set the current file to the one we just made
						p.setText("");      //reset text pane
					} catch (IOException ioe) {
						System.out.println("Error creating new file: " + ioe);
					}
				}

			} else {   // if we aren't in a project
				JFileChooser j = new JFileChooser(":f");
				j.setDialogTitle("Where do you want to make your file?");                 //set the text at the top
				j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  //open a dialog to choose a directory

				int r = j.showSaveDialog(null);                 //show the file chooser and save dialogue.
				if (r == JFileChooser.APPROVE_OPTION) {   //if the pick a place
					String newFileDir = j.getSelectedFile().getAbsolutePath();
					newFileName = JOptionPane.showInputDialog(f, "Enter a name for the new file");
					if (newFileName != null) {
						newPathName = newFileDir + "\\" + newFileName;
						newFile = new File(newPathName);   //make new file with appropriate name
						try {
							boolean createdNewFile = newFile.createNewFile();   //create file
							currentFile = newFile;  //set current file to the one we just created
							p.setText("");      //delete all text in the pane.
						} catch (IOException ioe) {
							System.out.println("Error creating new file: " + ioe);
						}
					}

				}
			}
		} else if (s.equals("Open File")) {
			JFileChooser j;
			if (projectDir == null) {     // if we don't have a project directory, just open the file
				j = new JFileChooser(":f");
			} else {       //else
				j = new JFileChooser(projectDir.getAbsolutePath());
			}
			j.setDialogTitle("Open which file?");

			int r = j.showOpenDialog(f);            //need to change currentProject if this isn't in the project
			if (r == JFileChooser.APPROVE_OPTION) {
				File file = new File(j.getSelectedFile().getAbsolutePath());
				if (file.exists() && !file.isDirectory()) {
					try {
						//FileReader
						FileReader fr = new FileReader(file);
						BufferedReader br = new BufferedReader(fr);
						String line = null;
						StringBuilder sb = new StringBuilder();
						while ((line = br.readLine()) != null) {
							sb.append(line + '\n');

						}
						p.setText(sb.toString());
						br.close();

						currentFile = new File(file.getAbsolutePath()); // sets the current open file to Main.
					} catch (Exception evt) {
						JOptionPane.showMessageDialog(f, evt.getMessage());
					}
				}
			}
		} else if (s.equals("Save File")) {
			if (currentFile == null) {
				JOptionPane.showMessageDialog(f, "Open a file to save one. We don't have save as yet.");
			} else {
				try {                     //save its contents.
					//JOptionPane.showMessageDialog(f, currentFile.getAbsolutePath());
					FileWriter wr = new FileWriter(currentFile, false);
					BufferedWriter w = new BufferedWriter(wr);

					w.write(p.getText());

					w.flush();
					w.close();
				} catch (Exception evt) {
					JOptionPane.showMessageDialog(f, evt.getMessage());
				}
			}
		} else if (s.equals("Close File")) {
			if (currentFile != null) {
				int option = JOptionPane.showConfirmDialog(f, "Are you sure you want to close project?\n " +
						"any unsaved work will be lost.", "Closing project", JOptionPane.YES_NO_OPTION);
				//.out.println(option);
				if (option == 0) {    //if they say yes they want to close
					p.setText("");
					f.setTitle("Code Editor");
					currentFile = null;
				}
			} else {
				JOptionPane.showMessageDialog(f, "No File is currently opened.");
			}
		}
		else if(s.equals("Compile and Run")){		// compiles and runs the current file
			if (currentFile != null){
				String filePath = currentFile.getParent();
				String fileName = currentFile.getName();
				String className = fileName.substring(0, fileName.length() - 5); // trims ".java" off the string, so we actually run the class
				System.out.println("path: " + filePath + "  name: " + fileName);
				compileFile(fileName, filePath);
				runFile(className, filePath);
			}
			else{
				JOptionPane.showMessageDialog(f, "No file is open, open a file to compile and run it :)");
			}
		}
		else if(s.equals("Compile")){				// compiles the current file
			if (currentFile != null){
				String filePath = currentFile.getParent();
				String fileName = currentFile.getName();
				System.out.println("path: " + filePath + "  name: " + fileName);
				compileFile(fileName, filePath);
			}
			else{
				JOptionPane.showMessageDialog(f, "No file is open, open a file to compile and run it :)");
			}
		}
		else if(s.equals("Run")){
			if (currentFile != null){
				String filePath = currentFile.getParent();
				String fileName = currentFile.getName();
				String className = fileName.substring(0, fileName.length() - 5);
				System.out.println("path: " + filePath + "  name: " + fileName);
				runFile(className, filePath);
			}
			else{
				JOptionPane.showMessageDialog(f, "No file is open, open a file to compile and run it :)");
			}
		}
	}

	@Override
	public void insertUpdate(DocumentEvent d) {		//these functions handle when the file is updated in order to
		//System.out.println(d.getOffset());		//change color of keywords.
		//System.out.println("inserted text");

		handleTextChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent d) {
		//System.out.println("removed text");
		handleTextChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent d) {
		//System.out.println("changedUpdate");
	}

	private Pattern keywordPattern() {				//these two functions create patterns to locate the keywords
		StringBuilder sb = new StringBuilder();		//using a "matcher" class. i don't fully understand it either
		String[] keyWords = new String[]{"if", "else", "for", "while"};
		for (String word : keyWords) {
			sb.append("\\b");   //start of word boundary
			sb.append(word);
			sb.append("\\b|");  //end of word boundary
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);   //Remove the trailing "|";
		}

		Pattern p = Pattern.compile(sb.toString());

		return p;
	}

	private Pattern arithmeticPattern() {
		StringBuilder sb = new StringBuilder();
		String[] keyWords = new String[]{"\\+", "\\-", "\\*", "\\/", "\\|\\|", "&&", "<", ">", "<=", ">=", "==", "!="};
		for (String word : keyWords) {
			sb.append("\\B");   //start of word boundary
			sb.append(word);
			sb.append("\\B|");  //end of word boundary
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);   //Remove the trailing "|";
		}

		Pattern p = Pattern.compile(sb.toString());

		return p;
	}

	//this is the function that actually changes the colors.
	private void updateKeywordStyles(StyledDocument doc, AttributeSet normal, AttributeSet keyword, AttributeSet arithmetic) throws BadLocationException {
		//System.out.println("updating keyword styles");
		doc.setCharacterAttributes(0, p.getText().length(), normal, true);

		Matcher keywordMatcher = keywordPatt.matcher(p.getDocument().getText(0,p.getDocument().getLength()));
		Matcher arithmeticMatcher = arithmeticPatt.matcher(p.getDocument().getText(0,p.getDocument().getLength()));
		while(keywordMatcher.find()){
			//changed the color of keywords
			doc.setCharacterAttributes(keywordMatcher.start(), keywordMatcher.end() - keywordMatcher.start() , keyword, false);
		}
		while(arithmeticMatcher.find()){
			//changed the color of keywords
			doc.setCharacterAttributes(arithmeticMatcher.start(), arithmeticMatcher.end() - arithmeticMatcher.start() , arithmetic, false);
		}
		//System.out.println("done updating keyword styles");
	}

	private void handleTextChanged(){
		//System.out.println("handling text change");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					updateKeywordStyles(document, normal, keyWords, arithmetic);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
		//System.out.println("done handling text change");
	}

	// writes lines to the console when compiling or running
	private void printLines(String command, String name, InputStream ins)throws Exception {
		String line;
		BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		StyledDocument doc = consolePane.getStyledDocument();
		while((line = in.readLine()) != null){
			try {
				doc.insertString(doc.getLength(), "\n" + name + " " + line, null);
			} catch(BadLocationException e){
				e.printStackTrace();
			}
		}
	}
	
	// runs a process, writing stdout and stderr to the "console". will be used for compiling and running
	private void runProcess(String command, String args, String path){
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command, args);		// set the command the process will execute
		builder.directory(new File(path));		// set the working directory for the process
		Process p;
		StyledDocument doc = consolePane.getStyledDocument();

		try{
			doc.insertString(doc.getLength(), "\n\n======== running: " + command + " " + args, null);
			p = builder.start();	//start the process
			printLines(command,args + " stdout: ", p.getInputStream());
			printLines(command, args + " stderr: ", p.getErrorStream());
			p.waitFor();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void compileFile(String name, String path){
		runProcess("javac", name, path);
	}
	private void runFile(String name, String path){
		runProcess("java", name, path);
	}
}
