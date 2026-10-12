package ui.register;

import job.User;
import chain.UserLinkedList;
import ui.login.LoginJFrame;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.*;

public class RegisterJFrame extends RegisterWindow {

    private UserLinkedList userList;
    private JPanel jPanel;
    private RegisterJButton adminBtn;
    private RegisterJButton guestBtn;

    public RegisterJFrame(UserLinkedList userList) throws HeadlessException {
        super("操作面板", 200, 200, 1000, 800);
        this.userList = userList;
        setJPanel();

        RegisterJLabel welcomeJLabel = new RegisterJLabel("欢迎来到小型学生管理系统", 320, 100, 360, 30);
        welcomeJLabel.setForeground(Color.WHITE);
        welcomeJLabel.setFont(new java.awt.Font("黑体", java.awt.Font.BOLD, 28));

        RegisterJLabel loginHintJLabel = new RegisterJLabel("注册界面", 445, 160, 360, 30);
        loginHintJLabel.setForeground(Color.WHITE);
        loginHintJLabel.setFont(new java.awt.Font("黑体", java.awt.Font.PLAIN, 24));

        RegisterJLabel buttonHintJLabel = new RegisterJLabel("请点击要注册的成员类型", 375, 300, 500, 30);
        buttonHintJLabel.setForeground(Color.WHITE);
        buttonHintJLabel.setFont(new java.awt.Font("等线", java.awt.Font.PLAIN, 20));

        this.add(welcomeJLabel);
        this.add(loginHintJLabel);
        this.add(buttonHintJLabel);

        adminBtn = new RegisterJButton("管理员", 350, 200, 80, 40);
        adminBtn.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 14));
        adminBtn.setBackground(Color.decode("#bbffaa"));
        adminBtn.addActionListener(e -> {
            showAdminForm();
            this.remove(buttonHintJLabel);
        });

        guestBtn = new RegisterJButton("访客", 550, 200, 80, 40);
        guestBtn.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 14));
        guestBtn.setBackground(Color.decode("#bbffaa"));
        guestBtn.addActionListener(e -> {
            showGuestForm();
            this.remove(buttonHintJLabel);
        });

        this.add(adminBtn);
        this.add(guestBtn);

        RegisterJButton loginJButton = new RegisterJButton("返回登录页面", 380, 650, 200, 50);
        loginJButton.setBackground(Color.decode("#AAEEFF"));
        loginJButton.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 20));
        loginJButton.addActionListener(e -> {
            new LoginJFrame();
            this.dispose();
        });
        this.add(loginJButton);

        this.setVisible(true);
    }

    private void setJPanel() {
        jPanel = new JPanel();
        jPanel.setBounds(0, 250, 1000, 500);
        jPanel.setLayout(null);
        jPanel.setOpaque(false);
        this.add(jPanel);
    }

    private JTextField addRow(String labelText, int lx, int ly, int fx, int fy, int w, int h, String name) {
        RegisterJLabel label = new RegisterJLabel(labelText, lx, ly, 200, 30);
        jPanel.add(label);

        JTextField field = new JTextField();
        field.setName(name);
        field.setBounds(fx, fy, w, h);
        jPanel.add(field);
        return field;
    }

    private void showAdminForm() {
        jPanel.removeAll();
        addRow("输入管理员ID：", 200, 0, 380, 0, 200, 30, "adminId");
        addRow("输入管理员口令：", 200, 50, 380, 50, 200, 30, "adminToken");
        addRow("输入一级密码：", 200, 100, 380, 100, 200, 30, "password1");
        addRow("再次输入一级密码：", 200, 150, 380, 150, 200, 30, "confirmPassword1");
        addRow("输入二级密码：", 200, 200, 380, 200, 200, 30, "password2");
        addRow("再次输入二级密码：", 200, 250, 380, 250, 200, 30, "confirmPassword2");

        RegisterJButton adminRegisterJButton = new RegisterJButton("注册", 420, 320, 120, 45);
        adminRegisterJButton.setFont(new java.awt.Font("等线", java.awt.Font.PLAIN, 20));
        adminRegisterJButton.setBackground(Color.decode("#fff94c"));
        adminRegisterJButton.addActionListener(e -> handleAdminRegister());

        this.getRootPane().setDefaultButton(adminRegisterJButton);
        jPanel.add(adminRegisterJButton);
        jPanel.revalidate();
        jPanel.repaint();
    }

    private void showGuestForm() {
        jPanel.removeAll();
        addRow("输入访客ID：", 200, 0, 380, 0, 200, 30, "guestId");
        addRow("输入密码：", 200, 100, 380, 100, 200, 30, "password");
        addRow("再次输入密码：", 200, 150, 380, 150, 200, 30, "confirmPassword");

        RegisterJButton guestRegisterJButton = new RegisterJButton("注册", 420, 320, 120, 45);
        guestRegisterJButton.setFont(new java.awt.Font("等线", java.awt.Font.PLAIN, 20));
        guestRegisterJButton.setBackground(Color.decode("#fff94c"));
        guestRegisterJButton.addActionListener(e -> handleGuestRegister());

        this.getRootPane().setDefaultButton(guestRegisterJButton);
        jPanel.add(guestRegisterJButton);
        jPanel.revalidate();
        jPanel.repaint();
    }

    private String getValue(String name) {
        for (Component c : jPanel.getComponents()) {
            if (c instanceof JTextField && name.equals(c.getName())) {
                return ((JTextField) c).getText().trim();
            }
        }
        return null;
    }

    private String getConfiguredAdminToken() {
        String token = System.getenv("STUDENT_SYSTEM_ADMIN_TOKEN");
        if (token == null || token.isBlank()) {
            token = System.getProperty("student.system.admin.token");
        }
        return token == null || token.isBlank() ? null : token;
    }

    private void handleAdminRegister() {
        String id = getValue("adminId");
        String token = getValue("adminToken");
        String pwd1 = getValue("password1");
        String pwd1c = getValue("confirmPassword1");
        String pwd2 = getValue("password2");
        String pwd2c = getValue("confirmPassword2");

        if (id.isEmpty() || pwd1.isEmpty() || pwd2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "存在未填入的内容，请重新检查！");
            return;
        }
        String configuredToken = getConfiguredAdminToken();
        if (configuredToken == null) {
            JOptionPane.showMessageDialog(this,
                    "Admin token is not configured. Set STUDENT_SYSTEM_ADMIN_TOKEN or student.system.admin.token.");
            return;
        }
        if (!configuredToken.equals(token)) {
            JOptionPane.showMessageDialog(this, "Admin口令错误！");
            return;
        }
        if (!pwd1.equals(pwd1c)) {
            JOptionPane.showMessageDialog(this, "两次密码不一致,请重新检查！");
            return;
        }
        if (!pwd2.equals(pwd2c)) {
            JOptionPane.showMessageDialog(this, "两次密码不一致,请重新检查！");
            return;
        }
        if (userList.containsId(id)) {
            JOptionPane.showMessageDialog(this, "该ID已存在！");
            return;
        }

        User admin = new User(id, pwd1, User.Role.Admin, pwd2);
        userList.addUser(admin);
        LoginJFrame.saveUsers();

        JOptionPane.showMessageDialog(this, "管理员注册成功！请牢记你的密码！");
        Timer timer = new Timer(3000, e -> {
            dispose();
            new LoginJFrame();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void handleGuestRegister() {
        String id = getValue("guestId");
        String pwd = getValue("password");
        String pwdc = getValue("confirmPassword");

        if (id.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "存在未填入的内容，请重新检查！");
            return;
        }
        if (!pwd.equals(pwdc)) {
            JOptionPane.showMessageDialog(this, "两次密码不一致,请重新检查！");
            return;
        }
        if (userList.containsId(id)) {
            JOptionPane.showMessageDialog(this, "该ID已存在！");
            return;
        }

        User admin = new User(id, pwd, User.Role.Guest, null);
        userList.addUser(admin);
        LoginJFrame.saveUsers();

        JOptionPane.showMessageDialog(this, "访客注册成功！请牢记你的密码！");
        Timer timer = new Timer(3000, e -> {
            dispose();
            new LoginJFrame();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
