package ui.adminpage.panel.member;

import job.Student;
import job.User;

import javax.swing.*;
import java.awt.*;

public class MemberEditDialog extends JDialog {
    private JTextField idField;
    private JTextField nameField;
    private JTextField ageField;
    private JComboBox<String> genderBox;
    private JTextField classField;
    private JTextField majorField;
    private JComboBox<String> roleBox;

    private Student result;

    public MemberEditDialog(Window owner, String title, Student old) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setSize(400, 350);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("ID："), gbc);
        gbc.gridx = 1;
        idField = new JTextField(25);
        add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("姓名："), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(25);
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("年龄："), gbc);
        gbc.gridx = 1;
        ageField = new JTextField(25);
        add(ageField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("性别："), gbc);
        gbc.gridx = 1;
        genderBox = new JComboBox<>(new String[] { "男", "女", "其他" });
        add(genderBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("班级："), gbc);
        gbc.gridx = 1;
        classField = new JTextField(25);
        add(classField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("专业："), gbc);
        gbc.gridx = 1;
        majorField = new JTextField(25);
        add(majorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("权限："), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[] { "成员", "访客", "管理员" });
        add(roleBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");

        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> {
            result = null;
            dispose();
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        if (old != null) {
            idField.setText(old.getId());
            nameField.setText(old.getName());
            ageField.setText(String.valueOf(old.getAge()));
            genderBox.setSelectedItem(old.getGender());
            classField.setText(old.getClassName());
            majorField.setText(old.getMajor());
            if (old.getRole() == User.Role.Guest) {
                roleBox.setSelectedItem("访客");
            } else if (old.getRole() == User.Role.Member) {
                roleBox.setSelectedItem("成员");
            } else if (old.getRole() == User.Role.Admin) {
                roleBox.setSelectedItem("管理员");
            }
        }

        getRootPane().setDefaultButton(okButton);
    }

    private void onOk() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String className = classField.getText().trim();
        String major = majorField.getText().trim();
        String roleText = (String) roleBox.getSelectedItem();

        if (id.isEmpty() || name.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "存在字段为空的内容！");
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

        User.Role role;
        if ("访客".equals(roleText))
            role = User.Role.Guest;
        else if ("管理员".equals(roleText))
            role = User.Role.Admin;
        else
            role = User.Role.Member;

        result = new Student(className, 0, major,
                age, gender, name, "00000000000", id,
                "123456", role, null);

        dispose();
    }

    public Student getResult() {
        return result;
    }
}
