package ui.adminpage.panel.activity;

import thingstodo.Activity;
import thingstodo.time.Time;

import javax.swing.*;
import java.awt.*;

public class ActivityEditDialog extends JDialog {

    private JTextField titleField;
    private JTextArea contentArea;
    private TimeRow startRow;
    private TimeRow endRow;
    private TimeRow signUpStartRow;
    private TimeRow signUpEndRow;
    private JTextField maxField;
    private JTextField waitingField;

    private final boolean createMode;

    private Activity result;

    public ActivityEditDialog(Window owner, String title, Activity old) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.createMode = (old == null);
        setSize(560, 690);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("标题："), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(25);
        add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("内容："), gbc);
        gbc.gridx = 1;
        contentArea = new JTextArea(3, 25);
        contentArea.setLineWrap(true);
        add(new JScrollPane(contentArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("活动开始时间："), gbc);
        gbc.gridx = 1;
        startRow = new TimeRow();
        add(startRow.getPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("活动结束时间："), gbc);
        gbc.gridx = 1;
        endRow = new TimeRow();
        add(endRow.getPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("报名开始时间："), gbc);
        gbc.gridx = 1;
        signUpStartRow = new TimeRow();
        add(signUpStartRow.getPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("报名结束时间："), gbc);
        gbc.gridx = 1;
        signUpEndRow = new TimeRow();
        add(signUpEndRow.getPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("报名人数："), gbc);
        gbc.gridx = 1;
        maxField = new JTextField(25);
        add(maxField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("候补人数："), gbc);
        gbc.gridx = 1;
        waitingField = new JTextField(25);
        add(waitingField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton okJButton = new JButton("确定");
        JButton cancelJButton = new JButton("取消");
        okJButton.addActionListener(e -> onOk());
        cancelJButton.addActionListener(e -> {
            result = null;
            dispose();
        });
        buttonPanel.add(okJButton);
        buttonPanel.add(cancelJButton);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        JLabel hintJLabel = new JLabel(
                "<html>如果你要创建不限制报名人数的活动，请将报名人数填为-1。<br>"
                        + "如果要创建禁止任何人报名的活动，请将报名人数填为0。</html>");
        hintJLabel.setFont(new Font("等线", Font.PLAIN, 12));
        hintJLabel.setForeground(Color.GRAY);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(hintJLabel, gbc);

        if (old != null) {
            titleField.setText(old.getTitle());
            contentArea.setText(old.getContent());
            startRow.fill(old.getStartTime());
            endRow.fill(old.getEndTime());
            signUpStartRow.fill(old.getSignUpStartTime());
            signUpEndRow.fill(old.getSignUpEndTime());
            maxField.setText(String.valueOf(old.getStudentList().getMaxMemberNum()));
            waitingField.setText(String.valueOf(old.getStudentList().getWaitingMemberNum()));
        } else {
            maxField.setText("-1");
            waitingField.setText("0");
        }

        getRootPane().setDefaultButton(okJButton);
    }

    private class TimeRow {

        private final JTextField yearField = new JTextField(4);
        private final JTextField monthField = new JTextField(2);
        private final JTextField dayField = new JTextField(2);
        private final JTextField hourField = new JTextField(2);
        private final JTextField minuteField = new JTextField(2);

        private JPanel getPanel() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            p.add(yearField);
            p.add(new JLabel("年"));
            p.add(monthField);
            p.add(new JLabel("月"));
            p.add(dayField);
            p.add(new JLabel("日"));
            p.add(hourField);
            p.add(new JLabel(":"));
            p.add(minuteField);
            return p;
        }

        private Time toTime() {
            return new Time(
                    parseIntOr(yearField.getText(), 0),
                    parseIntOr(monthField.getText(), 0),
                    parseIntOr(dayField.getText(), 0),
                    parseIntOr(hourField.getText(), 0),
                    parseIntOr(minuteField.getText(), 0));
        }

        private void fill(Time t) {
            if (t == null)
                return;
            yearField.setText(String.valueOf(t.getYear()));
            monthField.setText(String.valueOf(t.getMonth()));
            dayField.setText(String.valueOf(t.getDay()));
            hourField.setText(String.valueOf(t.getHour()));
            minuteField.setText(String.valueOf(t.getMinute()));
        }
    }

    private void onOk() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "标题不能为空！");
            return;
        }

        Time start, end, signUpStart, signUpEnd;
        int max;
        int waiting;
        try {
            start = startRow.toTime();
            end = endRow.toTime();
            signUpStart = signUpStartRow.toTime();
            signUpEnd = signUpEndRow.toTime();
            max = Integer.parseInt(maxField.getText().trim());
            waiting = Integer.parseInt(waitingField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "时间、人数上限或候补人数格式错误！");
            return;
        }

        if (max < -1) {
            JOptionPane.showMessageDialog(this, "报名人数只能填 -1（不限）、0（禁止报名）或正整数！");
            return;
        }

        if (waiting < 0) {
            JOptionPane.showMessageDialog(this, "候补人数不能为负数！");
            return;
        }

        if (!start.isValid()) {
            JOptionPane.showMessageDialog(this, "活动开始时间不合法！");
            return;
        }

        if (!end.isValid()) {
            JOptionPane.showMessageDialog(this, "活动结束时间不合法！");
            return;
        }

        if (!signUpStart.isValid()) {
            JOptionPane.showMessageDialog(this, "报名开始时间不合法！");
            return;
        }

        if (!signUpEnd.isValid()) {
            JOptionPane.showMessageDialog(this, "报名结束时间不合法！");
            return;
        }

        if (!start.isBefore(end)) {
            JOptionPane.showMessageDialog(this, "开始时间必须早于结束时间！");
            return;
        }

        if (!signUpStart.isBefore(signUpEnd)) {
            JOptionPane.showMessageDialog(this, "报名时间不正确！");
            return;
        }

        if (createMode && Time.now().isAfter(signUpEnd)) {
            JOptionPane.showMessageDialog(this, "报名时间不正确！");
            return;
        }

        result = new Activity(title, content, start, end, null);
        result.setSignUpStartTime(signUpStart);
        result.setSignUpEndTime(signUpEnd);
        result.getStudentList().setMaxMemberNum(max);
        result.getStudentList().setWaitingMemberNum(waiting);
        dispose();
    }

    private int parseIntOr(String text, int defaultValue) {
        if (text == null)
            return defaultValue;
        text = text.trim();
        if (text.isEmpty())
            return defaultValue;
        return Integer.parseInt(text);
    }

    public Activity getResult() {
        return result;
    }
}
