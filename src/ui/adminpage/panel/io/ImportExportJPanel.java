package ui.adminpage.panel.io;

import chain.UserLinkedList;
import job.Student;
import job.User;
import ui.login.LoginJFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImportExportJPanel extends JPanel {

    private final UserLinkedList userList;
    private final User currentUser;

    private JTextArea logArea;

    private static final String DEFAULT_EXPORT_PATH = "students_export.txt";
    private static final SimpleDateFormat TIME_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ImportExportJPanel(UserLinkedList userList, User currentUser) {
        this.userList = userList;
        this.currentUser = currentUser;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBackground(Color.decode("#006915"));

        JButton exportBtn = new JButton("导出全部数据");
        exportBtn.addActionListener(e -> doExport());
        topPanel.add(exportBtn);

        JButton importBtn = new JButton("导入成员数据");
        importBtn.addActionListener(e -> doImport());
        topPanel.add(importBtn);

        JButton clearLogBtn = new JButton("清空日志");
        clearLogBtn.addActionListener(e -> logArea.setText(""));
        topPanel.add(clearLogBtn);

        add(topPanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("格式说明"));
        infoPanel.setBackground(Color.WHITE);
        JLabel jLabel1 = new JLabel("导出文件格式：每行一个成员，字段以 \\t（制表符）分隔");
        JLabel jLabel2 = new JLabel("字段顺序：ID \\t 姓名 \\t 年龄 \\t 性别 \\t 班级 \\t 专业 \\t 手机号 \\t 角色");
        jLabel1.setFont(new Font("仿宋", Font.BOLD, 20));
        jLabel2.setFont(new Font("仿宋", Font.BOLD, 20));
        infoPanel.add(jLabel1);
        infoPanel.add(jLabel2);

        add(infoPanel, BorderLayout.CENTER);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Dialog", Font.PLAIN, 16));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("操作日志"));
        scroll.setPreferredSize(new Dimension(900, 250));
        add(scroll, BorderLayout.SOUTH);

        log("系统就绪。当前共 " + countStudents() + " 名学生。");
    }

    private void doExport() {
        ExportDialog dialog = new ExportDialog(
                SwingUtilities.getWindowAncestor(this), currentUser);
        dialog.setVisible(true);

        if (!dialog.isConfirmed()) {
            log("[取消] 用户取消了导出操作。");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择导出文件保存位置");
        chooser.setSelectedFile(new File(DEFAULT_EXPORT_PATH));
        chooser.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            log("[取消] 未选择保存位置。");
            return;
        }

        File target = chooser.getSelectedFile();
        if (!target.getName().toLowerCase().endsWith(".txt")) {
            target = new File(target.getAbsolutePath() + ".txt");
        }

        int count = 0;
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8))) {

            for (User u : userList.toList()) {
                if (u instanceof Student) {
                    Student s = (Student) u;
                    bw.write(s.getId() + "\t"
                            + s.getName() + "\t"
                            + s.getAge() + "\t"
                            + s.getGender() + "\t"
                            + s.getClassName() + "\t"
                            + s.getMajor() + "\t"
                            + s.getRawPhoneNum() + "\t"
                            + roleToText(s.getRole()));
                    bw.newLine();
                    count++;
                }
            }
            log("[导出成功] " + TIME_FMT.format(new Date())
                    + "  共导出 " + count + " 条记录 → " + target.getAbsolutePath());
        } catch (IOException e) {
            log("[导出失败] " + e.getMessage());
            JOptionPane.showMessageDialog(this, "导出失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doImport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择要导入的文件");
        chooser.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            log("[取消] 未选择导入文件。");
            return;
        }

        File source = chooser.getSelectedFile();
        if (!source.exists()) {
            log("[失败] 文件不存在：" + source.getAbsolutePath());
            return;
        }

        String[] options = { "追加（保留原有）", "替换（清空后导入）", "取消" };
        int mode = JOptionPane.showOptionDialog(
                this,
                "请选择导入方式：\n\n" +
                        "· 追加：保留现有成员，只添加文件中的新成员\n" +
                        "· 替换：清空现有成员，仅保留文件中的成员（需二级密码）",
                "导入方式",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (mode == 2 || mode == JOptionPane.CLOSED_OPTION) {
            log("[取消] 用户取消导入。");
            return;
        }

        boolean replace = (mode == 1);

        java.util.List<Student> parsed = new java.util.ArrayList<>();
        java.util.Set<String> parsedIds = new java.util.HashSet<>();

        int skip = 0;
        StringBuilder skipDetail = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(source), StandardCharsets.UTF_8))) {

            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split("\t");
                if (parts.length != 8) {
                    skip++;
                    skipDetail.append("  第 ").append(lineNo).append(" 行：字段数量不是 8\n");
                    continue;
                }

                String id = parts[0].trim();
                String name = parts[1].trim();
                int age;
                try {
                    age = Integer.parseInt(parts[2].trim());
                    if (age < 0 || age > 150)
                        throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    skip++;
                    skipDetail.append("  第 ").append(lineNo).append(" 行：年龄非法\n");
                    continue;
                }

                String gender = parts[3].trim();
                String className = parts[4].trim();
                String major = parts[5].trim();
                String phone = parts[6].trim();
                String roleText = parts[7].trim();

                if (id.isEmpty() || name.isEmpty()) {
                    skip++;
                    skipDetail.append("  第 ").append(lineNo).append(" 行：ID 或姓名为空\n");
                    continue;
                }

                if (parsedIds.contains(id)) {
                    skip++;
                    skipDetail.append("  第 ").append(lineNo).append(" 行：文件内 ID ").append(id).append(" 重复\n");
                    continue;
                }

                User.Role role = parseRole(roleText);
                if (role == null) {
                    skip++;
                    skipDetail.append("  第 ").append(lineNo).append(" 行：角色非法（").append(roleText).append("）\n");
                    continue;
                }

                Student s = new Student(className, 0, major,
                        age, gender, name, phone, id, "123456", role, null);
                parsed.add(s);
                parsedIds.add(id);
            }
        } catch (IOException e) {
            log("[导入失败] " + e.getMessage());
            JOptionPane.showMessageDialog(this, "导入失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (parsed.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "文件中没有可导入的有效数据，未做任何修改。",
                    "导入取消", JOptionPane.WARNING_MESSAGE);
            log("[取消] 文件无有效数据，未修改现有数据。");
            return;
        }


        if (replace) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "替换导入将清空现有全部成员。\n" +
                            "确定继续吗？",
                    "确认替换导入",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                log("[取消] 用户取消了替换导入。");
                return;
            }

            ExportDialog pwdDialog = new ExportDialog(
                    SwingUtilities.getWindowAncestor(this),
                    currentUser,
                    "替换导入（清空现有成员）");
            pwdDialog.setVisible(true);

            if (!pwdDialog.isConfirmed()) {
                log("[取消] 二级密码校验未通过，替换导入取消。");
                return;
            }
        }

        int added = 0;

        if (replace) {
            userList.clear();
            for (Student s : parsed) {
                userList.addUser(s);
                added++;
            }
        } else {
            for (Student s : parsed) {
                if (userList.containsId(s.getId())) {
                    skip++;
                    continue;
                }
                userList.addUser(s);
                added++;
            }
        }

        log("[导入完成] " + TIME_FMT.format(new Date())
                + "  成功 " + added + " 条，跳过 " + skip + " 条  ← " + source.getName());

        if (skip > 0) {
            log("跳过详情：\n" + skipDetail);
        }

        JOptionPane.showMessageDialog(this,
                "导入完成！\n成功：" + added + " 条\n跳过：" + skip + " 条",
                "导入结果", JOptionPane.INFORMATION_MESSAGE);

    }
    
    

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private int countStudents() {
        int c = 0;
        for (User u : userList.toList()) {
            if (u instanceof Student)
                c++;
        }
        return c;
    }

    private String roleToText(User.Role role) {
        if (role == User.Role.Guest)
            return "访客";
        if (role == User.Role.Member)
            return "成员";
        if (role == User.Role.Admin)
            return "管理员";
        return "未知";
    }

    private User.Role parseRole(String text) {
        if ("访客".equals(text))
            return User.Role.Guest;
        if ("成员".equals(text))
            return User.Role.Member;
        if ("管理员".equals(text))
            return User.Role.Admin;
        return null;
    }
}