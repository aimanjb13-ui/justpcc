import java.io.*;
import java.util.StringTokenizer; // Optional, or use split()

public class ExpensesApp {
    public static void main(String[] args) {
        
        // Use try-catch for IO Exceptions
        try {
            // 1. Setup Readers and Writers
            FileReader fr = new FileReader("Expenses.txt");
            BufferedReader br = new BufferedReader(fr);
            
            PrintWriter outFirstHalf = new PrintWriter(new FileWriter("FirstHalfExpenses.txt"));
            PrintWriter outTax = new PrintWriter(new FileWriter("TaxExpenses.txt"));

            // Write Headers for Output Files
            outFirstHalf.printf("%-30s %s%n", "Company Name", "Expenses");
            outTax.printf("%-30s %-20s %s%n", "Company Name", "Total Expenses", "Total Tax");

            String line;
            // Read file line by line
            while ((line = br.readLine()) != null) {
                // Split line by ';' delimiter
                // Data format: Name;ID;Jan;Feb...;Dec
                String[] data = line.split(";");
                
                String companyName = data[0];
                
                // Variables for calculations
                double firstHalfTotal = 0.0;
                double fullYearTotal = 0.0;

                // Loop through expenses (Indices 2 to 13 correspond to Jan-Dec)
                for (int i = 2; i <= 13; i++) {
                    double monthlyExp = Double.parseDouble(data[i]);
                    
                    // Add to full year total
                    fullYearTotal = fullYearTotal + monthlyExp;
                    
                    // Add to first half total (Indices 2 to 7 are Jan-Jun)
                    if (i <= 7) {
                        firstHalfTotal = firstHalfTotal + monthlyExp;
                    }
                }

                // 2. Write to FirstHalfExpenses.txt
                outFirstHalf.printf("%-30s RM%,.2f%n", companyName, firstHalfTotal);

                // 3. Calculate Tax
                double taxRate = 0.0;
                if (fullYearTotal < 50000) {
                    taxRate = 0.10; // 10%
                } else if (fullYearTotal <= 200000) {
                    taxRate = 0.20; // 20%
                } else {
                    taxRate = 0.30; // 30%
                }
                
                double taxAmount = fullYearTotal * taxRate;

                // 4. Write to TaxExpenses.txt
                outTax.printf("%-30s RM%,-18.2f RM%,.2f%n", companyName, fullYearTotal, taxAmount);
            }

            // Close all files
            br.close();
            outFirstHalf.close();
            outTax.close();
            
            System.out.println("Data processing complete. Check output files.");

        } catch (IOException e) {
            System.out.println("Error reading or writing file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number from file: " + e.getMessage());
        }
    }
}