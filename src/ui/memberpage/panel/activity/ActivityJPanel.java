package ui.memberpage.panel.activity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import job.Student;
import job.User;
import thingstodo.Activity;
import thingstodo.ActivityList;
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

    private JLabel selfStatusLabel;

    private JTextArea detailArea;

    public ActivityJPanel(ActivityList activityList, User currentUser) {
        this.activityList = activityList;
        this.currentUser = currentUser;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topPanel.setBackground(Color.decode("#006915"));

        JButton signUpJButton = new JButton("报名选中活动");
        signUpJButton.addActionListener(e -> doSignUp());
        topPanel.add(signUpJButton);

        JButton cancelJButton = new JButton("取消报名");
        cancelJButton.addActionListener(e -> doCancel());
        topPanel.add(cancelJButton);

        JButton refreshJButton = new JButton("刷新");
        refreshJButton.addActionListener(e -> refreshAll());
        topPanel.add(refreshJButton);

        add(topPanel, BorderLayout.NORTH);

        String[] activityCols = { "标题", "开始时间", "结束时间", "报名/上限" };
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
                refreshSelfStatus();
                refreshDetail();
            }
        });

        JScrollPane activityScroll = new JScrollPane(activityTable);
        activityScroll.setBorder(BorderFactory.createTitledBorder("活动列表"));
        activityScroll.setPreferredSize(new Dimension(900, 200));

        selfStatusLabel = new JLabel("我的报名状态：未选择活动");
        selfStatusLabel.setForeground(Color.WHITE);
        selfStatusLabel.setFont(new Font("等线", Font.BOLD, 16));
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.decode("#006915"));
        statusPanel.add(selfStatusLabel);

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
        memberScroll.setBorder(BorderFactory.createTitledBorder("全部报名人员（含候补）"));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.decode("#006915"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        activityScroll.setPreferredSize(new Dimension(900, 150));
        gbc.gridy = 0;
        gbc.weighty = 0.25;
        centerPanel.add(activityScroll, gbc);

        detailArea = new JTextArea(4, 60);
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("等线", Font.PLAIN, 14));
        detailArea.setBackground(new Color(0xF5F5F5));
        detailArea.setText("（请选择一个活动查看详情）");
        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailScroll.setBorder(BorderFactory.createTitledBorder("活动详情"));
        detailScroll.setPreferredSize(new Dimension(900, 120));
        gbc.gridy = 1;
        gbc.weighty = 0.20;
        centerPanel.add(detailScroll, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        centerPanel.add(statusPanel, gbc);

        memberScroll.setPreferredSize(new Dimension(900, 250));
        gbc.gridy = 3;
        gbc.weighty = 0.55;
        centerPanel.add(memberScroll, gbc);

        add(centerPanel, BorderLayout.CENTER);
        refreshAll();
    }

    private void refreshActivityTable() {
        activityModel.setRowCount(0);
        for (Activity a : activityList.getAll()) { 
            int current = a.getStudentList().getCurrentSize();
            int max = a.getStudentList().getMaxMemberNum();
            activityModel.addRow(new Object[] {
                    a.getTitle(),
                    formatTime(a.getStartTime()),
                    formatTime(a.getEndTime()),
                    current + " / " + (max == 0 ? "不限" : max)
            });
        }
    }

    private void refreshMemberTable() {
        memberModel.setRowCount(0);
        Activity activity = getSelectedActivity();
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

    private void refreshSelfStatus() {
        Activity activity = getSelectedActivity();
        if (activity == null) {
            selfStatusLabel.setText("我的报名状态：未选择活动");
            return;
        }

        String myId = currentUser.getId();
        int max = activity.getStudentList().getMaxMemberNum();
        List<Student> students = activity.getStudentList().returnAll().toList();

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            if (s.getId().equals(myId)) {
                if (i < max || max == 0) {
                    selfStatusLabel.setText("我的报名状态：✔ 已报名（正式名单）");
                } else {
                    selfStatusLabel.setText("我的报名状态：⏳ 候补名单（第 " + (i - max + 1) + " 位）");
                }
                return;
            }
        }
        selfStatusLabel.setText("我的报名状态：✘ 未报名");
    }

    private void doSignUp() {
        Activity activity = getSelectedActivity();
        if (activity == null) {
            JOptionPane.showMessageDialog(this, "请先选中一个活动。");
            return;
        }

        if (!(currentUser instanceof Student)) {
            JOptionPane.showMessageDialog(this, "当前账号不是学生，无法报名。");
            return;
        }
        Student me = (Student) currentUser;

        if (activity.getStudentList().containsId(me.getId())) {
            JOptionPane.showMessageDialog(this, "你已经报名了这个活动。");
            return;
        }

        int max = activity.getStudentList().getMaxMemberNum();
        int current = activity.getStudentList().getCurrentSize();
        int waitingMax = activity.getStudentList().getWaitingMemberNum();

        if (max == 0 || current < max) {
            activity.getStudentList().addStu(me);
            LoginJFrame.saveActivities();
            refreshAll();
            JOptionPane.showMessageDialog(this, "报名成功！你已进入正式名单。");
            return;
        }

        if (current < max + waitingMax) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "正式名单已满，是否加入候补名单？",
                    "候补确认",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION)
                return;

            activity.getStudentList().addStu(me);
            LoginJFrame.saveActivities();
            refreshAll();
            JOptionPane.showMessageDialog(this,
                    "已加入候补名单，你是第 " + (current - max + 1) + " 位候补。");
            return;
        }

        if (waitingMax == 0) {
            JOptionPane.showMessageDialog(this, "正式名额已满，该活动没有设置候补名额，无法报名。");
        } else {
            JOptionPane.showMessageDialog(this, "正式和候补名额均已满，无法报名。");
        }
    }

    private void doCancel() {
        Activity activity = getSelectedActivity();
        if (activity == null) {
            JOptionPane.showMessageDialog(this, "请先选中一个活动。");
            return;
        }

        String myId = currentUser.getId();
        if (!activity.getStudentList().containsId(myId)) {
            JOptionPane.showMessageDialog(this, "你没有报名这个活动。");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定取消报名吗？", "确认取消", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        activity.getStudentList().returnAll().removeStu(myId);
        autoPromote(activity);

        LoginJFrame.saveActivities();
        refreshAll();
        JOptionPane.showMessageDialog(this, "已取消报名。");
    }

    private void autoPromote(Activity activity) {

    }

    private Activity getSelectedActivity() {
        int row = activityTable.getSelectedRow();
        if (row == -1)
            return null;
        String title = (String) activityModel.getValueAt(row, 0);
        return activityList.findByTitle(title);
    }

    private void refreshDetail() {
        Activity activity = getSelectedActivity();
        if (activity == null) {
            detailArea.setText("（请选择一个活动查看详情）");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("标题：").append(activity.getTitle()).append("\n");
        sb.append("开始时间：").append(formatTime(activity.getStartTime())).append("\n");
        sb.append("结束时间：").append(formatTime(activity.getEndTime())).append("\n");
        sb.append("创建者：").append(
                activity.getCreator() == null ? "-" : activity.getCreator().getName()).append("\n");
        sb.append("人数上限：").append(
                activity.getStudentList().getMaxMemberNum() == 0
                        ? "不限"
                        : activity.getStudentList().getMaxMemberNum())
                .append("\n");
        sb.append("候补上限：").append(
                activity.getStudentList().getMaxMemberNum() == 0
                        ? "-"
                        : activity.getStudentList().getWaitingMemberNum())
                .append("\n");
        sb.append("内容：\n").append(
                activity.getContent() == null || activity.getContent().isEmpty()
                        ? "（无）"
                        : activity.getContent());

        detailArea.setText(sb.toString());
        detailArea.setCaretPosition(0);
    }

    private void refreshAll() {
        refreshActivityTable();
        refreshMemberTable();
        refreshSelfStatus();
        refreshDetail();
    }

    private String formatTime(Time t) {
        if (t == null)
            return "-";
        return String.format("%04d-%02d-%02d %02d:%02d",
                t.getYear(), t.getMonth(), t.getDay(), t.getHour(), t.getMinute());
    }
}