package ui.adminpage.panel.io;

import job.User;

import javax.swing.*;
import java.awt.*;

public class ExportDialog extends JDialog {

    private final User admin;
    private String actionDescribe;   
    private boolean confirmed = false;

    public ExportDialog(Window owner, User admin) {
        this(owner, admin, "导出全部数据");
    }

    public ExportDialog(Window owner, User admin, String actionDescribe) {
        super(owner, "二级密码确认", ModalityType.APPLICATION_MODAL);
        this.admin = admin;
        this.actionDescribe = actionDescribe;

        setSize(420, 200);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel hint = new JLabel(actionDescribe + " 需要二次密码确认：");
        hint.setFont(new Font("等线", Font.BOLD, 15));
        add(hint, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(new JLabel("二级密码："), gbc);
        gbc.gridx = 1;
        JPasswordField pf = new JPasswordField(15);
        add(pf, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton okJButton = new JButton("确认");
        JButton cancelJButton = new JButton("取消");
        btnPanel.add(okJButton);
        btnPanel.add(cancelJButton);
        add(btnPanel, gbc);

        okJButton.addActionListener(e -> {
            String input = new String(pf.getPassword());
            String real = admin.getSecondPassword();

            if (real == null || real.isEmpty()) {
                JOptionPane.showMessageDialog(this, "当前管理员未设置二级密码，操作取消！");
                return;
            }
            if (!real.equals(input)) {
                JOptionPane.showMessageDialog(this, "二级密码错误！");
                pf.setText("");
                return;
            }
            confirmed = true;
            dispose();
        });

        cancelJButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        getRootPane().setDefaultButton(okJButton);
        pf.requestFocusInWindow();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
