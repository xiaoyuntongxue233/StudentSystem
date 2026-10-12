package thingstodo;
import job.Person;
import thingstodo.time.Time;
import java.io.Serializable;

public class Activity implements Serializable {

    private static final long serialVersionUID = 4L;

    public static final int NOT_STARTED = 0;
    public static final int RUNNING = 1;
    public static final int FINISHED = 2;

    private Time startTime;
    private Time endTime;
    private Time signUpStartTime;
    private Time signUpEndTime;
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

    public Time getSignUpStartTime() {
        return signUpStartTime;
    }

    public void setSignUpStartTime(Time signUpStartTime) {
        this.signUpStartTime = signUpStartTime;
    }

    public Time getSignUpEndTime() {
        return signUpEndTime;
    }

    public void setSignUpEndTime(Time signUpEndTime) {
        this.signUpEndTime = signUpEndTime;
    }

    public int getStatus() {
        Time now = Time.now();
        if (startTime != null && now.isBefore(startTime)) {
            return NOT_STARTED;
        }
        if (endTime != null && now.isAfter(endTime)) {
            return FINISHED;
        }
        return RUNNING;
    }

    public String getStatusText() {
        int status = getStatus();
        if (status == NOT_STARTED) {
            return "未开始";
        }
        if (status == FINISHED) {
            return "已结束";
        }
        return "进行中";
    }

    public boolean isSignUpOpen(Time now) {
        if (signUpStartTime == null || signUpEndTime == null) {
            return true;
        }
        return now.isBetween(signUpStartTime, signUpEndTime);
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
