public class PrebuiltPC extends PC {
    private String modelName;
    private double fixedPrice;

    public PrebuiltPC(String customer, String cpu, int ram, String model, double price) {
        super(customer, cpu, ram);
        this.modelName = model;
        this.fixedPrice = price;
    }

    @Override
    public double calculatePrice() {
        return fixedPrice; 
    }

    @Override
    public String getType() {
        return "Prebuilt (" + modelName + ")";
    }

    @Override
    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(orderID).append(" | Customer: ").append(customerName).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("MODEL: " + modelName + "\n");
        sb.append("Specs: " + cpu + ", " + ramGB + "GB RAM\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("TOTAL PRICE:              $%.2f", calculatePrice()));
        return sb.toString();
    }
}