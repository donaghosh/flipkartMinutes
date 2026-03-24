Description

Implementation of the core system for Flipkart Minutes, Flipkart's instant delivery platform, enabling customers to order any item for delivery within minutes. The system efficiently manage customers, delivery partners, and orders to ensure rapid, reliable fulfillment.

Core Requirements
1. Onboarding
• The system support the onboarding of new customers and delivery partners.
2. Order Placement & Cancellation
• Customers can place orders for any item (item name or ID is specified in the order).
• In-Stock/Out-Stock Constraint: Items may be in stock or out of stock. Orders can only be placed for items that are in stock.
• Customers can cancel orders if they have not yet been picked up by a delivery partner.
3. Order Assignment & Fulfillment
• Orders are auto-assigned to any available delivery partner.
• There could be multiple assignment strategies: The system supports multiple assignment strategies (e.g., round-robin, least-busy partner, highest-rated partner). The strategy is configurable. The default strategy can be assigned to any available partner.
• If no delivery partner is available, the order remains in a queue and is assigned as soon as a partner becomes free.
• Each delivery partner can handle only one order at a time.
• The number of ongoing orders can exceed the number of delivery partners (orders may queue).
• Delivery partners can pick up assigned orders and mark them as delivered.
• Canceled orders does not get assigned to a delivery partner
• If an assigned order gets canceled before pickup, the delivery partner becomes available for other orders
• Once a delivery partner picks up an order, the order cannot be canceled by the customer or the system.
• Assume delivery partners are available 24x7. Ignore travel time.
4. Status Tracking
• The system provides real-time status for orders and delivery partners.
5. Concurrency & Thread Safety
• The system is thread-safe and handle all concurrency scenarios (multiple customers and partners acting simultaneously).
Bonus Features
• Notifications: Notify customers and delivery partners (via simulated logs) on order status changes.
• Ratings: Customers can rate delivery partners after successful delivery.
Dashboard: Showing top delivery partners based on the number of deliveries and ratings.
• Auto-cancel: If no delivery partner picks up the order within 30 minutes of its creation, the order is canceled automatically, regardless of whether an order has been assigned to a delivery partner or not
