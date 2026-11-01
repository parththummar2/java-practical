@FunctionalInterface interface Notifier { void send(String message); }
interface Urgent {}
public class prac6_B_NotificationSenders {
    public static void main(String[] args) {
        Notifier email = (Notifier & Urgent) message -> System.out.println("Email: " + message);
        Notifier sms = message -> System.out.println("SMS: " + message);
        Notifier[] senders = {email, sms};
        String message = "Hostel mess bill payment is due today.";
        System.out.println("--- Broadcast ---");
        for (Notifier sender : senders) {
            sender.send(message);
            if (sender instanceof Urgent) sender.send(message);
        }
    }
}
