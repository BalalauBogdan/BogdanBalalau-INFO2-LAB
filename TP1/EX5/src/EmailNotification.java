public class EmailNotification extends Notification {
    public EmailNotification(String message) {
        super(message);
    }

    @Override
    public void sendNotification() {
        System.out.println("notificationEmail");
    }
}
