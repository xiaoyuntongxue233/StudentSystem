package ui.adminpage.panel.member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ui.login.LoginJFrame;
import chain.UserLinkedList;
import job.Student;
import job.User;
import thingstodo.VerifySecondPassword;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MemberJPanel extends JPanel {

    private final UserLinkedList userList;
    private final User currentUser;
    private JTextField searchField;
    private JTable memberTable;
    private DefaultTableModel tableModel;

    public MemberJPanel(UserLinkedList userList, User currentUser) {
        this.userList = userList;
        this.currentUser = currentUser;
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


        String[] columns = { "ID", "姓名", "年龄", "性别", "班级", "专业", "权限" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setRowHeight(25);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(memberTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(Color.decode("#006915"));
        JButton addJButton = new JButton("添加");
        addJButton.addActionListener(e -> doAdd());
        bottomPanel.add(addJButton);

        JButton editJButton = new JButton("修改");
        editJButton.addActionListener(e -> doEdit());
        bottomPanel.add(editJButton);

        JButton deleteJButton = new JButton("删除");
        deleteJButton.addActionListener(e -> doDelete());
        bottomPanel.add(deleteJButton);

        JButton sortByAgeJButton = new JButton("按年龄排序");
        sortByAgeJButton.addActionListener(e -> doSortByAge());
        bottomPanel.add(sortByAgeJButton);

        JButton sortByClassJButton = new JButton("按班级排序");
        sortByClassJButton.addActionListener(e -> doSortByClass());
        bottomPanel.add(sortByClassJButton);

        JButton sortByIdJButton = new JButton("按ID排序");
        sortByIdJButton.addActionListener(e -> doSortById());
        bottomPanel.add(sortByIdJButton);

        JButton refreshJButton = new JButton("刷新");
        refreshJButton.addActionListener(e -> refreshTable());
        bottomPanel.add(refreshJButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);

        for (User u : userList.toList()) {
            if (u instanceof Student) {
                Student s = (Student) u;
                tableModel.addRow(new Object[] {
                        s.getId(),
                        s.getName(),
                        s.getAge(),
                        s.getGender(),
                        s.getClassName(),
                        s.getMajor(),
                        roleToText(s.getRole())
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
                            roleToText(s.getRole())
                    });
                }
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "没有匹配的成员。");
        }
    }

    private void doAdd() {
        MemberEditDialog dialog = new MemberEditDialog(
                SwingUtilities.getWindowAncestor(this), "添加成员", null);
        dialog.setVisible(true);

        Student result = dialog.getResult();
        if (result == null)
            return;
        if (userList.containsId(result.getId())) {
            JOptionPane.showMessageDialog(this, "ID 已存在!");
            return;
        }

        userList.addUser(result);
        LoginJFrame.saveUsers();
        refreshTable();
        JOptionPane.showMessageDialog(this, "添加成功！");
    }

    private void doEdit() {
        int row = memberTable.getSelectedRow();
        if (row == -1)
            return;

        String id = (String) tableModel.getValueAt(row, 0);
        User user = userList.findUser(id).data;
        if (!(user instanceof Student))
            return;

        Student old = (Student) user;

        MemberEditDialog dialog = new MemberEditDialog(
                SwingUtilities.getWindowAncestor(this), "修改成员", old);
        dialog.setVisible(true);

        Student result = dialog.getResult();
        if (result == null)
            return;

        if (!result.getId().equals(old.getId()) && userList.containsId(result.getId())) {
            JOptionPane.showMessageDialog(this, "新 ID 已存在!");
            return;
        }

        if (old.getRole() == User.Role.Admin
                && result.getRole() != User.Role.Admin
                && countAdmins() <= 1) {
            JOptionPane.showMessageDialog(this, "系统至少需要保留一个管理员！");
            return;
        }

        boolean isDemotion = (old.getRole() == User.Role.Admin && result.getRole() != User.Role.Admin);
        boolean isUpgrade = (old.getRole() != User.Role.Admin && result.getRole() == User.Role.Admin);
        String demotionText = "你正在将 管理员 [" + old.getName() + "]（ID: " + old.getId() + "）\n" + "降级为 "
                + roleToText(result.getRole()) + " 。\n" + "降级后该账号将失去管理员权限，确定继续吗？";
        String upgradeText = "你正在将 " + roleToText(result.getRole()) + " [" + old.getName() + "]（ID: " + old.getId()
                + "）\n" + "升级为 管理员 \n。" + "升级后该账号将获得管理员权限，确定继续吗？";

        if (isDemotion) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    demotionText,
                    "确认降级",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            if (!VerifySecondPassword.verifySecondPassword(currentUser)) {
                return;
            }
        }

        if (isUpgrade) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    upgradeText,
                    "确认升级",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            if (!VerifySecondPassword.verifySecondPassword(currentUser)) {
                return;
            }
        }

        if (currentUser != null && currentUser.getId().equals(old.getId())
                && !result.getId().equals(old.getId())) {
            JOptionPane.showMessageDialog(this, "不能修改当前登录账号的 ID！");
            return;
        }

        old.setId(result.getId());
        old.setName(result.getName());
        old.setAge(result.getAge());
        old.setGender(result.getGender());
        old.setClassName(result.getClassName());
        old.setMajor(result.getMajor());
        old.setRole(result.getRole());
        old.setPassword(result.getPassword());

        LoginJFrame.saveUsers();
        refreshTable();
        JOptionPane.showMessageDialog(this, "修改成功！");
    }

    private void doDelete() {
        int row = memberTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        boolean isSelf = currentUser != null && currentUser.getId().equals(id);

        if (isSelf) {
            if (!VerifySecondPassword.verifySecondPassword(currentUser)) {
                return;
            }

            int confirmSelf = JOptionPane.showConfirmDialog(
                    this,
                    "是否确定删除自己？\n删除之后将返回登录页面！", "确认删除？",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmSelf != JOptionPane.YES_OPTION) {
                return;
            }
        } else {
            User target = userList.findUser(id).data;
            if (target != null && target.getRole() == User.Role.Admin
                    && countAdmins() <= 1) {
                JOptionPane.showMessageDialog(this, "系统至少需要保留一个管理员，不能删除！");
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定删除成员 [" + name + "]（ID: " + id + "）吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.NO_OPTION)
            return;

        if (userList.removeUser(id)) {
            LoginJFrame.saveUsers();
            refreshTable();
            JOptionPane.showMessageDialog(this, "删除成功！");

            if (isSelf) {
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w != null)
                    w.dispose();
                new LoginJFrame();
            }
        } else {
            JOptionPane.showMessageDialog(this, "删除失败。");
        }
    }

    private void doSortByAge() {
        List<Student> students = getAllStudents();
        Student[] arr = students.toArray(new Student[0]);
        mergeSort(arr, 0, arr.length - 1, Comparator.comparingInt(Student::getAge));
        showStudents(Arrays.asList(arr));
    }

    private void doSortByClass() {
        List<Student> students = getAllStudents();
        Student[] arr = students.toArray(new Student[0]);
        mergeSort(arr, 0, arr.length - 1,
                Comparator.comparing(s -> s.getClassName() == null ? "" : s.getClassName()));
        showStudents(Arrays.asList(arr));
    }

    private void doSortById() {
        List<Student> students = getAllStudents();
        Student[] arr = students.toArray(new Student[0]);
        mergeSort(arr, 0, arr.length - 1,
                Comparator.comparing(s -> s.getId() == null ? "" : s.getId()));
        showStudents(Arrays.asList(arr));
    }

    private List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        for (User u : userList.toList()) {
            if (u instanceof Student)
                students.add((Student) u);
        }
        return students;
    }

    private void showStudents(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[] {
                    s.getId(), s.getName(), s.getAge(), s.getGender(),
                    s.getClassName(), s.getMajor(), roleToText(s.getRole())
            });
        }
    }

    private void mergeSort(Student[] arr, int left, int right, Comparator<Student> cmp) {
        if (left >= right)
            return;
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid, cmp);
        mergeSort(arr, mid + 1, right, cmp);
        merge(arr, left, mid, right, cmp);
    }

    private void merge(Student[] arr, int left, int mid, int right, Comparator<Student> cmp) {
        Student[] temp = new Student[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (cmp.compare(arr[i], arr[j]) <= 0)
                temp[k++] = arr[i++];
            else
                temp[k++] = arr[j++];
        }
        while (i <= mid)
            temp[k++] = arr[i++];
        while (j <= right)
            temp[k++] = arr[j++];

        for (int t = 0; t < temp.length; t++)
            arr[left + t] = temp[t];
    }

    private String roleToText(User.Role role) {
        if (role == User.Role.Guest)
            return "访客";
        if (role == User.Role.Member)
            return "成员";
        if (role == User.Role.Admin)
            return "管理员";
        return "未知";
    }

    private int countAdmins() {
        int count = 0;
        for (User u : userList.toList()) {
            if (u.getRole() == User.Role.Admin) {
                count++;
            }
        }
        return count;
    }

}
