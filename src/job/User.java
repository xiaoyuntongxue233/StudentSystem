package job;
import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Role {
        Guest, Member, Admin
    }
    private String id;
    private String password;
    private String secondPassword;
    private Role role;

    public User(String id, String password, Role role, String secondPassword) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.secondPassword = secondPassword;
    }

    public User() {
        this("0", "123456", Role.Guest, "123456");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecondPassword() {
        if (role == Role.Guest || role == Role.Member) {
            System.out.println("你不是管理员，不需要二级密码。");
            return null;
        } else {
            return secondPassword;
        }
    }

    public void setSecondPassword(String secondPassword) {
        if (role == Role.Guest || role == Role.Member) {
            System.out.println("你不是管理员，不需要二级密码。");
            return;
        } else {
            this.secondPassword = secondPassword;
        }
    }

}
