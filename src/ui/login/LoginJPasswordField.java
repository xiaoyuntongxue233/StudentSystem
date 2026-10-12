package ui.login;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JPasswordField;

public class LoginJPasswordField extends JPasswordField {

    public LoginJPasswordField(String title, int x, int y, int width, int height) {
        this.setText(title);
        this.setBounds(x, y, width, height);
        this.setForeground(Color.DARK_GRAY);
        this.setBackground(Color.WHITE);
        this.setFont(new Font("Dialog", Font.ITALIC, 20));
        this.setVisible(true);
    }
}
