package ui.guestpage.panel.member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import chain.UserLinkedList;
import job.Student;
import job.User;

import java.awt.*;

public class ViewJPanel extends JPanel {

    private final UserLinkedList userList;

    private JTextField searchField;
    private JTable memberTable;
    private DefaultTableModel tableModel;

    public ViewJPanel(UserLinkedList userList, User currentUser) {
        this.userList = userList;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topJPanel.setBackground(Color.decode("#006915"));

        JLabel hint = new JLabel("ID/姓名：");
        hint.setForeground(Color.WHITE);
        topJPanel.add(hint);

        searchField = new JTextField(15);
        topJPanel.add(searchField);

        JButton searchJButton = new JButton("搜索");
        searchJButton.addActionListener(e -> doSearch());
        topJPanel.add(searchJButton);

        JButton showAllJButton = new JButton("显示全部");
        showAllJButton.addActionListener(e -> refreshTable());
        topJPanel.add(showAllJButton);

        add(topJPanel, BorderLayout.NORTH);

        String[] columns = { "ID", "姓名", "年龄", "性别", "班级", "专业", "手机号" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setRowHeight(25);
        add(new JScrollPane(memberTable), BorderLayout.CENTER);

        JLabel tip = new JLabel("提示：手机号已脱敏，密码不可见。");
        tip.setForeground(Color.WHITE);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(Color.decode("#006915"));
        bottom.add(tip);
        add(bottom, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (User u : userList.toList()) {
            if (u instanceof Student) {
                Student s = (Student) u;

                String phone = maskPhone(s.getRawPhoneNum());

                tableModel.addRow(new Object[] {
                        s.getId(),
                        s.getName(),
                        s.getAge(),
                        s.getGender(),
                        s.getClassName(),
                        s.getMajor(),
                        phone
                });
            }
        }
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        for (User u : userList.toList()) {
            if (u instanceof Student) {
                Student s = (Student) u;
                if (s.getId().contains(keyword) || s.getName().contains(keyword)) {
                    tableModel.addRow(new Object[] {
                            s.getId(),
                            s.getName(),
                            s.getAge(),
                            s.getGender(),
                            s.getClassName(),
                            s.getMajor(),
                            maskPhone(s.getRawPhoneNum())
                    });
                }
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "没有匹配的成员。");
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone == null ? "" : phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}