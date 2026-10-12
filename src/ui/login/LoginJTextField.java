package ui.login;

import java.awt.Color;
import javax.swing.JTextField;

public class LoginJTextField extends JTextField {

    public LoginJTextField(String title,int x, int y, int width, int height) {
        this.setText(title);
        this.setBounds(x, y, width, height);
        this.setForeground(Color.DARK_GRAY);
        this.setBackground(Color.WHITE);
        this.setFont(new java.awt.Font("仿宋",java.awt.Font.ITALIC, 20));
        this.setVisible(true);
    }
}