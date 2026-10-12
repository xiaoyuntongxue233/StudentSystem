package ui.adminpage;

import javax.swing.*;

import job.User;
import ui.adminpage.panel.activity.ActivityJPanel;
import ui.adminpage.panel.member.MemberJPanel;
import ui.login.LoginJFrame;
import ui.adminpage.panel.io.ImportExportJPanel;
import thingstodo.ActivityList;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdminOperatingJFrame extends AdminOperatingWindow {

    private final ActivityList activityList = LoginJFrame.activityList;

    public AdminOperatingJFrame(User user) {

        setTitle("管理员操作面板 - " + user.getId());
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

        setJMenuBar(new AdminJMenuBar(this, user));

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("成员管理", new MemberJPanel(LoginJFrame.userList, user));
        tabbedPane.addTab("活动管理", new ActivityJPanel(activityList, user));
        tabbedPane.addTab("导入导出", new ImportExportJPanel(LoginJFrame.userList, user));

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