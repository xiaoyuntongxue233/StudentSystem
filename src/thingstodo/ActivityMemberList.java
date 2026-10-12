package thingstodo;

import chain.StudentLinkedList;
import job.Student;
import java.io.Serializable;

public class ActivityMemberList implements Serializable {

    private static final long serialVersionUID = 8L;

    private int maxMemberNum = 0;
    private int waitingMemberNum = 0;

    private StudentLinkedList studentList;

    public ActivityMemberList() {
        this(0);
    }

    public ActivityMemberList(int maxMemberNum) {
        this.maxMemberNum = maxMemberNum;
        this.studentList = new StudentLinkedList();
    }

    public boolean ContainsStudent(String id) {
        return studentList.containsId(id);
    }

    public Student findStudent(String id) {
        if (studentList.findStu(id) != null) {
            return (studentList.findStu(id)).data;
        } else {
            return null;
        }
    }

    public boolean containsId(String id) {
        return studentList.containsId(id);
    }

    public void addStu(Student s) {
        studentList.addStu(s);
    }

    public int getCurrentSize() {
        return studentList.getSize();
    }

    public java.util.List<Student> truncateToCapacity() {
        java.util.List<Student> removed = new java.util.ArrayList<>();
        if (maxMemberNum == 0)
            return removed;
        int capacity = getTotalCapacity();
        while (studentList.getSize() > capacity) {
            Student last = getLastStudent();
            if (last == null)
                break;
            studentList.removeStu(last.getId());
            removed.add(last);
        }
        return removed;
    }

    private Student getLastStudent() {
        java.util.List<Student> all = studentList.toList();
        if (all.isEmpty())
            return null;
        return all.get(all.size() - 1);
    }

    public boolean isEmpty() {
        return studentList.isEmpty();
    }

    public boolean isFull() {
        return studentList.getSize() >= maxMemberNum;
    }

    public boolean isWaitingFull() {
        return studentList.getSize() >= maxMemberNum + getWaitingMemberNum();
    }

    public int getMaxMemberNum() {
        return maxMemberNum;
    }

    public void setMaxMemberNum(int maxMenberNum) {
        this.maxMemberNum = maxMenberNum;
    }

    public void printAll() {
        studentList.printAll();
    }

    public StudentLinkedList returnAll() {
        return studentList;
    }

    public int getWaitingMemberNum() {
        return waitingMemberNum;
    }

    public void setWaitingMemberNum(int waitingMemberNum) {
        this.waitingMemberNum = waitingMemberNum;
    }

    public int getTotalCapacity() {
        return maxMemberNum + getWaitingMemberNum();
    }

    public int getOverflowCount() {
        int total = getTotalCapacity();
        if (maxMemberNum == 0)
            return 0;
        return Math.max(0, studentList.getSize() - total);
    }
}
