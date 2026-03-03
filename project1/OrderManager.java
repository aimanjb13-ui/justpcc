import java.io.*;
import java.util.ArrayList;

public class OrderManager {

    private final String DB_FILE = "pc_orders.txt";
    
    // === RUBRIC REQUIREMENT: STANDARD ARRAY & ORDER LIMIT ===
    private final int MAX_CART_SIZE = 20; // Maximum 20 items per order
    private PC[] cart = new PC[MAX_CART_SIZE]; // Standard Primitive Array
    private int itemCount = 0; // Tracks how many items are currently in the array

    public boolean addToCart(PC pc) {
        // CHECK 1: Order Limit
        if (itemCount >= MAX_CART_SIZE) {
            System.out.println("Cart is full! Cannot add more items.");
            return false; // Return false to indicate failure
        }
        
        // CHECK 2: Add to first available slot
        cart[itemCount] = pc;
        itemCount++;
        return true;
    }

    public PC[] getCartItems() {
        // Return only the valid items (remove null slots)
        PC[] activeItems = new PC[itemCount];
        for (int i = 0; i < itemCount; i++) {
            activeItems[i] = cart[i];
        }
        return activeItems;
    }

    public double getCartTotal() {
        double total = 0;
        // Standard Array Loop
        for (int i = 0; i < itemCount; i++) {
            if (cart[i] != null) {
                total += cart[i].calculatePrice();
            }
        }
        return total;
    }
    
    // === ARRAY MANIPULATION (Rubric Requirement: Shifting Elements) ===
    public void removeFromCart(String orderID) {
        int removeIndex = -1;
        
        // Step 1: Find the item index
        for (int i = 0; i < itemCount; i++) {
            if (cart[i].getID().equals(orderID)) {
                removeIndex = i;
                break;
            }
        }
        
        // Step 2: Remove and Shift Array (Closing the gap)
        if (removeIndex != -1) {
            for (int i = removeIndex; i < itemCount - 1; i++) {
                cart[i] = cart[i + 1]; // Shift item to the left
            }
            cart[itemCount - 1] = null; // Clear the last slot
            itemCount--; // Decrease count
            System.out.println("Item removed and array shifted.");
        }
    }

    public void clearCart() {
        // Reset the array
        cart = new PC[MAX_CART_SIZE];
        itemCount = 0;
    }

    public void checkoutAll(String method, String customerName, double cashPaid, double change) {
        System.out.println("\n=== CHECKOUT STARTED ===");
        
        if (itemCount == 0) {
            System.out.println("DEBUG: Cart is empty! Nothing to save.");
            return;
        }
        
        // Save to database file
        try (PrintWriter pw = new PrintWriter(new FileWriter(DB_FILE, true))) {
            for (int i = 0; i < itemCount; i++) {
                PC pc = cart[i];
                pc.setStatus(PC.STATUS_PAID);
                pc.setPaymentMethod(method);
                pw.println(pc.getFileFormat());
            }
            System.out.println("DEBUG: Saved items to database.");
        } catch (IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
        
        clearCart();
    }
    
    // === FIXED: Matches the call from PCFrontend ===
    public void saveReceiptToFile(String customerName, String receiptContent) {
        try {
            // Create safe filename
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String safeName = customerName.replaceAll("[^a-zA-Z0-9]", "_");
            String filename = "receipt_" + safeName + "_" + timestamp + ".txt";
            
            // Write receipt to file
            try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
                pw.print(receiptContent);
                pw.println("\n\n(Saved via OrderManager)");
            }
            System.out.println("Receipt saved to: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save receipt: " + e.getMessage());
        }
    }
    
    public ArrayList<String[]> loadAllHistory() {
        ArrayList<String[]> history = new ArrayList<>();
        File f = new File(DB_FILE);
        if (!f.exists()) return history;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                if (parts.length >= 6) {
                    history.add(new String[]{
                        parts[0].replace("ID:", ""),
                        parts[1].replace("Name:", ""),
                        parts[2].replace("Type:", ""),
                        parts[3].replace("Price:$", ""),
                        parts[4].replace("Status:", ""),
                        parts[5].replace("Method:", "")
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    public String trackOrder(String searchID) {
        for (String[] row : loadAllHistory()) {
            if (row[0].equalsIgnoreCase(searchID)) {
                return "Order Found!\nStatus: " + row[4] + "\nTotal: RM" + row[3];
            }
        }
        return "Order ID [" + searchID + "] not found.";
    }
    
    public void checkFileStatus() {
       File f = new File(DB_FILE);
       if (!f.exists()) {
           try { f.createNewFile(); } catch (IOException e) {}
       }
    }
}