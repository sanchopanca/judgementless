package engineer.kovalev.judgmentless;


import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class GUI {
    public JEditorPane textArea;
    public GUI() {
        JFrame jFrame = new JFrame("JFrame");
        jFrame.setSize(275, 100);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JEditorPane();
        textArea.setEditable(false);

        jFrame.add(textArea);
        jFrame.setVisible(true);
    }
}
