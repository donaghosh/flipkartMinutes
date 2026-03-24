package usermanagement;

import entity.Customer;
import entity.DeliveryPartner;
import strategy.RankingStrategy;

import java.util.List;

public interface IUserManagementService {
    void onboardCustomer(String customerId, String name);
    void onboardDeliveryPartner(String partnerId, String name);
    Customer getCustomer(String customerId);
    DeliveryPartner getPartner(String partnerId);
    List<String> getTopDeliveryPartners();
    void setRankingStrategy(RankingStrategy rankingStrategy);
}
