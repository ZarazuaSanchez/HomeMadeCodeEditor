package codeeditor;

import javax.swing.JFrame;
import javax.swing.text.Document;

public class codeEditor {

	public static void main(String[] args) {
		codeEditorGui gui = new codeEditorGui();
		Document doc = gui.p.getDocument();
		doc.addDocumentListener(gui);
	}

}
