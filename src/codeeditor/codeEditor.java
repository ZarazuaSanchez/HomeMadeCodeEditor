package codeeditor;

import javax.swing.JFrame;

public class codeEditor {

	public static void main(String[] args) {
		codeEditorGui gui = new codeEditorGui();
		gui.setTitle("Home-Made Code Editor");
		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
