
package chain;

import java.util.ArrayList;
import job.User;

public class UserLinkedList {

    private String listName;
    private UserNode head;
    private int size;

    public UserLinkedList() {
        this.head = null;
        this.size = 0;
    }

    public UserNode getHead() {
        return head;
    }

    public int getSize() {
        return size;
    }

    public void setHead(UserNode head) {
        this.head = head;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isEmpty() {
        return getHead() == null;
    }

    public void addUser(User s) {
        UserNode newUserNode = new UserNode(s);
        if (head == null) {
            head = newUserNode;
        } else {
            UserNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newUserNode;
        }
        size++;
    }

    public UserNode findUser(String id) {
        UserNode current = head;
        while (current != null) {
            if (current.data.getId().equals(id)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    public boolean checkPassword(String id, String password) {
        UserNode node = findUser(id);
        if (node == null) {
            return false;
        }
        return node.data.getPassword().equals(password);
    }

    public boolean removeUser(String id) {
        if (head == null) {
            return false;
        }

        if (head.data.getId().equals(id)) {
            head = head.next;
            size--;
            return true;
        }

        UserNode prev = head;
        UserNode current = head.next;
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

    public void clear() {
        head = null;
        size = 0;
    }

    public boolean containsId(String id) {
        return findUser(id) != null;
    }

    public void printAll() {
        if (head == null) {
            return;
        }
        UserNode current = head;
        while (current != null) {
            User s = current.data;
            System.out.println(s.getId());
            current = current.next;
        }
    }

    public ArrayList<User> toList() {
        ArrayList<User> list = new ArrayList<>();
        UserNode current = head;
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
