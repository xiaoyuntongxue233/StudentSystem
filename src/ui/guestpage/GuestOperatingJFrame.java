package ui.guestpage;

import javax.swing.*;

import job.User;
import ui.login.LoginJFrame;
import ui.guestpage.panel.activity.ActivityJPanel;
import ui.guestpage.panel.member.ViewJPanel;
import thingstodo.ActivityList;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GuestOperatingJFrame extends GuestOperatingWindow {

    private final ActivityList activityList = LoginJFrame.activityList;

    public GuestOperatingJFrame(User user) {

        setTitle("访客操作面板 - " + user.getId());
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

        setJMenuBar(new GuestJMenuBar(this, user));

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("查看队员", new ViewJPanel(LoginJFrame.userList, user));
        tabbedPane.addTab("查看活动", new ActivityJPanel(activityList, user));

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
