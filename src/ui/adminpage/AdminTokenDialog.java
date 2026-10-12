package ui.adminpage;

import thingstodo.AdminTokenList;
import ui.login.LoginJFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminTokenDialog extends JDialog {

    private final AdminTokenList tokenList;

    private JTextField searchField;
    private JTextField newTokenField;
    private JTable tokenTable;
    private DefaultTableModel tableModel;
    private JLabel countJLabel;

    public AdminTokenDialog(Window owner) {
        super(owner, "管理员口令管理", ModalityType.APPLICATION_MODAL);
        this.tokenList = LoginJFrame.adminTokenList;

        setSize(600, 480);
        setLocationRelativeTo(owner);

        JPanel contentJPanel = new JPanel(new BorderLayout(10, 10));
        contentJPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentJPanel);

        JPanel northJPanel = new JPanel(new BorderLayout(5, 5));

        JLabel hintJLabel = new JLabel("管理员口令用于注册新管理员时校验身份，可以添加、删除、查询。");
        hintJLabel.setFont(new Font("等线", Font.PLAIN, 14));
        northJPanel.add(hintJLabel, BorderLayout.NORTH);

        JPanel topJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topJPanel.setBackground(Color.decode("#006915"));

        JLabel searchJLabel = new JLabel("口令关键字：");
        searchJLabel.setForeground(Color.WHITE);
        topJPanel.add(searchJLabel);

        searchField = new JTextField(15);
        topJPanel.add(searchField);

        JButton searchJButton = new JButton("查询");
        searchJButton.addActionListener(e -> doSearch());
        topJPanel.add(searchJButton);

        JButton showAllJButton = new JButton("显示全部");
        showAllJButton.addActionListener(e -> refreshTable());
        topJPanel.add(showAllJButton);

        countJLabel = new JLabel("共 0 条");
        countJLabel.setForeground(Color.WHITE);
        topJPanel.add(countJLabel);

        northJPanel.add(topJPanel, BorderLayout.CENTER);
        contentJPanel.add(northJPanel, BorderLayout.NORTH);

        String[] columns = { "序号", "管理员口令" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tokenTable = new JTable(tableModel);
        tokenTable.setRowHeight(25);
        tokenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contentJPanel.add(new JScrollPane(tokenTable), BorderLayout.CENTER);

        JPanel bottomJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomJPanel.setBackground(Color.decode("#006915"));

        JLabel newTokenJLabel = new JLabel("新口令：");
        newTokenJLabel.setForeground(Color.WHITE);
        bottomJPanel.add(newTokenJLabel);

        newTokenField = new JTextField(15);
        bottomJPanel.add(newTokenField);

        JButton addJButton = new JButton("添加");
        addJButton.addActionListener(e -> doAdd());
        bottomJPanel.add(addJButton);

        JButton deleteJButton = new JButton("删除选中");
        deleteJButton.addActionListener(e -> doDelete());
        bottomJPanel.add(deleteJButton);

        JButton closeJButton = new JButton("关闭");
        closeJButton.addActionListener(e -> dispose());
        bottomJPanel.add(closeJButton);

        contentJPanel.add(bottomJPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(addJButton);

        refreshTable();
    }

    public void refreshTable() {
        fillTable(tokenList.getAll());
    }

    private void fillTable(ArrayList<String> tokens) {
        tableModel.setRowCount(0);
        for (int i = 0; i < tokens.size(); i++) {
            tableModel.addRow(new Object[] { i + 1, tokens.get(i) });
        }
        countJLabel.setText("共 " + tokens.size() + " 条");
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        ArrayList<String> result = tokenList.search(keyword);
        fillTable(result);

        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有匹配的管理员口令。");
        } else {
            JOptionPane.showMessageDialog(this, "查询到 " + result.size() + " 条管理员口令。");
        }
    }

    private void doAdd() {
        String token = newTokenField.getText().trim();
        if (token.isEmpty()) {
            JOptionPane.showMessageDialog(this, "口令不能为空！");
            return;
        }
        if (tokenList.contains(token)) {
            JOptionPane.showMessageDialog(this, "该口令已存在！");
            return;
        }

        tokenList.addToken(token);
        LoginJFrame.saveAdminTokens();
        newTokenField.setText("");
        refreshTable();
        JOptionPane.showMessageDialog(this, "添加成功！");
    }

    private void doDelete() {
        int row = tokenTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的口令！");
            return;
        }

        String token = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定删除管理员口令 [" + token + "] 吗？\n删除后该口令将不能再用于注册管理员。",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (tokenList.removeToken(token)) {
            LoginJFrame.saveAdminTokens();
            refreshTable();
            JOptionPane.showMessageDialog(this, "删除成功！");
        } else {
            JOptionPane.showMessageDialog(this, "删除失败。");
        }
    }
}
