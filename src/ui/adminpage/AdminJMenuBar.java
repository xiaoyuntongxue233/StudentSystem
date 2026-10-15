package ui.adminpage;

import javax.swing.*;

import job.User;
import ui.login.LoginJFrame;

public class AdminJMenuBar extends JMenuBar {

    private final AdminOperatingJFrame owner;
    private final User currentUser;

    public AdminJMenuBar(AdminOperatingJFrame owner, User currentUser) {
        this.owner = owner;
        this.currentUser = currentUser;

        JMenu systemMenu = new JMenu("系统");
        JMenuItem switchUserItem = new JMenuItem("切换用户");
        switchUserItem.addActionListener(e -> doSwitchUser());
        systemMenu.add(switchUserItem);
        systemMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("退出系统");
        exitItem.addActionListener(e -> doExit());
        systemMenu.add(exitItem);
        add(systemMenu);

        JMenu accountMenu = new JMenu("账户");
        JMenuItem myInfoItem = new JMenuItem("我的信息");
        myInfoItem.addActionListener(e -> showAccountDialog());
        accountMenu.add(myInfoItem);
        add(accountMenu);

        JMenu tokenMenu = new JMenu("口令");
        JMenuItem manageTokenItem = new JMenuItem("管理员口令管理");
        manageTokenItem.addActionListener(e -> showTokenDialog());
        tokenMenu.add(manageTokenItem);
        add(tokenMenu);

        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
                owner,
                "小型学生管理系统 v0.3.1",
                "关于", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        add(helpMenu);
    }

    private void showAccountDialog() {
        AdminAccount dialog = new AdminAccount(owner, currentUser);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            JOptionPane.showMessageDialog(owner,
                    "账户信息已更新。",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showTokenDialog() {
        AdminTokenDialog dialog = new AdminTokenDialog(owner);
        dialog.setVisible(true);
    }

    private void doSwitchUser() {
        int confirm = JOptionPane.showConfirmDialog(
                owner,
                "确定要切换用户吗？\n当前未保存的操作将会丢失。",
                "切换用户",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        owner.dispose();
        new LoginJFrame();
    }

    private void doExit() {
        owner.doExit();
    }
}