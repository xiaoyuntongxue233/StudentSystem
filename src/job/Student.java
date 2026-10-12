package job;

public class Student extends Person {

    private static final long serialVersionUID = 3L;
    
    private int grade;
    private String className;
    private String major;

    public Student(String className, int grade, String major) {
        this.className = className;
        this.grade = grade;
        this.major = major;
    }

    public Student(String className, int grade, String major, int age, String gender, String name, String phoneNum, String id, String password, Role role, String secondPassword) {
        super(age, gender, name, phoneNum, id, password, role, secondPassword);
        this.className = className;
        this.grade = grade;
        this.major = major;
    }

    public Student() {
        this("0", 0, "无专业");
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

}
