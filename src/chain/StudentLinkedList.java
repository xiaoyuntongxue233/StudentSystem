package chain;

import java.util.ArrayList;
import job.Student;

import java.io.Serializable;

public class StudentLinkedList implements Serializable {
    
    private static final long serialVersionUID = 6L;

    private String listName;
    private StuNode head;
    private int size;

    public StudentLinkedList() {
        this.head = null;
        this.size = 0;
    }

    public StuNode getHead() {
        return head;
    }

    public int getSize() {
        return size;
    }

    public void setHead(StuNode head) {
        this.head = head;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isEmpty() {
        return getHead() == null;
    }

    public void addStu(Student s) {
        StuNode newStuNode = new StuNode(s);
        if (head == null) {
            head = newStuNode;
        } else {
            StuNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newStuNode;
        }
        size++;
    }

    public StuNode findStu(String id) {
        StuNode current = head;
        while (current != null) {
            if (current.data.getId().equals(id)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    public boolean removeStu(String id) {
        if (head == null) {
            return false;
        }

        if (head.data.getId().equals(id)) {
            head = head.next;
            size--;
            return true;
        }

        StuNode prev = head;
        StuNode current = head.next;
        while (current != null) {
            if (current.data.getId().equals(id)) {
                prev.next = current.next;   
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;   
    }

    public boolean containsId(String id) {
        return findStu(id) != null;
    }

    public void printAll() {
        if (head == null) {
            return;
        }
        StuNode current = head;
        while (current != null) {
            Student s = current.data;
            System.out.println(s.getName() + "\t" + s.getAge() + "\t" + s.getId());
            current = current.next;
        }
    }

    public ArrayList<Student> toList() {
        ArrayList<Student> list = new ArrayList<>();
        StuNode current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
