import java.util.Map;

public abstract class DeliveringSupplier extends Supplier {


    public DeliveringSupplier(String name, String business_id, String payment_method, String suplier_ID, Contact_Person person, Contract contract, Map<Item,Pair<Integer, Float>> items) {
        super(name, business_id, payment_method, suplier_ID, person, contract, items);
    }



}
