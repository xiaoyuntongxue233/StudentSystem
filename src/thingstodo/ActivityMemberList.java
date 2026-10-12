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
        int capacity = getTotalCapacity();
        if (capacity == Integer.MAX_VALUE)
            return removed;
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
        if (isUnlimited()) {
            return false;
        }
        return studentList.getSize() >= maxMemberNum;
    }

    public boolean isWaitingFull() {
        return studentList.getSize() >= getTotalCapacity();
    }

    public boolean isUnlimited() {
        return maxMemberNum < 0;
    }

    public boolean isForbidden() {
        return maxMemberNum == 0;
    }

    public boolean isFormalMember(int index) {
        if (isUnlimited()) {
            return true;
        }
        return index < maxMemberNum;
    }

    public String getMaxMemberNumText() {
        if (isUnlimited()) {
            return "不限";
        }
        if (isForbidden()) {
            return "禁止报名";
        }
        return String.valueOf(maxMemberNum);
    }

    public String getWaitingMemberNumText() {
        if (maxMemberNum <= 0) {
            return "-";
        }
        return String.valueOf(waitingMemberNum);
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
        if (isUnlimited()) {
            return Integer.MAX_VALUE;
        }
        if (isForbidden()) {
            return 0;
        }
        return maxMemberNum + getWaitingMemberNum();
    }

    public int getOverflowCount() {
        int total = getTotalCapacity();
        if (total == Integer.MAX_VALUE) {
            return 0;
        }
        return Math.max(0, studentList.getSize() - total);
    }
}
