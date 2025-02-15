public class PushNotification extends Notification {
    public PushNotification(String message) {
        super(message);
    }

    @Override
    public void sendNotification() {
        System.out.println("notificationPush");
    }
}
