package ui.memberpage;
import java.awt.Color;
import java.awt.HeadlessException;
import javax.swing.JFrame;


public class MemberOperatingWindow extends JFrame{
    public MemberOperatingWindow(String title,int x,int y,int width,int height) throws HeadlessException {
        this.setTitle(title);
        this.setBounds(x, y, width, height);
        this.getContentPane().setBackground(Color.decode("#006915"));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); 
        this.setLayout(null);
    }

    public MemberOperatingWindow() throws HeadlessException {
    }
}
