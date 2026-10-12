package  ui.register;

import javax.swing.JButton;

public class RegisterJButton extends JButton{

    
    public RegisterJButton(String text,int x,int y,int width,int height) {
        super(text);
        this.setBounds(x, y, width, height);
        this.setVisible(true);
    }
}
