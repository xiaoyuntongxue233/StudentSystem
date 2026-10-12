package  ui.login;

import javax.swing.JButton;

public class LoginJButton extends JButton{

    
    public LoginJButton(String text,int x,int y,int width,int height) {
        super(text);
        this.setBounds(x, y, width, height);
        this.setVisible(true);
    }
}
