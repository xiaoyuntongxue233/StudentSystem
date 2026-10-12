package ui.adminpage.panel.activity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import job.Person;
import job.Student;
import job.User;
import thingstodo.*;
import thingstodo.time.Time;
import ui.login.LoginJFrame;

import java.awt.*;
import java.util.List;

public class ActivityJPanel extends JPanel {

    private final ActivityList activityList;
    private final User currentUser;

    private JTable activityTable;
    private DefaultTableModel activityModel;

    private JTable memberTable;
    private DefaultTableModel memberModel;

    public ActivityJPanel(ActivityList activityList, User currentUser) {
        this.activityList = activityList;
        this.currentUser = currentUser;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topPanel.setBackground(Color.decode("#006915"));

        JButton createBtn = new JButton("创建活动");
        createBtn.addActionListener(e -> doCreate());
        topPanel.add(createBtn);

        JButton deleteBtn = new JButton("删除活动");
        deleteBtn.addActionListener(e -> doDelete());
        topPanel.add(deleteBtn);

        JButton editBtn = new JButton("修改活动");
        editBtn.addActionListener(e -> doEdit());
        topPanel.add(editBtn);

        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refreshAll());
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        String[] activityCols = { "标题", "开始时间", "结束时间", "创建者", "报名/上限" };
        activityModel = new DefaultTableModel(activityCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activityTable = new JTable(activityModel);
        activityTable.setRowHeight(25);
        activityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        activityTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshMemberTable();
            }
        });

        JScrollPane activityScroll = new JScrollPane(activityTable);
        activityScroll.setBorder(BorderFactory.createTitledBorder("活动列表"));

        String[] memberCols = { "ID", "姓名", "状态" };
        memberModel = new DefaultTableModel(memberCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(memberModel);
        memberTable.setRowHeight(25);

        JScrollPane memberScroll = new JScrollPane(memberTable);
        memberScroll.setBorder(BorderFactory.createTitledBorder("报名名单（选中活动后显示）"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                activityScroll, memberScroll);
        splitPane.setResizeWeight(0.55);
        add(splitPane, BorderLayout.CENTER);

        JLabel statusLabel = new JLabel("提示：选中活动可查看报名名单");
        statusLabel.setForeground(Color.WHITE);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(Color.decode("#006915"));
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshAll();
    }

    public void refreshActivityTable() {
        activityModel.setRowCount(0);
        for (Activity a : activityList.getAll()) {
            int current = a.getStudentList().getCurrentSize();
            int max = a.getStudentList().getMaxMemberNum();
            activityModel.addRow(new Object[] {
                    a.getTitle(),
                    formatTime(a.getStartTime()),
                    formatTime(a.getEndTime()),
                    a.getCreator() == null ? "-" : a.getCreator().getName(),
                    current + " / " + (max == 0 ? "不限" : max)
            });
        }
    }

    private void refreshMemberTable() {
        memberModel.setRowCount(0);
        int row = activityTable.getSelectedRow();
        if (row == -1)
            return;

        String title = (String) activityModel.getValueAt(row, 0);
        Activity activity = activityList.findByTitle(title);
        if (activity == null)
            return;

        int max = activity.getStudentList().getMaxMemberNum();
        List<Student> students = activity.getStudentList().returnAll().toList();

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            String status;
            if (i < max || max == 0) {
                status = "正式";
            } else {
                status = "候补（第" + (i - max + 1) + "位）";
            }
            memberModel.addRow(new Object[] {
                    s.getId(),
                    s.getName(),
                    status
            });
        }
    }

    private void doCreate() {
        ActivityEditDialog dialog = new ActivityEditDialog(
                SwingUtilities.getWindowAncestor(this), "创建活动", null);
        dialog.setVisible(true);

        Activity result = dialog.getResult();
        if (result == null)
            return;

        if (activityList.findByTitle(result.getTitle()) != null) {
            JOptionPane.showMessageDialog(this, "活动标题已存在！");
            return;
        }

        Person creator = (currentUser instanceof Person) ? (Person) currentUser : null;
        if (creator == null) {
            JOptionPane.showMessageDialog(this, "创建者信息无效！");
            return;
        }

        Activity activity = new Activity(
                result.getTitle(),
                result.getContent(),
                result.getStartTime(),
                result.getEndTime(),
                creator);
        activity.getStudentList().setMaxMemberNum(result.getStudentList().getMaxMemberNum());

        activityList.add(activity);
        LoginJFrame.saveActivities();
        refreshActivityTable();
        JOptionPane.showMessageDialog(this, "创建成功！");
    }

    private void doEdit() {
    int row = activityTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "请先选中一个活动。");
        return;
    }

    String title = (String) activityModel.getValueAt(row, 0);
    Activity old = activityList.findByTitle(title);
    if (old == null) {
        JOptionPane.showMessageDialog(this, "未找到该活动。");
        return;
    }

    ActivityEditDialog dialog = new ActivityEditDialog(
            SwingUtilities.getWindowAncestor(this), "修改活动", old);
    dialog.setVisible(true);

    Activity result = dialog.getResult();
    if (result == null) {
        return;
    }

    if (!result.getTitle().equals(old.getTitle())
            && activityList.findByTitle(result.getTitle()) != null) {
        JOptionPane.showMessageDialog(this, "活动标题已存在！");
        return;
    }

    int newMax = result.getStudentList().getMaxMemberNum();
    int newWaitingMax = newMax / 3;
    int newCapacity = (newMax == 0) ? Integer.MAX_VALUE : (newMax + newWaitingMax);
    int currentSize = old.getStudentList().getCurrentSize();

    if (currentSize > newCapacity) {
        int overflow = currentSize - newCapacity;

        java.util.List<Student> all = old.getStudentList().returnAll().toList();
        StringBuilder sb = new StringBuilder();
        sb.append("以下 ").append(overflow).append(" 位成员将被移出报名名单：\n");
        for (int i = newCapacity; i < all.size(); i++) {
            Student s = all.get(i);
            sb.append("  · ").append(s.getName())
              .append("（ID: ").append(s.getId()).append("）\n");
        }
        sb.append("\n确定要修改吗？");

        int confirm = JOptionPane.showConfirmDialog(
                this, sb.toString(), "确认修改人数上限",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
    }

    old.setTitle(result.getTitle());
    old.setContent(result.getContent());
    old.setStartTime(result.getStartTime());
    old.setEndTime(result.getEndTime());
    old.getStudentList().setMaxMemberNum(newMax);

    
    java.util.List<Student> removed = old.getStudentList().truncateToCapacity();

    if (!removed.isEmpty()) {
        StringBuilder msg = new StringBuilder("已移出 " + removed.size() + " 位成员：\n");
        for (Student s : removed) {
            msg.append("  · ").append(s.getName())
               .append("（ID: ").append(s.getId()).append("）\n");
        }
        JOptionPane.showMessageDialog(this, msg.toString(),
                "已移出成员", JOptionPane.INFORMATION_MESSAGE);
    }

    LoginJFrame.saveActivities();
    refreshAll();
    JOptionPane.showMessageDialog(this, "修改成功！");
}

    private void doDelete() {
        int row = activityTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一个活动。");
            return;
        }

        String title = (String) activityModel.getValueAt(row, 0);
        Activity activity = activityList.findByTitle(title);
        if (activity == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定删除活动 [" + title + "] 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        activityList.remove(activity);
        LoginJFrame.saveActivities();
        refreshAll();
        JOptionPane.showMessageDialog(this, "删除成功！");
    }

    private void refreshAll() {
        refreshActivityTable();
        refreshMemberTable();
    }

    private String formatTime(Time t) {
        if (t == null)
            return "-";
        return String.format("%04d-%02d-%02d %02d:%02d",
                t.getYear(), t.getMonth(), t.getDay(), t.getHour(), t.getMinute());
    }
}
