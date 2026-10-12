package thingstodo;

import job.Person;

public class ActivityCreate {

    private final ActivityList activityList;

    public ActivityCreate(ActivityList activityList) {
        this.activityList = activityList;
    }

    public Activity create(String title, String content,Person creator) {
        if (creator == null) {
            throw new IllegalArgumentException("creator 不能为空");
        }

        Activity activity = new Activity(title, content, creator);

        if (creator.getRole() == Person.Role.Admin) {
            activityList.add(activity);
            System.out.println("[系统] Admin 创建的 Activity 已加入列表: " + activity);
        } else{
            System.out.println("你没有这个权限。");
        }

        return activity;
    }
}