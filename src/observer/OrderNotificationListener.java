package observer;

public class OrderNotificationListener implements OrderEventListener {
    @Override
    public void onOrderStatusChanged(OrderEvent event) {
        System.out.println("[NOTIFICATION] Order " + event.getOrder().getOrderId() +
                " status changed: " + event.getPreviousStatus() + " -> " + event.getNewStatus());
    }
}
