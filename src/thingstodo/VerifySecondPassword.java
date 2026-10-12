package thingstodo;

import job.*;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class VerifySecondPassword {

    public static boolean verifySecondPassword(User self) {
        if (self == null) {
            return false;
        }
        if (self.getRole() != User.Role.Admin) {
            JOptionPane.showMessageDialog(null,"当前账号不是管理员，无法通过二级密码删除自己。","提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String real = self.getSecondPassword();
        if (real == null) {
            JOptionPane.showMessageDialog(null,"当前管理员没有设置二级密码，无法删除自己。", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        for (int attempt = 1; attempt <= 3; attempt++) {
            JPasswordField pf = new JPasswordField();
            int ok = JOptionPane.showConfirmDialog(
                    null, pf,
                    "删除自己需要输入二级密码（第 " + attempt + "/3 次）：",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (ok != JOptionPane.OK_OPTION) {
                return false;
            }

            String input = new String(pf.getPassword());
            if (real.equals(input)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(null,"二级密码错误！","提示", JOptionPane.WARNING_MESSAGE);
            }
        }
        return false;
    }
}
