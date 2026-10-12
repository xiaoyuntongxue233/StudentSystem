package thingstodo;

import java.util.ArrayList;

public class ActivityList {

    private final ArrayList<Activity> activities = new ArrayList<>();

    public void add(Activity activity) {
        if (activity != null) {
            activities.add(activity);
        }
    }

    public boolean remove(Activity activity) {
        return activities.remove(activity);
    }

    public ArrayList<Activity> getAll() {
        return activities;
    }

    public int size() {
        return activities.size();
    }

    public boolean isEmpty() {
        return activities.isEmpty();
    }

    public void printAll() {
        for (Activity a : activities) {
            System.out.println(a);
        }
    }

    public Activity findByTitle(String title) {
        for (Activity a : activities) {
            if (a.getTitle().equals(title)) {
                return a;
            }
        }
        return null;
    }

    public void clear() {
        activities.clear();
    }
}
