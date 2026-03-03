public class CustomBuiltPC extends PC {
    private boolean hasLiquidCooling;
    private boolean hasRGB;
    private String gpuTier; 

    // PRICING CONSTANTS
    private static final double BASE_FEE = 500.0;
    private static final double PRICE_HIGH_CPU = 500.0; // i9 or Ryzen 9
    private static final double PRICE_MID_CPU = 300.0;  // i7 or Ryzen 7
    private static final double PRICE_LOW_CPU = 150.0;  // i5
    private static final double PRICE_RAM_PER_GB = 5.0;
    private static final double PRICE_GPU_ULTRA = 1200.0;
    private static final double PRICE_GPU_HIGH = 700.0;
    private static final double PRICE_GPU_MID = 300.0;
    private static final double PRICE_LIQUID = 150.0;
    private static final double PRICE_RGB = 50.0;

    public CustomBuiltPC(String customer, String cpu, int ram, String gpu, boolean liquid, boolean rgb) {
        super(customer, cpu, ram);
        this.gpuTier = gpu;
        this.hasLiquidCooling = liquid;
        this.hasRGB = rgb;
    }

    @Override
    public double calculatePrice() {
        double total = BASE_FEE;
        
        // CPU Logic
        if(cpu.contains("i9") || cpu.contains("Ryzen 9")) total += PRICE_HIGH_CPU;
        else if(cpu.contains("i7") || cpu.contains("Ryzen 7")) total += PRICE_MID_CPU;
        else total += PRICE_LOW_CPU;

        // RAM Logic
        total += (ramGB * PRICE_RAM_PER_GB); 

        // GPU Logic
        if(gpuTier.contains("Ultra")) total += PRICE_GPU_ULTRA;
        else if(gpuTier.contains("High")) total += PRICE_GPU_HIGH;
        else total += PRICE_GPU_MID;

        // Extras
        if(hasLiquidCooling) total += PRICE_LIQUID;
        if(hasRGB) total += PRICE_RGB;

        return total;
    }

    @Override
    public String getType() { return "Custom Config"; }

    @Override
    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(orderID).append(" | Customer: ").append(customerName).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("BASE SYSTEM FEE:          $" + BASE_FEE + "\n");
        sb.append(String.format("CPU (%s):       +$%.2f\n", cpu.split("\\(")[0], getCpuPrice())); 
        sb.append(String.format("RAM (%d GB):             +$%.2f\n", ramGB, (double)ramGB * PRICE_RAM_PER_GB));
        sb.append(String.format("GPU (%s):               +$%.2f\n", gpuTier.split(":")[0], getGpuPrice()));
        
        if(hasLiquidCooling) sb.append("Liquid Cooling:           +$" + PRICE_LIQUID + "\n");
        if(hasRGB)            sb.append("RGB Lighting:             +$" + PRICE_RGB + "\n");
        
        sb.append("------------------------------------------\n");
        sb.append(String.format("TOTAL PRICE:              $%.2f", calculatePrice()));
        
        return sb.toString();
    }

    private double getCpuPrice() {
        if(cpu.contains("i9") || cpu.contains("Ryzen 9")) return PRICE_HIGH_CPU;
        if(cpu.contains("i7") || cpu.contains("Ryzen 7")) return PRICE_MID_CPU;
        return PRICE_LOW_CPU;
    }
    private double getGpuPrice() {
        if(gpuTier.contains("Ultra")) return PRICE_GPU_ULTRA;
        if(gpuTier.contains("High")) return PRICE_GPU_HIGH;
        return PRICE_GPU_MID;
    }
}