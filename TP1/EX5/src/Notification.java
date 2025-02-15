public abstract class Notification implements Notifiable {
    private String message;

    public Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void displayNotification() {
        System.out.println(message);
    }

    @Override
    public void sendNotification() {
        System.out.println("notification");
    }
}
