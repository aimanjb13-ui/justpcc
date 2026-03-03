import java.io.*;
import java.util.StringTokenizer;

public class JomBeliSales {

    public static void main(String[] args) {
        // -----------------------------------------------------------------------
        // VARIABLE INITIALIZATION
        // -----------------------------------------------------------------------
        // Variable to accumulate total amount for "DeliveredOrders.txt"
        double totalDeliveredRevenue = 0.0;

        // Counters for "HighOrders.txt" summary
        int countHighDelivered = 0;
        int countHighCancelled = 0;
        int countHighPending = 0;

        // -----------------------------------------------------------------------
        // FILE OPERATIONS
        // -----------------------------------------------------------------------
        try {
            // 1. Create Reader for the input file "Orders.txt"
            BufferedReader reader = new BufferedReader(new FileReader("Orders.txt"));

            // 2. Create Writers for the two output files
            PrintWriter deliveredWriter = new PrintWriter(new FileWriter("DeliveredOrders.txt"));
            PrintWriter highWriter = new PrintWriter(new FileWriter("HighOrders.txt"));

            // -------------------------------------------------------------------
            // WRITE HEADERS (Matching Figure 2 and Figure 3)
            // -------------------------------------------------------------------
            
            // Header for DeliveredOrders.txt
            deliveredWriter.println("Delivered Orders Year 2024");
            // Note: Figure 2 shows "Unit Price (RM)" but contains Total Values. 
            // We use standard tab spacing (\t) to align columns.
            deliveredWriter.println("Order ID\tCustomer Name\tProduct Name\tQuantity\tTotal Price (RM)"); 
            deliveredWriter.println("==================================================================================");

            // Header for HighOrders.txt
            highWriter.println("Orders Exceeding RM500 (Year 2024)");
            highWriter.println("Order ID\tCustomer Name\tProduct Name\tAmount of Orders (RM)\tStatus");
            highWriter.println("==================================================================================");

            // -------------------------------------------------------------------
            // PROCESS FILE LINE BY LINE
            // -------------------------------------------------------------------
            String lineOfData;
            
            while ((lineOfData = reader.readLine()) != null) {
                // Task: Use StringTokenizer to split data by semicolon ";"
                StringTokenizer st = new StringTokenizer(lineOfData, ";");

                // Ensure the line has data before processing
                if (st.hasMoreTokens()) {
                    // Extract tokens in strict order as per Orders.txt format:
                    // ID; Name; Product; Quantity; UnitPrice; Status
                    String orderID = st.nextToken().trim();
                    String customerName = st.nextToken().trim();
                    String productName = st.nextToken().trim();
                    
                    // Parse numeric values
                    int quantity = Integer.parseInt(st.nextToken().trim());
                    double unitPrice = Double.parseDouble(st.nextToken().trim());
                    
                    String status = st.nextToken().trim();

                    // Calculate the total amount for this order [cite: 311]
                    // Formula: Amount = quantity * unit price
                    double totalAmount = quantity * unitPrice;

                    // -----------------------------------------------------------
                    // TASK A: Handle "DeliveredOrders.txt" [cite: 309]
                    // -----------------------------------------------------------
                    if (status.equalsIgnoreCase("Delivered")) {
                        // Write details to file
                        deliveredWriter.println(orderID + "\t" + customerName + "\t" + productName + "\t" + quantity + "\t" + totalAmount);
                        
                        // Add to the running total revenue
                        totalDeliveredRevenue += totalAmount;
                    }

                    // -----------------------------------------------------------
                    // TASK B: Handle "HighOrders.txt" [cite: 378]
                    // (Orders exceeding RM500)
                    // -----------------------------------------------------------
                    if (totalAmount > 500) {
                        // Write details to file
                        highWriter.println(orderID + "\t" + customerName + "\t" + productName + "\t" + totalAmount + "\t" + status);

                        // Increment counters based on status for the summary
                        if (status.equalsIgnoreCase("Delivered")) {
                            countHighDelivered++;
                        } else if (status.equalsIgnoreCase("Cancelled")) {
                            countHighCancelled++;
                        } else if (status.equalsIgnoreCase("Pending")) {
                            countHighPending++;
                        }
                    }
                }
            }

            // -------------------------------------------------------------------
            // WRITE FOOTERS (Summaries at the end of files)
            // -------------------------------------------------------------------

            // Footer for DeliveredOrders.txt [cite: 376]
            deliveredWriter.println("==================================================================================");
            deliveredWriter.println("Total Amount of Orders : " + totalDeliveredRevenue);

            // Footer for HighOrders.txt [cite: 442]
            highWriter.println("==================================================================================");
            highWriter.println("Status Summary for Orders Exceeding RM500");
            highWriter.println("Total number of Delivered Orders : " + countHighDelivered);
            highWriter.println("Total Number of Cancelled Orders :" + countHighCancelled);
            highWriter.println("Total Number of Pending Orders :" + countHighPending);

            // -------------------------------------------------------------------
            // CLOSE RESOURCES
            // -------------------------------------------------------------------
            reader.close();
            deliveredWriter.close();
            highWriter.close();

            System.out.println("Processing complete. Output files generated successfully.");

        } catch (FileNotFoundException e) {
            // Handle missing input file
            System.out.println("Error: File 'Orders.txt' not found.");
        } catch (IOException e) {
            // Handle IO errors (reading/writing)
            System.out.println("Error: An Input/Output error occurred - " + e.getMessage());
        } catch (NumberFormatException e) {
            // Handle errors if text file contains bad numbers
            System.out.println("Error: Data format incorrect. Could not parse number.");
        } catch (Exception e) {
            // Global exception handler
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}