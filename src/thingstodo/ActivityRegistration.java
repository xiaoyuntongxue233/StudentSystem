package thingstodo;

import java.util.Scanner;
import job.Student;

public class ActivityRegistration {

    private final Activity activity;

    
    
    Scanner sc = new Scanner(System.in);

    public ActivityRegistration(Activity activity) {
        this.activity = activity;
    }

    public void addStu(Student s) {

        ActivityMemberList list = activity.getStudentList();

        if (s == null) {
            System.out.println("学生为空。");

        } else {
            String id = s.getId();
            if (list.containsId(id)) {
                System.out.println("学号为" + id + "的学生已存在。");

            } else if (!list.isFull()) {
                list.addStu(s);
                System.out.println("已加入。");

            } else if (!list.isWaitingFull()) {
                System.out.println("人数已满。是否成为第" + (list.getCurrentSize() - list.getMaxMemberNum() + 1) + "个候补者？");
                sc.nextLine();
                list.addStu(s);
                System.out.println("已加入。");

            } else {
                System.out.println("候补人数已满。");
            }
        }

    }

    
}
