public class SMSNotification extends Notification {
    public SMSNotification(String message) {
        super(message);
    }

    @Override
    public void sendNotification() {
        System.out.println("notificationSMS");
    }
}
