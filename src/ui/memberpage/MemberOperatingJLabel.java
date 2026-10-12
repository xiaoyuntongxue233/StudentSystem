package ui.memberpage;

import javax.swing.JLabel;
import java.awt.Color;

public class MemberOperatingJLabel extends  JLabel{

    public MemberOperatingJLabel(String text,int x,int y,int width,int height) {
        this.setText(text);
        this.setBounds(x, y, width, height);
        this.setVisible(true);
        this.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 20));
        this.setForeground(Color.WHITE);
    }

    
    

   


    
}

