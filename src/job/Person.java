package job;

public class Person extends User {

    private static final long serialVersionUID = 2L;
    
    private String name;
    private int age;
    private String gender;
    private String phoneNum;

    public Person() {
        this("空白", 0, "无性别", "00000000000");
    }

    public Person(String name, int age, String gender, String phoneNum) {
        this.age = age;
        this.gender = gender;
        this.name = name;
        this.phoneNum = phoneNum;

    }

    public Person(int age, String gender, String name, String phoneNum, String id, String password, Role role,
            String secondPassword) {
        super(id, password, role, secondPassword);
        this.age = age;
        this.gender = gender;
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNum() {
        if (getRole() == Role.Guest) {
            return maskPhoneNum();
        } else {
            return phoneNum;
        }
    }

    public String getRawPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String maskPhoneNum() {
        return phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, 11);
    }

}
