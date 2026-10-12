package ui.memberpage;

import javax.swing.*;

import job.User;
import ui.login.LoginJFrame;
import ui.memberpage.panel.activity.ActivityJPanel;
import ui.memberpage.panel.member.ViewJPanel;
import thingstodo.ActivityList;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MemberOperatingJFrame extends MemberOperatingWindow {

    private final ActivityList activityList = LoginJFrame.activityList;

    public MemberOperatingJFrame(User user) {

        setTitle("成员操作面板 - " + user.getId());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LoginJFrame.saveUsers();
                LoginJFrame.saveActivities();
                doExit();
            }
        });

        setJMenuBar(new MemberJMenuBar(this, user));

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("查看队员", new ViewJPanel(LoginJFrame.userList, user));
        tabbedPane.addTab("报名活动", new ActivityJPanel(activityList, user));

        add(tabbedPane);
        setVisible(true);
    }

    public void doExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要退出系统吗？",
                "退出系统",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}