package ui.guestpage.panel.activity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import job.Student;
import job.User;
import thingstodo.Activity;
import thingstodo.ActivityList;
import thingstodo.time.Time;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityJPanel extends JPanel {

    private final ActivityList activityList;

    private JTable activityTable;
    private DefaultTableModel activityModel;
    private JTextField searchField;

    private JTable memberTable;
    private DefaultTableModel memberModel;

    private JTextArea detailArea;

    public ActivityJPanel(ActivityList activityList, User currentUser) {
        this.activityList = activityList;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topPanel.setBackground(Color.decode("#006915"));

        JButton refreshJButton = new JButton("刷新");
        refreshJButton.addActionListener(e -> refreshAll());
        topPanel.add(refreshJButton);

        JLabel searchJLabel = new JLabel("活动标题：");
        searchJLabel.setForeground(Color.WHITE);
        topPanel.add(searchJLabel);

        searchField = new JTextField(15);
        topPanel.add(searchField);

        JButton searchJButton = new JButton("搜索");
        searchJButton.addActionListener(e -> doSearch());
        topPanel.add(searchJButton);

        JButton showAllJButton = new JButton("显示全部");
        showAllJButton.addActionListener(e -> refreshActivityTable());
        topPanel.add(showAllJButton);

        add(topPanel, BorderLayout.NORTH);

        String[] activityCols = { "标题", "开始时间", "结束时间", "报名/上限", "状态" };
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
                refreshDetail();
            }
        });

        JScrollPane activityScroll = new JScrollPane(activityTable);
        activityScroll.setBorder(BorderFactory.createTitledBorder("活动列表"));
        activityScroll.setPreferredSize(new Dimension(900, 200));



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

        memberScroll.setPreferredSize(new Dimension(900, 250));
        gbc.gridy = 2;
        gbc.weighty = 0.55;
        centerPanel.add(memberScroll, gbc);

        add(centerPanel, BorderLayout.CENTER);
        refreshAll();
    }

    private void refreshActivityTable() {
        fillActivityTable(activityList.getAll());
    }

    private void fillActivityTable(List<Activity> list) {
        activityModel.setRowCount(0);
        for (Activity a : list) {
            int current = a.getStudentList().getCurrentSize();
            activityModel.addRow(new Object[] {
                    a.getTitle(),
                    formatTime(a.getStartTime()),
                    formatTime(a.getEndTime()),
                    current + " / " + a.getStudentList().getMaxMemberNumText(),
                    a.getStatusText()
            });
        }
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshActivityTable();
            return;
        }

        List<Activity> matched = new ArrayList<>();
        for (Activity a : activityList.getAll()) {
            if (a.getTitle() != null && a.getTitle().contains(keyword)) {
                matched.add(a);
            }
        }

        fillActivityTable(matched);

        if (matched.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有匹配的活动。");
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
            if (activity.getStudentList().isFormalMember(i)) {
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
        sb.append("报名开始时间：").append(formatTime(activity.getSignUpStartTime())).append("\n");
        sb.append("报名结束时间：").append(formatTime(activity.getSignUpEndTime())).append("\n");
        sb.append("活动状态：").append(activity.getStatusText()).append("\n");
        sb.append("创建者：").append(
                activity.getCreator() == null ? "-" : activity.getCreator().getName()).append("\n");
        sb.append("报名人数：").append(activity.getStudentList().getMaxMemberNumText()).append("\n");
        sb.append("候补上限：").append(activity.getStudentList().getWaitingMemberNumText()).append("\n");
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
        refreshDetail();
    }

    private String formatTime(Time t) {
        if (t == null)
            return "-";
        return String.format("%04d-%02d-%02d %02d:%02d",
                t.getYear(), t.getMonth(), t.getDay(), t.getHour(), t.getMinute());
    }
}