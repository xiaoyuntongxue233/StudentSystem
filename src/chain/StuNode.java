package chain;

import java.io.Serializable;
import job.Student;

public class StuNode implements Serializable {
    
    private static final long serialVersionUID = 7L;

    public Student data;    
    public StuNode next;       

    public StuNode(Student data) {
        this.data = data;
        this.next = null;
    }
}