package repository;

import entity.DeliveryPartner;
import enums.PartnerStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeliveryPartnerRepository implements IDeliveryPartnerRepository {
    private final Map<String, DeliveryPartner> storage = new ConcurrentHashMap<>();

    @Override
    public void save(DeliveryPartner partner) {
        storage.put(partner.getPartnerId(), partner);
    }

    @Override
    public DeliveryPartner findById(String id) {
        return storage.get(id);
    }

    @Override
    public List<DeliveryPartner> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<DeliveryPartner> findAvailable() {
        List<DeliveryPartner> available = new ArrayList<>();
        for (DeliveryPartner partner : storage.values()) {
            if (partner.getStatus() == PartnerStatus.AVAILABLE) {
                available.add(partner);
            }
        }
        return available;
    }
}

