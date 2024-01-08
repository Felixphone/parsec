package launcher.watchdog.notifications;

import java.util.Date;

public class IsAliveNotification extends WatchdogNotification {

    private Date postTime;

    public IsAliveNotification() {
        postTime = new Date();
    }

    public Date getPostTime() {
        return postTime;
    }
}
