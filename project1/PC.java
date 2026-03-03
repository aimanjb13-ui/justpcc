import java.util.UUID;

public abstract class PC {
    protected String orderID;
    protected String customerName;
    protected String cpu;
    protected int ramGB;
    protected String status; 
    protected String paymentMethod; 

    public static final String STATUS_PENDING = "Payment Pending";
    public static final String STATUS_PAID = "Paid";

    public PC(String name, String cpu, int ram) {
        this.customerName = name;
        this.cpu = cpu;
        this.ramGB = ram;
        this.orderID = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.status = STATUS_PENDING;
        this.paymentMethod = "Not Selected";
    }

    // --- ADD THIS METHOD ---
    public void setName(String name) {
        this.customerName = name;
    }

    public void setStatus(String s) { this.status = s; }
    public String getStatus() { return status; }
    public String getID() { return orderID; }
    public String getName() { return customerName; }
    
    public void setPaymentMethod(String m) { this.paymentMethod = m; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCpu() { return cpu; }
    public int getRam() { return ramGB; }

    public abstract double calculatePrice();
    public abstract String getType();
    public abstract String getDetails(); 

    public String getFileFormat() {
        return "ID:" + orderID + " | " + 
               "Name:" + customerName + " | " + 
               "Type:" + getType() + " | " + 
               "Price:$" + calculatePrice() + " | " + 
               "Status:" + status + " | " + 
               "Method:" + paymentMethod;
    }
}