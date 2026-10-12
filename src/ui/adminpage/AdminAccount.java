package ui.adminpage;

import job.Student;
import job.User;
import ui.login.LoginJFrame;

import javax.swing.*;
import java.awt.*;

public class AdminAccount extends JDialog {

    private final User user;

    private JTextField idField;
    private JTextField nameField;
    private JTextField ageField;
    private JComboBox<String> genderBox;
    private JTextField phoneField;
    private JTextField classField;
    private JTextField majorField;

    private JPasswordField passwordField;
    private JPasswordField secondPasswordField;
    private JButton togglePasswordBtn;
    private JButton toggleSecondPasswordBtn;

    private final String originalPassword;
    private final String originalSecondPassword;
    private final String maskedPassword;
    private final String maskedSecondPassword;
    private boolean saved = false;

    private boolean passwordVisible = false;
    private boolean secondPasswordVisible = false;

    public AdminAccount(Window owner, User user) {
        super(owner, "我的账户信息", ModalityType.APPLICATION_MODAL);
        this.user = user;

        this.originalPassword = user.getPassword();
        this.originalSecondPassword = user.getSecondPassword();
        this.maskedPassword = maskPassword(originalPassword);
        this.maskedSecondPassword = maskPassword(originalSecondPassword);

        setSize(450, 520);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int y = 0;

        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("ID："), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setText(user.getId());
        idField.setEditable(false);
        idField.setBackground(new Color(0xEEEEEE));
        add(idField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("姓名："), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        add(nameField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("年龄："), gbc);
        gbc.gridx = 1;
        ageField = new JTextField(20);
        add(ageField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("性别："), gbc);
        gbc.gridx = 1;
        genderBox = new JComboBox<>(new String[] { "男", "女", "其他" });
        add(genderBox, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("手机号："), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        add(phoneField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("班级："), gbc);
        gbc.gridx = 1;
        classField = new JTextField(20);
        add(classField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("专业："), gbc);
        gbc.gridx = 1;
        majorField = new JTextField(20);
        add(majorField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("一级密码："), gbc);
        gbc.gridx = 1;
        JPanel pwd1Panel = new JPanel(new BorderLayout(5, 0));
        passwordField = new JPasswordField(20);
        passwordField.setEchoChar('*');
        passwordField.setText(maskedPassword);
        togglePasswordBtn = new JButton("显示");
        togglePasswordBtn.setPreferredSize(new Dimension(70, 28));
        togglePasswordBtn.addActionListener(e -> togglePassword());
        pwd1Panel.add(passwordField, BorderLayout.CENTER);
        pwd1Panel.add(togglePasswordBtn, BorderLayout.EAST);
        add(pwd1Panel, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("二级密码："), gbc);
        gbc.gridx = 1;
        JPanel pwd2Panel = new JPanel(new BorderLayout(5, 0));
        secondPasswordField = new JPasswordField(20);
        secondPasswordField.setEchoChar((char) 0);
        secondPasswordField.setText(maskedSecondPassword);
        toggleSecondPasswordBtn = new JButton("显示");
        toggleSecondPasswordBtn.setPreferredSize(new Dimension(70, 28));
        toggleSecondPasswordBtn.addActionListener(e -> toggleSecondPassword());
        pwd2Panel.add(secondPasswordField, BorderLayout.CENTER);
        pwd2Panel.add(toggleSecondPasswordBtn, BorderLayout.EAST);
        add(pwd2Panel, gbc);

        if (user.getRole() != User.Role.Admin) {
            toggleSecondPasswordBtn.setEnabled(false);
            secondPasswordField.setEnabled(false);
            secondPasswordField.setText("");
        }

        boolean isStudent = user instanceof Student;
        if (!isStudent) {
            classField.setEnabled(false);
            majorField.setEnabled(false);
        }

        y++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        saveBtn.addActionListener(e -> onSave());
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        fillFields();

        getRootPane().setDefaultButton(saveBtn);
    }

    private String maskPassword(String pwd) {
        if (pwd == null || pwd.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pwd.length(); i++)
            sb.append('*');
        return sb.toString();
    }

    private void togglePassword() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setText(originalPassword);
            passwordField.setEchoChar((char) 0);
            togglePasswordBtn.setText("隐藏");
        } else {
            passwordField.setText(maskedPassword);
            passwordField.setEchoChar((char) 0);
            togglePasswordBtn.setText("显示");
        }
    }

    private void toggleSecondPassword() {
        secondPasswordVisible = !secondPasswordVisible;
        if (secondPasswordVisible) {
            secondPasswordField.setText(originalSecondPassword);
            secondPasswordField.setEchoChar((char) 0);
            toggleSecondPasswordBtn.setText("隐藏");
        } else {
            secondPasswordField.setText(maskedSecondPassword);
            secondPasswordField.setEchoChar((char) 0);
            toggleSecondPasswordBtn.setText("显示");
        }
    }

    private void fillFields() {
        if (user instanceof Student) {
            Student s = (Student) user;
            nameField.setText(s.getName());
            ageField.setText(String.valueOf(s.getAge()));
            genderBox.setSelectedItem(s.getGender());
            phoneField.setText(s.getPhoneNum());
            classField.setText(s.getClassName());
            majorField.setText(s.getMajor());
        }
        passwordField.setText(maskedPassword);
        secondPasswordField.setText(maskedSecondPassword);
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String phone = phoneField.getText().trim();
        String pwd = new String(passwordField.getPassword());
        String pwd2 = new String(secondPasswordField.getPassword());

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "姓名不能为空！");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0 || age > 150)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "年龄必须是 0~150 的整数！");
            return;
        }

        if (!phone.isEmpty() && phone.length() != 11) {
            JOptionPane.showMessageDialog(this, "手机号必须是 11 位！");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定保存修改吗？", "确认保存",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        if (user instanceof Student) {
            Student s = (Student) user;
            s.setName(name);
            s.setAge(age);
            s.setGender((String) genderBox.getSelectedItem());
            if (!phone.isEmpty())
                s.setPhoneNum(phone);
            s.setClassName(classField.getText().trim());
            s.setMajor(majorField.getText().trim());
        }
        if (!pwd.isEmpty() && !pwd.equals(maskedPassword)) {
            user.setPassword(pwd);
        }
        if (!pwd2.isEmpty() && !pwd2.equals(maskedSecondPassword)) {
            user.setSecondPassword(pwd2);
        }

        saved = true;
        JOptionPane.showMessageDialog(this, "保存成功！");
        LoginJFrame.saveUsers();
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}