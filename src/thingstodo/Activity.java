package thingstodo;
import job.Person;
import thingstodo.time.Time;
import java.io.Serializable;

public class Activity implements Serializable {

    private static final long serialVersionUID = 4L;
    
    private Time startTime;
    private Time endTime;
    private String title;
    private String content;
    private Person creator;
    private ActivityMemberList studentList;
    

    public Activity() {
        this("请输入标题","请输入内容");
    }

    public Activity(String title, String content) {
        this.content = content;
        this.title = title;
        this.studentList = new ActivityMemberList();
    }

    public Activity(String title, String content,Person creator) {
        this.content = content;
        this.title = title;
        this.creator = creator;
        this.studentList = new ActivityMemberList();
    }

    public Activity(String title, String content, Time startTime,Person creator) {
        this.content = content;
        this.startTime = startTime;
        this.title = title;
        this.creator = creator;
        this.studentList = new ActivityMemberList();
    }

    public Activity(String title, String content, Time startTime, Time endTime,Person creator) {
        this.content = content;
        this.endTime = endTime;
        this.startTime = startTime;
        this.title = title;
        this.creator = creator;
        this.studentList = new ActivityMemberList();
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ActivityMemberList getStudentList() {
        return studentList;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

}
