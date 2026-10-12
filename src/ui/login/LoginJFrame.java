package ui.login;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;

import ui.adminpage.AdminOperatingJFrame;
import ui.memberpage.MemberOperatingJFrame;
import ui.guestpage.GuestOperatingJFrame;
import ui.register.RegisterJFrame;
import thingstodo.*;
import chain.UserLinkedList;
import job.User;
import job.Student;
import storage.DataStore;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class LoginJFrame extends LoginWindow {

    public static UserLinkedList userList = new UserLinkedList();
    public static ActivityList activityList = new ActivityList();
    public static final String USER_FILE = "users.dat";
    public static final String ACTIVITY_FILE = "activities.dat";

    static {
        ArrayList<User> savedUsers = DataStore.load(USER_FILE);

        if (savedUsers != null) {
            userList = new UserLinkedList();
            for (User u : savedUsers) {
                userList.addUser(u);
            }
        } else {
            userList.addUser(new Student("管理组", 0, "管理", 30, "男", "系统管理员",
                    "13800000000", "admin", "123456", User.Role.Admin, "123456"));
            userList.addUser(new Student("一班", 1, "计算机", 20, "男", "成员甲",
                    "13800000001", "member", "123456", User.Role.Member, null));
            userList.addUser(new Student("二班", 1, "计算机", 19, "女", "访客甲",
                    "13800000002", "guest", "123456", User.Role.Guest, null));
            saveUsers();
        }

        ArrayList<Activity> savedActs = DataStore.load(ACTIVITY_FILE);
        System.out.println("===== 活动数：" + (savedActs == null ? "null" : savedActs.size()) + " =====");
        if (savedActs != null) {
            activityList = new ActivityList();
            for (Activity a : savedActs) {
                activityList.add(a);
            }
        } else {
            activityList = new ActivityList();
            saveActivities();
        }
    }

    public static void saveUsers() {
        DataStore.save(USER_FILE, userList.toList());
    }

    public static void saveActivities() {
        DataStore.save(ACTIVITY_FILE, activityList.getAll());
    }

    private LoginJTextField idJTextField;
    private LoginJPasswordField passwordJPasswordField;
    private LoginJTextField identifyJTextField;
    private LoginJButton loginJButton;
    private LoginJButton registerJButton;
    private LoginJButton passwordVisibleJButton;
    private static JudgePasswordWaitingTime waitingTime = new JudgePasswordWaitingTime();
    private char defaultEchoChar;
    private char[] currentCode;
    private int restTrial = 5;
    private boolean locked = false;
    private int restSeconds = 0;
    private Timer lockTimer;
    private LoginJLabel identifyNumJLabel;
    private boolean passwordVisible = false;

    public LoginJFrame() throws HeadlessException {

        super("登录界面", 200, 100, 1000, 800);

        LoginJLabel welcomeJLabel = new LoginJLabel("欢迎来到小型学生管理系统", 320, 100, 360, 30);
        welcomeJLabel.setForeground(Color.WHITE);
        welcomeJLabel.setFont(new java.awt.Font("黑体", java.awt.Font.BOLD, 28));

        LoginJLabel loginHintJLabel = new LoginJLabel("登录界面", 445, 160, 360, 30);
        loginHintJLabel.setForeground(Color.WHITE);
        loginHintJLabel.setFont(new java.awt.Font("黑体", java.awt.Font.BOLD, 24));

        LoginJLabel idJLabel = new LoginJLabel("输入ID：", 200, 305, 150, 20);
        LoginJLabel passwordJLabel = new LoginJLabel("输入密码：", 200, 365, 150, 20);
        LoginJLabel identifyJLabel = new LoginJLabel("输入验证码：", 200, 425, 150, 20);
        identifyNumJLabel = new LoginJLabel("点击以生成验证码", 550, 425, 150, 18);

        identifyNumJLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentCode = IdentifyingCode.generate();
                identifyNumJLabel.setText(new String(currentCode));
            }
        });

        idJLabel.setForeground(Color.WHITE);
        passwordJLabel.setForeground(Color.WHITE);
        identifyJLabel.setForeground(Color.WHITE);
        identifyNumJLabel.setForeground(Color.WHITE);

        idJLabel.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 20));
        passwordJLabel.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 20));
        identifyJLabel.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 20));
        identifyNumJLabel.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 18));

        this.add(welcomeJLabel);
        this.add(loginHintJLabel);
        this.add(idJLabel);
        this.add(passwordJLabel);
        this.add(identifyJLabel);
        this.add(identifyNumJLabel);

        idJTextField = new LoginJTextField("", 350, 300, 300, 30);
        passwordJPasswordField = new LoginJPasswordField("", 350, 360, 300, 30);
        identifyJTextField = new LoginJTextField("", 350, 420, 150, 30);

        this.add(idJTextField);
        this.add(passwordJPasswordField);
        this.add(identifyJTextField);
        defaultEchoChar = passwordJPasswordField.getEchoChar();

        passwordVisibleJButton = new LoginJButton("显示", 650, 360, 80, 30);
        loginJButton = new LoginJButton("登录", 350, 600, 100, 50);
        registerJButton = new LoginJButton("注册", 530, 600, 100, 50);

        passwordVisibleJButton.setBackground(Color.decode("#dddddd"));
        passwordVisibleJButton.setFont(new Font("等线", java.awt.Font.PLAIN, 16));
        passwordVisibleJButton.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordJPasswordField.setEchoChar((char) 0);
                passwordVisibleJButton.setText("隐藏");
            } else {
                passwordJPasswordField.setEchoChar(defaultEchoChar);
                passwordVisibleJButton.setText("显示");
            }
        });

        loginJButton.setBackground(Color.decode("#AAEEFF"));
        loginJButton.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 20));
        loginJButton.addActionListener(e -> {
            if (locked) {
                JOptionPane.showMessageDialog(this,
                        "密码错误次数过多，请等待 " + formatTime(restSeconds) + " 后重试！",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = getId();
            String password = getPassword();
            char[] input = getIdentify().toCharArray();

            if (!userList.containsId(id)) {
                JOptionPane.showMessageDialog(this, "ID不存在！",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (currentCode == null) {
                JOptionPane.showMessageDialog(this, "请先生成验证码！",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!IdentifyingCode.isEqual(input, currentCode)) {
                JOptionPane.showMessageDialog(this, "验证码错误！",
                        "提示", JOptionPane.WARNING_MESSAGE);
                currentCode = IdentifyingCode.generate();
                identifyNumJLabel.setText(new String(currentCode));
                return;
            }

            if (!userList.checkPassword(id, password)) {
                restTrial--;
                if (restTrial <= 0) {
                    lockLogin();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "密码错误！剩余 " + restTrial + " 次机会输入密码！",
                            "提示", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }

            User user = userList.findUser(id).data;
            User.Role role = userList.findUser(id).data.getRole();
            if (role == User.Role.Admin) {
                new AdminOperatingJFrame(user) {
                };
                dispose();
            } else if (role == User.Role.Member) {
                new MemberOperatingJFrame(user) {
                };
                dispose();
            } else if (role == User.Role.Guest) {
                new GuestOperatingJFrame(user) {
                };
                dispose();
            }

        });
        registerJButton.setBackground(Color.decode("#fff94c"));
        registerJButton.setFont(new java.awt.Font("等线", java.awt.Font.BOLD, 20));
        registerJButton.addActionListener(e -> {
            new RegisterJFrame(this.userList) {
            };
            dispose();
        });

        this.add(passwordVisibleJButton);
        this.add(loginJButton);
        this.add(registerJButton);

        this.getRootPane().setDefaultButton(loginJButton);

        this.setVisible(true);
    }

    public String getId() {
        return idJTextField.getText();
    }

    public String getPassword() {
        return new String(passwordJPasswordField.getPassword());
    }

    public String getIdentify() {
        return identifyJTextField.getText();
    }

    public LoginJButton getLoginButton() {
        return loginJButton;
    }

    public LoginJButton getRegisterButton() {
        return registerJButton;
    }

    private void lockLogin() {
        locked = true;
        restSeconds = waitingTime.getLockSeconds();

        JOptionPane.showMessageDialog(this,
                "密码错误次数过多，请等待 " + formatTime(restSeconds) + " 后重试！",
                "账号已锁定", JOptionPane.ERROR_MESSAGE);

        if (lockTimer != null && lockTimer.isRunning()) {
            lockTimer.stop();
        }
        lockTimer = new Timer(1000, ev -> {
            restSeconds--;
            if (restSeconds <= 0) {
                lockTimer.stop();
                locked = false;
                restTrial = 5;
                loginJButton.setEnabled(true);
            }
        });
        lockTimer.start();
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%d分%02d秒", m, s);
    }
}
