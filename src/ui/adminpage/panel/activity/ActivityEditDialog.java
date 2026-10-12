package ui.adminpage.panel.activity;

import thingstodo.Activity;
import thingstodo.time.Time;

import javax.swing.*;
import java.awt.*;

public class ActivityEditDialog extends JDialog {

    private JTextField titleField;
    private JTextArea contentArea;
    private JTextField yearJField, monthJField, dayJField, hourJField, minuteJField;
    private JTextField year2JField, month2JField, day2JField, hour2JField, minute2JField;
    private JTextField maxField;

    private Activity result;

    public ActivityEditDialog(Window owner, String title, Activity old) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setSize(500, 500);
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
        add(new JLabel("开始时间："), gbc);
        gbc.gridx = 1;
        add(createTimePanel(true), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("结束时间："), gbc);
        gbc.gridx = 1;
        add(createTimePanel(false), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("人数上限："), gbc);
        gbc.gridx = 1;
        maxField = new JTextField(25);
        add(maxField, gbc);

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
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        if (old != null) {
            titleField.setText(old.getTitle());
            contentArea.setText(old.getContent());
            fillTime(old.getStartTime(), true);
            fillTime(old.getEndTime(), false);
            maxField.setText(String.valueOf(old.getStudentList().getMaxMemberNum()));
        } else {
            maxField.setText("0");
        }

        getRootPane().setDefaultButton(okJButton);
    }

    private JPanel createTimePanel(boolean isStart) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        if (isStart) {
            yearJField = new JTextField(4);
            monthJField = new JTextField(2);
            dayJField = new JTextField(2);
            hourJField = new JTextField(2);
            minuteJField = new JTextField(2);
            p.add(yearJField);
            p.add(new JLabel("年"));
            p.add(monthJField);
            p.add(new JLabel("月"));
            p.add(dayJField);
            p.add(new JLabel("日"));
            p.add(hourJField);
            p.add(new JLabel(":"));
            p.add(minuteJField);
        } else {
            year2JField = new JTextField(4);
            month2JField = new JTextField(2);
            day2JField = new JTextField(2);
            hour2JField = new JTextField(2);
            minute2JField = new JTextField(2);
            p.add(year2JField);
            p.add(new JLabel("年"));
            p.add(month2JField);
            p.add(new JLabel("月"));
            p.add(day2JField);
            p.add(new JLabel("日"));
            p.add(hour2JField);
            p.add(new JLabel(":"));
            p.add(minute2JField);
        }
        return p;
    }

    private void fillTime(Time t, boolean isStart) {
        if (t == null)
            return;
        if (isStart) {
            yearJField.setText(String.valueOf(t.getYear()));
            monthJField.setText(String.valueOf(t.getMonth()));
            dayJField.setText(String.valueOf(t.getDay()));
            hourJField.setText(String.valueOf(t.getHour()));
            minuteJField.setText(String.valueOf(t.getMinute()));
        } else {
            year2JField.setText(String.valueOf(t.getYear()));
            month2JField.setText(String.valueOf(t.getMonth()));
            day2JField.setText(String.valueOf(t.getDay()));
            hour2JField.setText(String.valueOf(t.getHour()));
            minute2JField.setText(String.valueOf(t.getMinute()));
        }
    }

    private void onOk() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "标题不能为空！");
            return;
        }

        Time start, end;
        int max;
        try {
            start = parseTime(yearJField, monthJField, dayJField, hourJField, minuteJField);
            end = parseTime(year2JField, month2JField, day2JField, hour2JField, minute2JField);
            max = Integer.parseInt(maxField.getText().trim());
            if (max < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "时间或人数上限格式错误！");
            return;
        }

        if (!start.isValid()) {
            JOptionPane.showMessageDialog(this, "开始时间不合法！");
            return;
        }

        if (!end.isValid()) {
            JOptionPane.showMessageDialog(this, "结束时间不合法！");
            return;
        }

        if (!start.isBefore(end)) {
            JOptionPane.showMessageDialog(this, "开始时间必须早于结束时间！");
            return;
        }

        result = new Activity(title, content, start, end, null);
        result.getStudentList().setMaxMemberNum(max);
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

    private Time parseTime(JTextField y, JTextField mon, JTextField d, JTextField h, JTextField min) {
        return new Time(
                parseIntOr(y.getText(), 0),
                parseIntOr(mon.getText(), 0),
                parseIntOr(d.getText(), 0),
                parseIntOr(h.getText(), 0),
                parseIntOr(min.getText(), 0));
    }

    public Activity getResult() {
        return result;
    }
}