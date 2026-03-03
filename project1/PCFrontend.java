import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
// ... existing variables ...

// === NEW RUBRIC REQUIREMENTS ===

public class PCFrontend {
    private JPanel menuPanel;
    private JLabel totalLabel;
    private JPanel orderListPanel;
    private JTextField receiptArea;
    private double cashGiven = 0.0;
    private double cashChange = 0.0;
    private String lastReceiptText = "";
    private ArrayList<CartItem> cartItems = new ArrayList<>();
    private OrderManager backend;
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTextField nameField;
    private JTextArea receiptTextArea; // Change from JTextField receiptArea;
    private String currentPaymentMethod = "CARD"; // Default
    private JCheckBox warrantyCheck;           // Requirement: JCheckBox
    private JRadioButton standardShip, expressShip; // Requirement: JRadioButton
    private JComboBox<String> filterBox;       // Requirement: JComboBox
    private double shippingCost = 0.0;
    private double warrantyCost = 0.0;
    // Navigation
    private JPanel navbar;
    private JButton navBackBtn;
    private JLabel navTitle;

    // Red-Black Theme Colors
    private final Color COLOR_BG = new Color(15, 15, 20);
    private final Color COLOR_ACCENT = new Color(220, 0, 0);
    private final Color COLOR_SEC = new Color(180, 0, 0);
    private final Color COLOR_CARD = new Color(25, 25, 30, 230);
    private final Color COLOR_NAV = new Color(25, 0, 0);
    private final Font NAV_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Color PAPER_WHITE = new Color(240, 240, 240);

    // Background Images
    private final String BG_IMAGE_HOME = "banner.jpg";
    private final String BG_IMAGE_COMMON = "banners.jpg";

    // Payment Radios
    private JRadioButton cardRadio, cashRadio;

    // Image Cache
    private Map<String, ImageIcon> imageCache = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PCFrontend::new);
    }
    
    class CartItem {
    PC pc;
    int quantity;
    
    public CartItem(PC pc) {
        this.pc = pc;
        this.quantity = 1;
    }
    
    public double getTotalPrice() {
        return pc.calculatePrice() * quantity;
    }
    
    public String getName() {
        return pc.getType();
    }
    
    public String getPrice() {
        return String.format("RM%.2f", pc.calculatePrice());
    }
}
    
    public PCFrontend() {
        backend = new OrderManager();
        preloadImages();
        initializeGUI();
    }

    private void preloadImages() {
        String[] paths = {
            "images/starter.jpg", "images/streamer.jpg", "images/pro.jpg",
            "images/i9.jpg", "images/ryzen.jpg", "images/4090.jpg",
            "images/3060.jpg", "images/ram.jpg"
        };
        for (String path : paths) {
            File f = new File(path);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(path);
                Image img = icon.getImage().getScaledInstance(140, 120, Image.SCALE_SMOOTH);
                imageCache.put(path, new ImageIcon(img));
            } else {
                imageCache.put(path, null);
            }
        }
    }

    private void initializeGUI() {
    frame = new JFrame("JUS SYSTEM INTEGRATORS");
    frame.setSize(1200, 850);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setLayout(new BorderLayout());
    
    
    // createNavbar();
    // frame.add(navbar, BorderLayout.NORTH); // 
    
    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);
    mainPanel.add(createHomePanel(), "Home");
    mainPanel.add(createFormPanel(), "Form");
    mainPanel.add(createPaymentPanel(), "Payment");
    mainPanel.add(createReceiptPanel(), "Receipt");
    
    frame.add(mainPanel, BorderLayout.CENTER);
    frame.setVisible(true);
    navigateTo("Home");
}

    private void createNavbar() {
        navbar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(40, 0, 0),
                    getWidth(), 0, new Color(20, 0, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        navbar.setPreferredSize(new Dimension(1000, 60));
        navbar.setOpaque(false);

        navBackBtn = new JButton("< BACK");
        navBackBtn.setForeground(Color.WHITE);
        navBackBtn.setBackground(new Color(60, 60, 70));
        navBackBtn.setFocusPainted(false);
        navBackBtn.setBorderPainted(false);
        navBackBtn.addActionListener(e -> navigateTo("Home"));

        navTitle = new JLabel("JUSPC SYSTEM INTEGRATORS", SwingConstants.CENTER);
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        navTitle.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(navBackBtn);

        navbar.add(leftPanel, BorderLayout.WEST);
        navbar.add(navTitle, BorderLayout.CENTER);
    }

private JButton createImageHomeButton() {
    JButton homeBtn = new JButton();
    homeBtn.setFocusPainted(false);
    homeBtn.setBorderPainted(false);
    homeBtn.setContentAreaFilled(false);
    homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    try {
        // Load your image - change "home_icon.png" to your image filename
        ImageIcon originalIcon = new ImageIcon("home_icon.png");
        // Scale to LARGER size - changed from 40x40 to 60x60
        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        homeBtn.setIcon(new ImageIcon(scaledImage));
    } catch (Exception e) {
        // Fallback text if image not found
        homeBtn.setText("🏠 HOME");
        homeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Larger font
        homeBtn.setBackground(new Color(70, 70, 80));
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setPreferredSize(new Dimension(120, 60)); // Larger text button
    }
    
    homeBtn.setToolTipText("Return to Home");
    
    // Optional: Add hover effect for better visibility
    homeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            homeBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            homeBtn.setBorder(null);
        }
    });
    
    homeBtn.addActionListener(e -> navigateTo("Home"));
    
    // Set explicit size for the button
    homeBtn.setPreferredSize(new Dimension(70, 70)); // Larger overall button
    
    return homeBtn;
}
    
private JPanel createHomePanel() {
    // Main panel with background
    BackgroundPanel mainPanel = new BackgroundPanel("background.jpg");
    mainPanel.setLayout(new BorderLayout());

    // Top Section: Title and Description
    JPanel topPanel = new JPanel(new GridBagLayout());
    topPanel.setOpaque(false);
    topPanel.setBorder(new EmptyBorder(80, 20, 40, 20)); // Increased top padding

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(5, 10, 20, 10);
    gbc.anchor = GridBagConstraints.CENTER;

    // Title: JUSPC - PURPLE DIGITIZED FONT
    JLabel titleLabel = new JLabel("JUSPC", SwingConstants.CENTER);
    
    // Try digital/cyber fonts first
    String[] digitalFonts = {"Orbitron", "Agency FB", "Eurostile", "Bank Gothic", "Courier New"};
    Font digitalFont = null;
    
    for (String fontName : digitalFonts) {
        Font testFont = new Font(fontName, Font.BOLD, 72);
        if (testFont.getFamily().equals(fontName)) {
            digitalFont = testFont;
            break;
        }
    }
    
    if (digitalFont == null) {
        digitalFont = new Font("Courier New", Font.BOLD, 72); // Monospace = digital look
    }
    
    titleLabel.setFont(digitalFont);
    titleLabel.setForeground(new Color(138, 43, 226)); // Purple color
    
    // Add digital glow effect
    titleLabel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(138, 43, 226, 100), 3), // Purple glow
        BorderFactory.createEmptyBorder(10, 20, 10, 20)
    ));

    // Description
    JLabel descLabel = new JLabel(
        "<html><div style='text-align: center; width: 600px;'>" +
        "Build your dream PC with precision and performance.<br>" +
        "Choose from curated pre-built systems or customize every component." +
        "</div></html>",
        SwingConstants.CENTER
    );
    descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    descLabel.setForeground(new Color(220, 220, 220));
    descLabel.setOpaque(false);

    // Add to top panel
    topPanel.add(titleLabel, gbc);
    gbc.gridy = 1;
    gbc.insets = new Insets(20, 80, 40, 80);
    topPanel.add(descLabel, gbc);

    // Center Section: Button pillars
    JPanel centerPanel = new JPanel(new GridLayout(1, 3, 20, 0));
    centerPanel.setOpaque(false);
    centerPanel.setBorder(new EmptyBorder(10, 50, 10, 50));
    
    JPanel centerWrapper = new JPanel(new BorderLayout());
    centerWrapper.setOpaque(false);
    centerWrapper.add(centerPanel, BorderLayout.CENTER);


    
        // Create buttons with animation
    JPanel startPillar = createImageButtonPanel(
        "start_bg.jpg", 
        "START<br>BUILDING", 
        Color.WHITE, 
        e -> navigateTo("Form")
    );
    
    JPanel trackPillar = createImageButtonPanel(
        "track_bg.jpg", 
        "TRACK<br>ORDER", 
        Color.WHITE, 
        e -> showTrackOrderDialog()
    );
    
    JPanel historyPillar = createImageButtonPanel(
        "history_bg.jpg", 
        "ORDER<br>HISTORY", 
        Color.WHITE, 
        e -> navigateTo("Receipt")
    );
    
    centerPanel.add(startPillar);
    centerPanel.add(trackPillar);
    centerPanel.add(historyPillar);

    // Bottom Section: Footer
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setOpaque(false);
    bottomPanel.setBorder(new EmptyBorder(20, 30, 30, 30));

    JLabel footerLabel = new JLabel(
        "PROFESSIONAL PC BUILDING | CUSTOM CONFIGURATIONS | NEXT-DAY BUILD & TEST",
        SwingConstants.CENTER
    );
    footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    footerLabel.setForeground(new Color(180, 180, 180));
    bottomPanel.add(footerLabel, BorderLayout.CENTER);

    // Assemble WITHOUT navbar (no "HOME" at top)
    mainPanel.add(topPanel, BorderLayout.NORTH);
    mainPanel.add(centerWrapper, BorderLayout.CENTER);
    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    return mainPanel;
}

private JPanel createImageButtonPanel(String imagePath, String text, Color textColor, 
                                      java.awt.event.ActionListener action) {
    // Create the panel
    JPanel panel = new JPanel(new BorderLayout()) {
        private Image bgImage;
        private Image scaledImage;
        
        {
            // Load background image
            java.io.File imgFile = new java.io.File(imagePath);
            if (imgFile.exists()) {
                try {
                    ImageIcon icon = new ImageIcon(imagePath);
                    bgImage = icon.getImage();
                } catch (Exception e) {
                    System.err.println("Could not load image: " + imagePath);
                }
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            
            // Enable high-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw background image
            if (bgImage != null) {
                // Scale if needed
                if (scaledImage == null || 
                    scaledImage.getWidth(null) != getWidth() || 
                    scaledImage.getHeight(null) != getHeight()) {
                    
                    scaledImage = bgImage.getScaledInstance(
                        getWidth(), 
                        getHeight(), 
                        Image.SCALE_SMOOTH
                    );
                }
                g2d.drawImage(scaledImage, 0, 0, this);
            } else {
                // Fallback color
                g2d.setColor(new Color(50, 50, 60, 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            
            // Dark overlay for text readability
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    
    // Set panel properties
    panel.setBorder(BorderFactory.createEmptyBorder(80, 20, 80, 20));
    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    panel.setPreferredSize(new Dimension(300, 400));
    
    // Add text
    JLabel textLabel = new JLabel("<html><center>" + text + "</center></html>", SwingConstants.CENTER);
    textLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
    textLabel.setForeground(textColor);
    panel.add(textLabel, BorderLayout.CENTER);
    
    // Add mouse listener for animation
    panel.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            // Optional: Add instant feedback
            panel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
            panel.repaint();
        }
        
        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            // Remove border feedback
            panel.setBorder(BorderFactory.createEmptyBorder(80, 20, 80, 20));
            panel.repaint();
            
            // Start the press animation
            animateButtonPress(panel, action);
        }
        
        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            // Remove border if mouse leaves while pressed
            panel.setBorder(BorderFactory.createEmptyBorder(80, 20, 80, 20));
            panel.repaint();
        }
    });
    
    return panel;
}

// Animation method
private void animateButtonPress(JPanel button, java.awt.event.ActionListener action) {
    // Store original size
    final int originalWidth = button.getWidth();
    final int originalHeight = button.getHeight();
    final int originalX = button.getX();
    final int originalY = button.getY();
    
    // Create animation timer
    javax.swing.Timer timer = new javax.swing.Timer(15, new java.awt.event.ActionListener() {
        private int frame = 0;
        private final int totalFrames = 10;
        
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            frame++;
            
            // Calculate scale (press down then bounce back)
            double scale;
            if (frame <= 5) {
                // Scale down to 90%
                scale = 1.0 - (0.1 * (frame / 5.0));
            } else {
                // Scale back up
                int bounceFrame = frame - 5;
                scale = 0.9 + (0.1 * (bounceFrame / 5.0));
            }
            
            // Apply scale
            int newWidth = (int)(originalWidth * scale);
            int newHeight = (int)(originalHeight * scale);
            int newX = originalX + (originalWidth - newWidth) / 2;
            int newY = originalY + (originalHeight - newHeight) / 2;
            
            button.setBounds(newX, newY, newWidth, newHeight);
            button.revalidate();
            button.repaint();
            
            // End animation
            if (frame >= totalFrames) {
                ((javax.swing.Timer) e.getSource()).stop();
                
                // Reset to original
                button.setBounds(originalX, originalY, originalWidth, originalHeight);
                button.revalidate();
                button.repaint();
                
                // Execute action
                if (action != null) {
                    action.actionPerformed(
                        new java.awt.event.ActionEvent(button, 
                            java.awt.event.ActionEvent.ACTION_PERFORMED, 
                            "click")
                    );
                }
            }
        }
    });
    
    timer.start();
}


// Helper method to fade an image
private Image makeImageFaded(Image image, float opacity) {
    // Create a buffered image with transparency
    BufferedImage buffered = new BufferedImage(
        image.getWidth(null), 
        image.getHeight(null), 
        BufferedImage.TYPE_INT_ARGB
    );
    
    Graphics2D g2d = buffered.createGraphics();
    
    // Apply opacity
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    g2d.drawImage(image, 0, 0, null);
    g2d.dispose();
    
    return buffered;
}
    
    // Track Order Dialog implementation
    private void showTrackOrderDialog() {
        String orderId = JOptionPane.showInputDialog(
            frame, 
            "Enter your Order ID:", 
            "Track Order", 
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (orderId != null && !orderId.trim().isEmpty()) {
            String result = backend.trackOrder(orderId.trim());
            JOptionPane.showMessageDialog(
                frame, 
                result, 
                "Order Status", 
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
        private JPanel createPillarPanel(String title, String description, String imagePath, Color accentColor) {
    JPanel pillar = new JPanel(new BorderLayout()) {
        private Image bgImage;
        
        {
            // Load background image
            try {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    // Load and scale image
                    Image original = new ImageIcon(imagePath).getImage();
                    // Scale to fit panel size (slightly larger than needed for better quality)
                    bgImage = original.getScaledInstance(350, 350, Image.SCALE_SMOOTH);
                }
            } catch (Exception e) {
                System.err.println("Image not found: " + imagePath);
                bgImage = null;
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Draw very dark base
            g2d.setColor(new Color(10, 10, 15));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw faded background image if available
            if (bgImage != null) {
                // Set to 15% opacity for subtle fade
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                
                // Center the image
                int x = (getWidth() - bgImage.getWidth(null)) / 2;
                int y = (getHeight() - bgImage.getHeight(null)) / 2;
                g2d.drawImage(bgImage, x, y, this);
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
            
            // Draw dark overlay for better text readability
            g2d.setColor(new Color(0, 0, 0, 160)); // 160/255 = ~63% opacity
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw accent border
            g2d.setColor(accentColor);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
            
            // Add subtle inner highlight at top
            g2d.setPaint(new GradientPaint(
                0, 0, new Color(255, 255, 255, 20),
                0, 30, new Color(255, 255, 255, 0)
            ));
            g2d.fillRect(2, 2, getWidth() - 5, 30);
        }
    };
    
    pillar.setPreferredSize(new Dimension(320, 420));
    pillar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    pillar.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
    
    // Create content panel with vertical layout
    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setOpaque(false);
    
    // Title only (no icon)
    JLabel titleLabel = new JLabel("<html><center>" + title + "</center></html>", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setAlignmentX(0.5f);
    
    // Description
    JLabel descLabel = new JLabel("<html><center style='padding:0 25px; line-height:1.4;'>" + description + "</center></html>", SwingConstants.CENTER);
    descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    descLabel.setForeground(new Color(220, 220, 220));
    descLabel.setAlignmentX(0.5f);
    
    // Add hover effect
    pillar.addMouseListener(new java.awt.event.MouseAdapter() {
        private Border originalBorder = pillar.getBorder();
        
        public void mouseEntered(java.awt.event.MouseEvent e) {
            pillar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor.brighter(), 3),
                BorderFactory.createEmptyBorder(37, 17, 37, 17)
            ));
            pillar.repaint();
        }
        
        public void mouseExited(java.awt.event.MouseEvent e) {
            pillar.setBorder(originalBorder);
            pillar.repaint();
        }
    });
    
    // Assemble content with proper spacing
    content.add(Box.createVerticalGlue());
    content.add(titleLabel);
    content.add(Box.createVerticalStrut(20));
    content.add(descLabel);
    content.add(Box.createVerticalGlue());
    
    pillar.add(content, BorderLayout.CENTER);
    
    return pillar;
}
    
  
    // Helper method to create faded images
    private Image createFadedImage(Image original, float opacity) {
        int width = original.getWidth(null);
        int height = original.getHeight(null);
        
        if (width <= 0 || height <= 0) {
            return original; 
        }
        
        // Create buffered image
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffered.createGraphics();
        
        // Apply opacity
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        
        return buffered.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
    }
    
    // Helper method to get icons
    private String getIconForTitle(String title) {
        switch (title) {
            case "START BUILDING":
                return "🛠️";
            case "TRACK ORDER":
                return "🔍";
            case "ORDER HISTORY":
                return "📋";
            default:
                return "⭐";
        }
    }

    private JPanel createFeatureCard(String title, String description, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 30, 40, 200));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accentColor, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(350, 160));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(0.5f);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setAlignmentX(0.5f);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

   private JPanel createFormPanel() {
    BackgroundPanel root = new BackgroundPanel("form_bg.jpg");
    root.setLayout(new BorderLayout(15, 15));
    root.setBorder(new EmptyBorder(15, 15, 15, 15));

    // Top Navigation
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    topPanel.setBorder(new EmptyBorder(10, 25, 10, 15));

    JButton homeBtn = createImageHomeButton();
    topPanel.add(homeBtn, BorderLayout.WEST);

    // Center Buttons
    JPanel centerBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
    centerBtnPanel.setOpaque(false);
    JButton prebuiltBtn = new ModernButton("PREBUILT PCS", COLOR_ACCENT, new Color(180, 0, 0));
    JButton customBtn = new ModernButton("CUSTOM CONFIG", COLOR_SEC, new Color(150, 0, 0));
    centerBtnPanel.add(prebuiltBtn);
    centerBtnPanel.add(customBtn);
    
    // === NEW: JComboBox for Filtering (RUBRIC REQUIREMENT) ===
    String[] filters = {"Show All Items", "Budget (< RM1500)", "High-End (> RM1500)"};
    filterBox = new JComboBox<>(filters);
    filterBox.setFont(new Font("Segoe UI", Font.BOLD, 12));
    filterBox.setPreferredSize(new Dimension(150, 35));
    filterBox.addActionListener(e -> {
        // Reload menu based on selection
        loadPrebuiltMenu(); 
    });
    
    JPanel rightFilterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightFilterPanel.setOpaque(false);
    rightFilterPanel.add(new JLabel("<html><font color='white'>Filter: </font></html>"));
    rightFilterPanel.add(filterBox);
    topPanel.add(rightFilterPanel, BorderLayout.EAST);
    // ========================================================

    topPanel.add(centerBtnPanel, BorderLayout.CENTER);
    root.add(topPanel, BorderLayout.NORTH);

    // Main Content
    JPanel mainContent = new JPanel(new BorderLayout(15, 15));
    mainContent.setOpaque(false);

    // 1. SELECT ITEM SECTION
    JPanel selectItemContainer = new JPanel(new BorderLayout());
    selectItemContainer.setOpaque(false);
    selectItemContainer.setBorder(new EmptyBorder(0, 0, 15, 0));
    
    menuPanel = new JPanel(new GridLayout(0, 3, 15, 15));
    menuPanel.setOpaque(false);
    
    JPanel menuWrapper = new JPanel(new BorderLayout());
    menuWrapper.setOpaque(false);
    menuWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
    menuWrapper.add(menuPanel, BorderLayout.CENTER);
    
    JScrollPane menuScroll = new JScrollPane(menuWrapper);
    menuScroll.setBorder(new TitledBorder(new LineBorder(COLOR_ACCENT, 2), "SELECT ITEM", 0, 0, new Font("Segoe UI", Font.BOLD, 20), Color.WHITE));
    menuScroll.setOpaque(false);
    menuScroll.getViewport().setOpaque(false);
    menuScroll.setPreferredSize(new Dimension(0, 450));
    
    selectItemContainer.add(menuScroll, BorderLayout.CENTER);
    mainContent.add(selectItemContainer, BorderLayout.CENTER);

    // 2. BOTTOM SECTION
    JPanel bottomSection = new JPanel(new GridLayout(1, 2, 15, 0));
    bottomSection.setOpaque(false);
    bottomSection.setPreferredSize(new Dimension(0, 320));
    
    // 2A. CURRENT ORDER
    JPanel cartContainer = new JPanel(new BorderLayout());
    cartContainer.setOpaque(false);
    
    JPanel cartPanel = new JPanel(new BorderLayout());
    cartPanel.setBackground(new Color(35, 35, 45));
    cartPanel.setBorder(new TitledBorder(new LineBorder(COLOR_SEC, 2), "CURRENT ORDER", 0, 0, new Font("Segoe UI", Font.BOLD, 16), Color.WHITE));
    
    JPanel cartSplitPanel = new JPanel(new GridBagLayout());
    cartSplitPanel.setOpaque(true);
    cartSplitPanel.setBackground(new Color(35, 35, 45));
    GridBagConstraints gbc = new GridBagConstraints();
    
    // Left: List
    gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.7; gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH; gbc.insets = new Insets(5, 5, 5, 5);
    
    JPanel cartItemsPanel = new JPanel(new BorderLayout());
    cartItemsPanel.setOpaque(true);
    cartItemsPanel.setBackground(new Color(45, 45, 55));
    
    orderListPanel = new JPanel();
    orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
    orderListPanel.setOpaque(true);
    orderListPanel.setBackground(new Color(45, 45, 55));
    JScrollPane orderScroll = new JScrollPane(orderListPanel);
    orderScroll.getViewport().setOpaque(true);
    orderScroll.getViewport().setBackground(new Color(45, 45, 55));
    orderScroll.setBorder(null);
    
    cartItemsPanel.add(orderScroll, BorderLayout.CENTER);
    cartSplitPanel.add(cartItemsPanel, gbc);
    
    // Right: Info
    gbc.gridx = 1; gbc.weightx = 0.3; gbc.insets = new Insets(10, 10, 10, 10);
    
    JPanel customerPanel = new JPanel(new BorderLayout());
    customerPanel.setOpaque(true);
    customerPanel.setBackground(new Color(35, 35, 45));
    
    JPanel nameInputPanel = new JPanel(new BorderLayout(5, 5));
    nameInputPanel.setOpaque(true);
    nameInputPanel.setBackground(new Color(35, 35, 45));
    JLabel nameLbl = new JLabel("CUSTOMER NAME:");
    nameLbl.setForeground(Color.WHITE);
    nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
    nameField = new JTextField();
    nameField.setPreferredSize(new Dimension(0, 35));
    nameField.setBackground(new Color(60, 60, 70));
    nameField.setForeground(Color.WHITE);
    nameField.setCaretColor(Color.WHITE);
    nameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    nameInputPanel.add(nameLbl, BorderLayout.NORTH);
    nameInputPanel.add(nameField, BorderLayout.CENTER);
    
    // Total Panel
    JPanel totalPanel = new JPanel(new BorderLayout());
    totalPanel.setOpaque(true);
    totalPanel.setBackground(new Color(35, 35, 45));
    totalPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
    
    // === NEW: JCheckBox for Warranty (RUBRIC REQUIREMENT) ===
    warrantyCheck = new JCheckBox("Extended Warranty (+RM100)");
    warrantyCheck.setOpaque(false);
    warrantyCheck.setForeground(Color.WHITE);
    warrantyCheck.setFocusPainted(false);
    warrantyCheck.addActionListener(e -> {
        warrantyCost = warrantyCheck.isSelected() ? 100.0 : 0.0;
        updateUISelection();
    });
    totalPanel.add(warrantyCheck, BorderLayout.NORTH);
    // ========================================================
    
    totalLabel = new JLabel("RM0.00", SwingConstants.CENTER);
    totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    totalLabel.setForeground(COLOR_ACCENT);
    
    JLabel totalText = new JLabel("TOTAL:", SwingConstants.CENTER);
    totalText.setFont(new Font("Segoe UI", Font.BOLD, 16));
    totalText.setForeground(Color.WHITE);
    
    JPanel totalWrapper = new JPanel(new BorderLayout());
    totalWrapper.setOpaque(true);
    totalWrapper.setBackground(new Color(35, 35, 45));
    totalWrapper.add(totalText, BorderLayout.NORTH);
    totalWrapper.add(totalLabel, BorderLayout.CENTER);
    
    totalPanel.add(totalWrapper, BorderLayout.CENTER);
    
    // Buttons
    JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
    buttonsPanel.setOpaque(true);
    buttonsPanel.setBackground(new Color(35, 35, 45));
    buttonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
    
    JButton clearBtn = new ModernButton("CLEAR ALL", new Color(100, 0, 0), new Color(150, 0, 0));
    clearBtn.addActionListener(e -> {
        cartItems.clear();
        backend.clearCart();
        orderListPanel.removeAll();
        totalLabel.setText("RM0.00");
        receiptTextArea.setText("");
        currentPaymentMethod = "CARD";
        cashGiven = 0.0;
        cashChange = 0.0;
        
        // Reset new components
        warrantyCheck.setSelected(false);
        warrantyCost = 0.0;
        if(standardShip != null) standardShip.setSelected(true);
        shippingCost = 0.0;
        
        orderListPanel.revalidate();
        orderListPanel.repaint();
    });
    
    JButton placeBtn = new ModernButton("PLACE ORDER", COLOR_ACCENT, new Color(180, 0, 0));
    placeBtn.addActionListener(e -> {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter your name to proceed.");
        } else if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Your cart is currently empty.");
        } else {
            backend.clearCart();
            // Process Cart Items...
            for (CartItem cartItem : cartItems) {
                PC originalPC = cartItem.pc;
                for (int i = 0; i < cartItem.quantity; i++) {
                    if (originalPC instanceof PrebuiltPC) {
                        PrebuiltPC prebuilt = (PrebuiltPC) originalPC;
                        String type = prebuilt.getType();
                        String modelName = "Unknown";
                        if (type.startsWith("Prebuilt (")) {
                            modelName = type.replace("Prebuilt (", "").replace(")", "");
                        }
                        PrebuiltPC newPC = new PrebuiltPC(name, prebuilt.getCpu(), prebuilt.getRam(), modelName, prebuilt.calculatePrice());
                        backend.addToCart(newPC);
                    }
                }
            }
            // Add Warranty as a line item in backend if selected
            if(warrantyCheck.isSelected()) {
                backend.addToCart(new PrebuiltPC(name, "N/A", 0, "Warranty Service", 100.0));
            }
            
            receiptTextArea.setText(generateReceiptPreview());
            navigateTo("Payment");
        }
    });
    
    buttonsPanel.add(clearBtn);
    buttonsPanel.add(placeBtn);
    
    customerPanel.add(nameInputPanel, BorderLayout.NORTH);
    customerPanel.add(totalPanel, BorderLayout.CENTER);
    customerPanel.add(buttonsPanel, BorderLayout.SOUTH);
    cartSplitPanel.add(customerPanel, gbc);
    cartPanel.add(cartSplitPanel, BorderLayout.CENTER);
    cartContainer.add(cartPanel, BorderLayout.CENTER);

    // 2B. RECEIPT PREVIEW
    receiptTextArea = new JTextArea();
    receiptTextArea.setEditable(false);
    receiptTextArea.setFont(new Font("Courier New", Font.PLAIN, 12));
    receiptTextArea.setBackground(new Color(255, 255, 255));
    receiptTextArea.setForeground(Color.BLACK);
    receiptTextArea.setLineWrap(true);
    receiptTextArea.setWrapStyleWord(true);
    receiptTextArea.setMargin(new Insets(10, 10, 10, 10));
    JScrollPane receiptScroll = new JScrollPane(receiptTextArea);
    receiptScroll.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1), "ORDER DETAILS", 0, 0, new Font("Segoe UI", Font.BOLD, 16), Color.WHITE));
    receiptScroll.setOpaque(true);
    receiptScroll.getViewport().setOpaque(true);
    receiptScroll.getViewport().setBackground(new Color(255, 255, 255));
    receiptScroll.setPreferredSize(new Dimension(0, 200));

    bottomSection.add(cartContainer);
    bottomSection.add(receiptScroll);
    mainContent.add(bottomSection, BorderLayout.SOUTH);
    root.add(mainContent, BorderLayout.CENTER);

    prebuiltBtn.addActionListener(e -> loadPrebuiltMenu());
    customBtn.addActionListener(e -> loadCustomMenu());
    loadPrebuiltMenu();

    return root;
}


private void showPaymentSuccessWithReceipt(String paymentMethod, String customerName, double change) {
    // Create receipt filename
    String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
    String receiptFileName = "receipt_" + customerName + "_" + timestamp + ".txt";
    
    JDialog successDialog = new JDialog(frame, "Payment Successful", true);
    successDialog.setSize(450, 350);
    successDialog.setLocationRelativeTo(frame);
    successDialog.setLayout(new BorderLayout());
    successDialog.getContentPane().setBackground(new Color(240, 255, 240));
    
    // Success icon
    JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    iconPanel.setBackground(new Color(240, 255, 240));
    JLabel iconLabel = new JLabel("✓");
    iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
    iconLabel.setForeground(new Color(0, 150, 0));
    iconPanel.add(iconLabel);
    
    // Success message
    JPanel messagePanel = new JPanel(new BorderLayout());
    messagePanel.setBackground(new Color(240, 255, 240));
    messagePanel.setBorder(new EmptyBorder(20, 30, 20, 30));
    
    JLabel successLabel = new JLabel("PAYMENT SUCCESSFUL!", SwingConstants.CENTER);
    successLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    successLabel.setForeground(new Color(0, 100, 0));
    
    JLabel detailsLabel = new JLabel("<html><div style='text-align: center;'>" +
        "Your order has been confirmed!<br><br>" +
        "<b>Payment Method:</b> " + paymentMethod + "<br>" +
        (change > 0 ? "<b>Change:</b> RM" + String.format("%.2f", change) + "<br>" : "") +
        "<br><b>Receipt saved as:</b><br>" +
        "<code>" + receiptFileName + "</code><br><br>" +
        "Thank you for your purchase!" +
        "</div></html>", SwingConstants.CENTER);
    detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    detailsLabel.setForeground(Color.BLACK);
    
    messagePanel.add(successLabel, BorderLayout.NORTH);
    messagePanel.add(detailsLabel, BorderLayout.CENTER);
    
    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
    buttonPanel.setBackground(new Color(240, 255, 240));
    buttonPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
    
    JButton viewBtn = new JButton("VIEW RECEIPT");
    viewBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    viewBtn.setBackground(new Color(0, 150, 0));
    viewBtn.setForeground(Color.WHITE);
    viewBtn.setPreferredSize(new Dimension(150, 40));
    viewBtn.setFocusPainted(false);
    viewBtn.setBorderPainted(false);
    viewBtn.addActionListener(ev -> {
        successDialog.dispose();
        navigateTo("Receipt");
    });
    
    JButton openBtn = new JButton("OPEN RECEIPT FILE");
    openBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    openBtn.setBackground(new Color(70, 130, 180));
    openBtn.setForeground(Color.WHITE);
    openBtn.setPreferredSize(new Dimension(180, 40));
    openBtn.setFocusPainted(false);
    openBtn.setBorderPainted(false);
    openBtn.addActionListener(ev -> {
        try {
            // Try to open the receipt file with default text editor
            File receiptFile = new File(receiptFileName);
            if (receiptFile.exists()) {
                java.awt.Desktop.getDesktop().open(receiptFile);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(successDialog, 
                "Could not open receipt file automatically.\n" +
                "Look for: " + receiptFileName + " in the program folder.",
                "File Info", JOptionPane.INFORMATION_MESSAGE);
        }
    });
    
    buttonPanel.add(viewBtn);
    buttonPanel.add(openBtn);
    
    // Assemble dialog
    successDialog.add(iconPanel, BorderLayout.NORTH);
    successDialog.add(messagePanel, BorderLayout.CENTER);
    successDialog.add(buttonPanel, BorderLayout.SOUTH);
    
    successDialog.setVisible(true);
}

   private JPanel createPaymentPanel() {
    BackgroundPanel root = new BackgroundPanel(BG_IMAGE_COMMON);
    root.setLayout(new BorderLayout());
    
    // Top panel with home button
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    topPanel.setBorder(new EmptyBorder(10, 25, 0, 15));
    
    JButton homeBtn = createImageHomeButton();
    topPanel.add(homeBtn, BorderLayout.WEST);
    
    JLabel title = new JLabel("SELECT PAYMENT METHOD", SwingConstants.CENTER);
    title.setFont(new Font("Courier New", Font.BOLD, 32));
    title.setForeground(new Color(0, 255, 255));
    title.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0, 200, 255, 100), 1),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)
    ));
    
    topPanel.add(title, BorderLayout.CENTER);
    root.add(topPanel, BorderLayout.NORTH);
    
    // CENTER SECTION
    JPanel centerPanel = new JPanel(new GridBagLayout());
    centerPanel.setOpaque(false);
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 100, 20, 100);
    
    JPanel contentStack = new JPanel(new GridLayout(2, 1, 0, 20)); // Stack: Payment Buttons, then Shipping
    contentStack.setOpaque(false);
    contentStack.setPreferredSize(new Dimension(600, 400));
    
    // 1. Payment Buttons Group
    JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 20));
    buttonsPanel.setOpaque(false);
    
    // Card Button
    JPanel cardPanel = createPaymentButton("CARD PAYMENT", new Color(0, 255, 255), e -> processPayment("CARD"));
    // Cash Button
    JPanel cashPanel = createPaymentButton("CASH PAYMENT", new Color(0, 255, 150), e -> processPayment("CASH"));
    
    buttonsPanel.add(cardPanel);
    buttonsPanel.add(cashPanel);
    
    // 2. Shipping Options (RUBRIC REQUIREMENT: JRadioButtons)
    JPanel shippingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    shippingPanel.setOpaque(false);
    shippingPanel.setBorder(new TitledBorder(new LineBorder(Color.WHITE, 1), "MEMBERSHIPS", 0, 0, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));
    
    standardShip = new JRadioButton("member (Freegift)");
    expressShip = new JRadioButton("non member (no speciality)");
    
    // Styling
    styleRadioButton(standardShip);
    styleRadioButton(expressShip);
    standardShip.setSelected(true); // Default
    
    // Grouping
    ButtonGroup shipGroup = new ButtonGroup();
    shipGroup.add(standardShip);
    shipGroup.add(expressShip);
    
    // Logic
    ActionListener shipListener = e -> {
        shippingCost = expressShip.isSelected() ? 50.0 : 0.0;
        // Also update backend if needed, for now just update visual total logic
        // We will pass this cost during payment
        System.out.println("Shipping cost updated: " + shippingCost);
    };
    standardShip.addActionListener(shipListener);
    expressShip.addActionListener(shipListener);
    
    shippingPanel.add(standardShip);
    shippingPanel.add(expressShip);
    
    // Assemble
    contentStack.add(buttonsPanel);
    contentStack.add(shippingPanel);
    
    centerPanel.add(contentStack, gbc);
    root.add(centerPanel, BorderLayout.CENTER);
    return root;
}

// Helper for Radio Button Styling
private void styleRadioButton(JRadioButton rb) {
    rb.setOpaque(false);
    rb.setForeground(Color.WHITE);
    rb.setFont(new Font("Segoe UI", Font.BOLD, 16));
    rb.setFocusPainted(false);
}

// Helper for Payment Button creation (refactored to save space)
private JPanel createPaymentButton(String text, Color color, java.awt.event.ActionListener listener) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(0, 0, 0, 200));
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color, 2),
        BorderFactory.createEmptyBorder(25, 25, 25, 25)
    ));
    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    label.setFont(new Font("Segoe UI", Font.BOLD, 26));
    label.setForeground(color);
    panel.add(label, BorderLayout.CENTER);
    
    panel.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) { listener.actionPerformed(null); }
        public void mouseEntered(MouseEvent e) { 
            panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color.brighter(), 3), BorderFactory.createEmptyBorder(24, 24, 24, 24)));
        }
        public void mouseExited(MouseEvent e) { 
            panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color, 2), BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        }
    });
    return panel;
}
// Helper method to load images with fallback
private ImageIcon loadImageWithFallback(String path, int width, int height) {
    try {
        File file = new File(path);
        if (file.exists()) {
            ImageIcon original = new ImageIcon(path);
            Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } else {
            // Create fallback
            System.err.println("Image not found: " + path);
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            
            if (path.contains("card")) {
                g2d.setColor(new Color(200, 220, 255)); // Light blue for card
                g2d.fillRect(0, 0, width, height);
                g2d.setColor(Color.BLUE);
                g2d.drawString("CARD", width/2-20, height/2);
            } else {
                g2d.setColor(new Color(220, 255, 220)); // Light green for cash
                g2d.fillRect(0, 0, width, height);
                g2d.setColor(Color.GREEN.darker());
                g2d.drawString("CASH", width/2-20, height/2);
            }
            g2d.dispose();
            return new ImageIcon(img);
        }
    } catch (Exception e) {
        System.err.println("Error loading image: " + path);
        return createFallbackIcon(width, height);
    }
}

private ImageIcon createFallbackIcon(int width, int height) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = img.createGraphics();
    g2d.setColor(Color.LIGHT_GRAY);
    g2d.fillRect(0, 0, width, height);
    g2d.setColor(Color.DARK_GRAY);
    g2d.drawRect(2, 2, width-5, height-5);
    g2d.dispose();
    return new ImageIcon(img);
}

private void processPayment(String method) {
    String customerName = nameField.getText().trim();
    
    if (customerName.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "Please enter customer name first!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    if (cartItems.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "Your cart is empty!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // RESET instance variables first
    this.cashGiven = 0.0;
    this.cashChange = 0.0;
    
    double total = getCartTotal();
    
    if (method.equals("CASH")) {
        Double inputAmount = showCashKeypad(total);
        if (inputAmount == null) return; // User cancelled
        
        if (inputAmount < total) {
            JOptionPane.showMessageDialog(frame, 
                String.format("Insufficient cash. Total: RM%.2f, Given: RM%.2f", total, inputAmount), 
                "Payment Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // STORE IN INSTANCE VARIABLES
        this.cashGiven = inputAmount;
        this.cashChange = this.cashGiven - total;
        
        System.out.println("DEBUG: Cash payment recorded - Given: " + this.cashGiven + 
                          ", Change: " + this.cashChange);
    }
    
    // Update current payment method
    this.currentPaymentMethod = method;
    
    // Process payment
    try {
        // Clear and rebuild backend cart
        backend.clearCart();
        
        // Add all items to backend
        for (CartItem cartItem : cartItems) {
            PC originalPC = cartItem.pc;
            
            for (int i = 0; i < cartItem.quantity; i++) {
                if (originalPC instanceof PrebuiltPC) {
                    PrebuiltPC prebuilt = (PrebuiltPC) originalPC;
                    String type = prebuilt.getType();
                    String modelName = type.startsWith("Prebuilt (") ? 
                        type.replace("Prebuilt (", "").replace(")", "") : "Unknown";
                    
                    PrebuiltPC newPC = new PrebuiltPC(
                        customerName, 
                        prebuilt.getCpu(), 
                        prebuilt.getRam(), 
                        modelName,
                        prebuilt.calculatePrice()
                    );
                    backend.addToCart(newPC);
                }
            }
        }
        
        // Checkout with all parameters
        backend.checkoutAll(method, customerName, this.cashGiven, this.cashChange);
        
        // Generate and save receipt - THIS WILL NOW USE THE UPDATED INSTANCE VARIABLES
        this.lastReceiptText = generateReceiptPreview();
        saveReceiptToFile(customerName);
        
        // Show success
        showPaymentSuccess(method, customerName, this.cashChange);
        
        // Reset application state
        resetApplicationState();
        
        // Navigate to receipt page
        navigateTo("Receipt");
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, 
            "Payment failed: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void resetApplicationState() {
    // Clear cart items
    cartItems.clear();
    
    // Clear backend cart
    backend.clearCart();
    
    // Clear UI
    orderListPanel.removeAll();
    totalLabel.setText("RM0.00");
    receiptTextArea.setText("");
    nameField.setText("");
    
    // Reset payment variables
    currentPaymentMethod = "CARD";
    cashGiven = 0.0;
    cashChange = 0.0;
    
    // Refresh UI
    orderListPanel.revalidate();
    orderListPanel.repaint();
    
    System.out.println("Application state reset");
}

private void saveReceiptToFile(String customerName) {
    try {
        // Create safe filename
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String safeName = customerName.replaceAll("[^a-zA-Z0-9]", "_");
        String filename = "receipt_" + safeName + "_" + timestamp + ".txt";
        
        // Write receipt to file
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
            pw.print(lastReceiptText);
            pw.println("\n--- END OF RECEIPT ---");
            pw.println("Generated by JUSPC System Integrators");
        }
        
        System.out.println("Receipt saved to: " + filename);
        
    } catch (java.io.IOException e) {
        System.err.println("Failed to save receipt: " + e.getMessage());
    }
}

// Add this new method to show payment success
private void showPaymentSuccess(String paymentMethod, String customerName, double change) {
    // Create dialog
    JDialog successDialog = new JDialog(frame, "Payment Successful", true);
    successDialog.setSize(400, 350);
    successDialog.setLocationRelativeTo(frame);
    successDialog.setLayout(new BorderLayout());
    successDialog.getContentPane().setBackground(new Color(240, 255, 240));
    
    // Success icon
    JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    iconPanel.setBackground(new Color(240, 255, 240));
    JLabel iconLabel = new JLabel("✓");
    iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
    iconLabel.setForeground(new Color(0, 150, 0));
    iconPanel.add(iconLabel);
    
    // Success message - UPDATED WITH CUSTOMER NAME
    JPanel messagePanel = new JPanel(new BorderLayout());
    messagePanel.setBackground(new Color(240, 255, 240));
    messagePanel.setBorder(new EmptyBorder(20, 30, 20, 30));
    
    JLabel successLabel = new JLabel("PAYMENT SUCCESSFUL!", SwingConstants.CENTER);
    successLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    successLabel.setForeground(new Color(0, 100, 0));
    
    JLabel detailsLabel = new JLabel("<html><div style='text-align: center;'>" +
        "Your order has been confirmed!<br><br>" +
        "<b>Customer:</b> " + customerName + "<br>" +
        "<b>Payment Method:</b> " + paymentMethod + "<br>" +
        (change > 0 ? "<b>Change:</b> RM" + String.format("%.2f", change) + "<br>" : "") +
        "<br>Thank you for your purchase!" +
        "</div></html>", SwingConstants.CENTER);
    detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    detailsLabel.setForeground(Color.BLACK);
    
    messagePanel.add(successLabel, BorderLayout.NORTH);
    messagePanel.add(detailsLabel, BorderLayout.CENTER);
    
    // Button
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBackground(new Color(240, 255, 240));
    JButton okButton = new JButton("VIEW RECEIPT");
    okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    okButton.setBackground(new Color(0, 150, 0));
    okButton.setForeground(Color.WHITE);
    okButton.setPreferredSize(new Dimension(150, 40));
    okButton.setFocusPainted(false);
    okButton.setBorderPainted(false);
    okButton.addActionListener(ev -> {
        successDialog.dispose();
        navigateTo("Receipt");
    });
    buttonPanel.add(okButton);
    
    // Assemble dialog
    successDialog.add(iconPanel, BorderLayout.NORTH);
    successDialog.add(messagePanel, BorderLayout.CENTER);
    successDialog.add(buttonPanel, BorderLayout.SOUTH);
    
    // Show dialog after a short delay
    Timer timer = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            successDialog.setVisible(true);
        }
    });
    timer.setRepeats(false);
    timer.start();
}

// Also update the showCashKeypad method to return to payment panel on cancel
private Double showCashKeypad(double total) {
    JDialog dialog = new JDialog(frame, "Cash Payment", true);
    dialog.setSize(320, 420);
    dialog.setLocationRelativeTo(frame);
    dialog.setLayout(new BorderLayout(10, 10));

    JTextField display = new JTextField();
    display.setEditable(false);
    display.setFont(new Font("Segoe UI", Font.BOLD, 22));
    display.setHorizontalAlignment(JTextField.RIGHT);
    display.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel totalLbl = new JLabel(String.format("TOTAL: RM%.2f", total), SwingConstants.CENTER);
    totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
    totalLbl.setForeground(Color.WHITE);

    JPanel keypad = new JPanel(new GridLayout(4, 3, 10, 10));
    keypad.setBorder(new EmptyBorder(10, 10, 10, 10));
    keypad.setBackground(COLOR_CARD);

    String[] keys = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "C", "0", "OK"};
    final Double[] result = {null};

    for (String key : keys) {
        JButton btn = new ModernButton(key, new Color(60, 60, 70), COLOR_ACCENT);
        btn.addActionListener(e -> {
            switch (key) {
                case "C":
                    display.setText("");
                    break;
                case "OK":
                    if (!display.getText().isEmpty()) {
                        try {
                            result[0] = Double.parseDouble(display.getText());
                            dialog.dispose();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, "Invalid amount!");
                        }
                    }
                    break;
                default:
                    display.setText(display.getText() + key);
            }
        });
        keypad.add(btn);
    }

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(COLOR_CARD);
    topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    topPanel.add(display, BorderLayout.NORTH);
    topPanel.add(totalLbl, BorderLayout.SOUTH);

    dialog.add(topPanel, BorderLayout.NORTH);
    dialog.add(keypad, BorderLayout.CENTER);
    
    // Add cancel button at bottom
    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(e -> {
        dialog.dispose();
    });
    dialog.add(cancelBtn, BorderLayout.SOUTH);
    
    dialog.setVisible(true);

    return result[0];
}

    private JPanel createReceiptPanel() {
    BackgroundPanel root = new BackgroundPanel(BG_IMAGE_COMMON);
    root.setLayout(new BorderLayout(20, 20));
    
    // Add home button at top-left
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    topPanel.setBorder(new EmptyBorder(40, 25, 40, 15));
    
    JButton homeBtn = createImageHomeButton();
    topPanel.add(homeBtn, BorderLayout.WEST);
    
    // NEON RED TITLE
    JLabel title = new JLabel("ORDER RECEIPT & HISTORY", SwingConstants.CENTER);
    
    // Try to load cyber fonts for neon effect
    String[] neonFonts = {"Orbitron", "Agency FB", "Eurostile", "Bank Gothic", "Consolas", "Courier New"};
    Font neonFont = null;
    
    for (String fontName : neonFonts) {
        Font testFont = new Font(fontName, Font.BOLD, 36); // Larger font size (was 20)
        if (testFont.getFamily().equals(fontName)) {
            neonFont = testFont;
            break;
        }
    }
    
    if (neonFont == null) {
        neonFont = new Font("Courier New", Font.BOLD, 36); // Fallback to larger font
    }
    
    title.setFont(neonFont);
    
    // NEON RED COLOR with glow effect
    Color neonRed = new Color(255, 20, 20); // Bright red
    Color neonGlow = new Color(255, 50, 50, 100); // Glow effect
    
    title.setForeground(neonRed);
    
    // Create neon glow effect
    title.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(neonGlow, 3), // Outer glow
        BorderFactory.createEmptyBorder(10, 20, 10, 20) // Inner padding
    ));
    
    // Optional: Add shadow effect for more depth
    title.setFont(neonFont.deriveFont(Font.BOLD, 38)); // Slightly larger
    
    topPanel.add(title, BorderLayout.CENTER);
    root.add(topPanel, BorderLayout.NORTH);
    
    // Adjust main content padding to position it lower
    root.setBorder(new EmptyBorder(0, 50, 30, 50)); // Reduced top padding from 30 to 0

    JPanel receiptPaper = new JPanel(new BorderLayout());
    receiptPaper.setBackground(PAPER_WHITE);
    receiptPaper.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

    JTextArea finalReceiptArea = new JTextArea();
    finalReceiptArea.setEditable(false);
    finalReceiptArea.setFont(new Font("Courier New", Font.BOLD, 14));
    finalReceiptArea.setBackground(PAPER_WHITE);
    finalReceiptArea.setForeground(new Color(20, 20, 20));
    finalReceiptArea.setMargin(new Insets(20, 20, 20, 20));
    receiptPaper.add(new JScrollPane(finalReceiptArea), BorderLayout.CENTER);

    String[] columns = {"ID", "Name", "Type", "Price", "Status", "Method"};
    DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
    JTable historyTable = new JTable(tableModel);
    JScrollPane tableScroll = new JScrollPane(historyTable);
    tableScroll.setBorder(new TitledBorder(new LineBorder(COLOR_SEC), "ORDER HISTORY", 0, 0, NAV_FONT, Color.WHITE));
    tableScroll.setOpaque(false);
    tableScroll.getViewport().setOpaque(false);

    JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
    mainContent.setOpaque(false);
    mainContent.add(receiptPaper);
    mainContent.add(tableScroll);

    root.add(mainContent, BorderLayout.CENTER);

    root.addComponentListener(new java.awt.event.ComponentAdapter() {
        public void componentShown(java.awt.event.ComponentEvent e) {
            finalReceiptArea.setText(lastReceiptText);
            tableModel.setRowCount(0);
            for (String[] row : backend.loadAllHistory()) {
                tableModel.addRow(row);
            }
        }
    });

    return root;
}

    private void navigateTo(String panelName) {
    cardLayout.show(mainPanel, panelName);
    
    // ONLY update navTitle if it exists
    if (navTitle != null) {
        switch (panelName) {
            case "Home":
                navTitle.setText("HOME");
                break;
            case "Form":
                navTitle.setText("BUILD YOUR PC");
                break;
            case "Payment":
                navTitle.setText("PAYMENT");
                break;
            case "Receipt":
                navTitle.setText("RECEIPT");
                break;
        }
        navBackBtn.setVisible(!panelName.equals("Home"));
    }
}

   private void loadPrebuiltMenu() {
    menuPanel.removeAll();
    menuPanel.setLayout(new GridLayout(0, 3, 12, 12)); 
    
    // Get filter status
    int filterIndex = (filterBox != null) ? filterBox.getSelectedIndex() : 0;
    
    // Define items data
    Object[][] items = {
        {"Starter PC", 800.0, "images/starter.jpg"},
        {"Streamer PC", 1500.0, "images/streamer.jpg"},
        {"Pro PC", 2500.0, "images/pro.jpg"}
    };
    
    for(Object[] item : items) {
        String name = (String)item[0];
        double price = (Double)item[1];
        String img = (String)item[2];
        
        boolean show = false;
        if(filterIndex == 0) show = true; // Show All
        else if(filterIndex == 1 && price <= 1500) show = true; // Budget
        else if(filterIndex == 2 && price > 1500) show = true;  // High End
        
        if(show) {
            addMenuItem(name, price, img);
        }
    }
    
    menuPanel.revalidate();
    menuPanel.repaint();
}
    private void loadCustomMenu() {
        menuPanel.removeAll();
        menuPanel.setLayout(new BorderLayout());
        JPanel subNav = new JPanel(new FlowLayout());
        subNav.setOpaque(false);
        JButton cpuBtn = new ModernButton("CPUs", COLOR_SEC, new Color(150, 0, 0));
        JButton gpuBtn = new ModernButton("GPUs", COLOR_SEC, new Color(150, 0, 0));
        JButton ramBtn = new ModernButton("RAM", COLOR_SEC, new Color(150, 0, 0));
        subNav.add(cpuBtn);
        subNav.add(gpuBtn);
        subNav.add(ramBtn);

        JPanel itemsGrid = new JPanel(new GridLayout(0, 2, 10, 10));
        itemsGrid.setOpaque(false);

        cpuBtn.addActionListener(e -> {
            itemsGrid.removeAll();
            addPartCard(itemsGrid, "Intel i9-14900K", 589, "images/i9.jpg");
            addPartCard(itemsGrid, "AMD Ryzen 7", 399, "images/ryzen.jpg");
            itemsGrid.revalidate();
            itemsGrid.repaint();
        });

        gpuBtn.addActionListener(e -> {
            itemsGrid.removeAll();
            addPartCard(itemsGrid, "RTX 4090", 1599, "images/4090.jpg");
            addPartCard(itemsGrid, "RTX 3060", 350, "images/3060.jpg");
            itemsGrid.revalidate();
            itemsGrid.repaint();
        });

        ramBtn.addActionListener(e -> {
            itemsGrid.removeAll();
            addPartCard(itemsGrid, "32GB DDR5", 150, "images/ram.jpg");
            itemsGrid.revalidate();
            itemsGrid.repaint();
        });

        menuPanel.add(subNav, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(itemsGrid);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        menuPanel.add(scroll, BorderLayout.CENTER);
        menuPanel.revalidate();
        menuPanel.repaint();
        cpuBtn.doClick();
    }

  private void addMenuItem(String name, double price, String imgPath) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel img = new JLabel("", SwingConstants.CENTER);
        if(imageCache.get(imgPath) != null) img.setIcon(imageCache.get(imgPath));
        else img.setText(name);
        
        JLabel lbl = new JLabel("<html><center>"+name+"<br>RM"+price+"</center></html>", SwingConstants.CENTER);
        card.add(img, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // === NEW CODE: IMMEDIATE LIMIT CHECK ===
                if (cartItems.size() >= 20) {
                    JOptionPane.showMessageDialog(frame, 
                        "Cart is full! You cannot add more than 20 items.", 
                        "Order Limit Reached", 
                        JOptionPane.WARNING_MESSAGE);
                    return; // Stop here, do not add the item
                }
                // =======================================

                cartItems.add(new CartItem(new PrebuiltPC("Guest", "N/A", 0, name, price)));
                updateUISelection();
            }
        });
        menuPanel.add(card);
    }

   private void addPartCard(JPanel container, String name, double price, String imgPath) {
    JPanel card = createBaseCard(name, price, imgPath);
    card.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {  // Fixed: MouseEvent not Event.MouseEvent
            // Check if item already exists in cart
            boolean found = false;
            for (CartItem item : cartItems) {
                if (item.getName().equals(name)) {
                    item.quantity++;
                    found = true;
                    break;
                }
            }
            
            // If not found, add new item
            if (!found) {
                cartItems.add(new CartItem(new PrebuiltPC("User", "Custom Part", 0, name, price)));
            }
            
            updateUISelection();  // CORRECT - this is the NEW version without parameters
        }
    });
    container.add(card);
}

   private JPanel createBaseCard(String name, double price, String imgPath) {
    JPanel card = new JPanel(new BorderLayout(5, 5));
    
    // CHANGE ONLY THESE TWO LINES:
    card.setBackground(Color.WHITE);  // SOLID WHITE (was: new Color(255, 255, 255, 180))
    card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));  // SOLID BORDER (was: new Color(200, 200, 200, 180))
    
    // EVERYTHING ELSE STAYS THE SAME:
    card.setPreferredSize(new Dimension(200, 250));
    card.setCursor(new Cursor(Cursor.HAND_CURSOR));

    JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
    ImageIcon icon = imageCache.get(imgPath);
    if (icon != null) {
        Image scaled = icon.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
        imgLabel.setIcon(new ImageIcon(scaled));
    } else {
        imgLabel.setText("No Image");
        // CHANGE THIS TO SOLID:
        imgLabel.setForeground(Color.DARK_GRAY);  // Solid text (was: new Color(100, 100, 100, 200))
        imgLabel.setBackground(new Color(240, 240, 240));  // Solid background
        imgLabel.setOpaque(true);  // Make background visible
    }

    JLabel nameLbl = new JLabel("<html><center>" + name + "</center></html>", SwingConstants.CENTER);
    nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
    nameLbl.setForeground(Color.BLACK);

    JLabel priceLbl = new JLabel("RM" + String.format("%.2f", price), SwingConstants.CENTER);
    priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
    priceLbl.setForeground(COLOR_ACCENT);

    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setOpaque(false);
    bottomPanel.add(priceLbl, BorderLayout.CENTER);

    card.add(nameLbl, BorderLayout.NORTH);
    card.add(imgLabel, BorderLayout.CENTER);
    card.add(bottomPanel, BorderLayout.SOUTH);

    return card;
}
    private void updateUISelection() {
    System.out.println("DEBUG: updateUISelection called");
    updateOrderList();
    totalLabel.setText(String.format("TOTAL: RM%.2f", getCartTotal()));
    receiptTextArea.setText(generateReceiptPreview()); // Changed this line
}

private void updateOrderList() {
    System.out.println("DEBUG: updateOrderList called, cart items: " + cartItems.size());
    
    orderListPanel.removeAll();
    
    if (cartItems.isEmpty()) {
        // Center the "Cart is empty" message
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setOpaque(false);
        
        JLabel emptyLabel = new JLabel("Cart is empty", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Smaller font
        emptyLabel.setForeground(new Color(150, 150, 150));
        
        emptyPanel.add(emptyLabel);
        orderListPanel.add(emptyPanel);
    } else {
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            final int index = i;
            
            // Create the row panel - MAKE IT SMALLER
            JPanel itemRow = new JPanel(new BorderLayout(5, 0)); // Less spacing
            itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Much smaller height (was 60)
            itemRow.setBackground(new Color(250, 250, 250)); // Very light background
            itemRow.setBorder(new EmptyBorder(3, 3, 3, 3)); // Less padding
            
            // ========== LEFT: RED X REMOVE BUTTON ==========
            JButton removeBtn = new JButton("✕");
            removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Smaller font
            removeBtn.setForeground(Color.WHITE);
            removeBtn.setBackground(new Color(220, 0, 0));
            removeBtn.setFocusPainted(false);
            removeBtn.setBorderPainted(false);
            removeBtn.setPreferredSize(new Dimension(25, 25)); // Smaller button (was 35)
            removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            removeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    removeBtn.setBackground(new Color(255, 50, 50));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    removeBtn.setBackground(new Color(220, 0, 0));
                }
            });
            
            removeBtn.addActionListener(e -> {
                cartItems.remove(index);
                updateUISelection();
            });
            
            // ========== CENTER LEFT: Item name ==========
            JLabel nameLabel = new JLabel(item.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font
            
            // ========== CENTER: Quantity controls ==========
            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0)); // Less spacing
            quantityPanel.setOpaque(false);
            
            // Minus button - SMALLER
            JButton minusBtn = new JButton("-");
            minusBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Smaller font
            minusBtn.setPreferredSize(new Dimension(20, 20)); // Smaller button
            minusBtn.setBackground(new Color(240, 240, 240));
            minusBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            minusBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            minusBtn.addActionListener(e -> {
                if (item.quantity > 1) {
                    item.quantity--;
                } else {
                    cartItems.remove(index);
                }
                updateUISelection();
            });
            
            // Quantity label - SMALLER
            JLabel qtyLabel = new JLabel(String.valueOf(item.quantity));
            qtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font
            qtyLabel.setPreferredSize(new Dimension(25, 20)); // Smaller label
            qtyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            qtyLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            
            // Plus button - SMALLER
            JButton plusBtn = new JButton("+");
            plusBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Smaller font
            plusBtn.setPreferredSize(new Dimension(20, 20)); // Smaller button
            plusBtn.setBackground(new Color(240, 240, 240));
            plusBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            plusBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            plusBtn.addActionListener(e -> {
                item.quantity++;
                updateUISelection();
            });
            
            quantityPanel.add(minusBtn);
            quantityPanel.add(qtyLabel);
            quantityPanel.add(plusBtn);
            
            // ========== RIGHT: Total price ==========
            JLabel priceLabel = new JLabel(String.format("RM%.2f", item.getTotalPrice()));
            priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font
            priceLabel.setForeground(COLOR_ACCENT);
            
            // ========== ASSEMBLE THE ROW ==========
            // Create a panel for the remove button and item name
            JPanel leftPanel = new JPanel(new BorderLayout(5, 0));
            leftPanel.setOpaque(false);
            leftPanel.add(removeBtn, BorderLayout.WEST);
            leftPanel.add(nameLabel, BorderLayout.CENTER);
            
            // Add all panels to the row
            itemRow.add(leftPanel, BorderLayout.WEST);
            itemRow.add(quantityPanel, BorderLayout.CENTER);
            itemRow.add(priceLabel, BorderLayout.EAST);
            
            orderListPanel.add(itemRow);
            orderListPanel.add(Box.createVerticalStrut(2)); // Minimal spacing between items
        }
    }
    
    orderListPanel.revalidate();
    orderListPanel.repaint();
}

private double getCartTotal() {
    double total = 0;
    for (CartItem item : cartItems) {
        total += item.getTotalPrice();
    }
    // Add Rubric Extras
    total += warrantyCost;
    total += shippingCost;
    
    return total;
}

    
    private String generateReceiptPreview() {
    System.out.println("=== DEBUG RECEIPT GENERATION ===");
    System.out.println("Payment Method: " + currentPaymentMethod);
    System.out.println("cashGiven: " + cashGiven);
    System.out.println("cashChange: " + cashChange);
    System.out.println("Cart Total: " + getCartTotal());
    
    String customer = nameField.getText().trim().isEmpty() ? "Customer" : nameField.getText().trim();
    StringBuilder sb = new StringBuilder();
    
    // Get current date/time
    java.util.Date now = new java.util.Date();
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
    String dateStr = sdf.format(now);

    // ========== HEADER ==========
    sb.append("    JUSPC SYSTEM INTEGRATORS    \n");
    sb.append("==================================\n");
    sb.append("Date: ").append(dateStr).append("\n");
    sb.append("Customer: ").append(customer).append("\n");
    sb.append("----------------------------------\n");
    
    // ========== COLUMN HEADERS ==========
    sb.append(String.format("%-3s %-20s %8s\n", "QTY", "ITEM", "PRICE"));
    sb.append("----------------------------------\n");
    
    // ========== ITEMS ==========
    double total = getCartTotal();
    if (cartItems.isEmpty()) {
        sb.append("          No items in cart\n");
    } else {
        for (CartItem item : cartItems) {
            String itemName = item.getName();
            if (itemName.length() > 20) {
                itemName = itemName.substring(0, 17) + "...";
            }
            
            sb.append(String.format("%-3d %-20s %8.2f\n", 
                item.quantity, 
                itemName, 
                item.getTotalPrice()));
        }
    }
    
    // ========== PAYMENT SUMMARY ==========
    sb.append("----------------------------------\n");
    sb.append(String.format("%-25s %8.2f\n", "TOTAL DUE: RM", total));
    
    // Show payment details - FIXED LOGIC
    if (currentPaymentMethod != null && currentPaymentMethod.equals("CASH")) {
        // Cash payment - use instance variables
        sb.append(String.format("%-25s %8.2f\n", "CASH PAID: RM", cashGiven));
        sb.append(String.format("%-25s %8.2f\n", "CHANGE: RM", cashChange));
        sb.append("----------------------------------\n");
        sb.append(String.format("%-25s %8s\n", "PAYMENT METHOD:", "CASH"));
    } else {
        // Card payment or default
        sb.append("----------------------------------\n");
        sb.append(String.format("%-25s %8s\n", "PAYMENT METHOD:", 
            currentPaymentMethod != null ? currentPaymentMethod : "CARD"));
    }
    
    sb.append("==================================\n");
    sb.append("  Thank you for your order!\n");
    sb.append("==================================\n");
    
    System.out.println("=== END DEBUG ===\n");
    return sb.toString();
}

    class BackgroundPanel extends JPanel {
    private Image img;
    private Image scaledImg;
    
    public BackgroundPanel(String path) { 
        try { 
            img = new ImageIcon(path).getImage();
        } catch(Exception e) {
            System.err.println("Could not load background: " + path);
        }
    }
    
    @Override 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(img != null) {
            // Only rescale if size changed (better performance)
            if (scaledImg == null || 
                scaledImg.getWidth(null) != getWidth() || 
                scaledImg.getHeight(null) != getHeight()) {
                
                scaledImg = img.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            }
            g.drawImage(scaledImg, 0, 0, this);
        }
    }
}

    class ModernButton extends JButton {
        private Color normalColor;
        private Color hoverColor;

        public ModernButton(String text, Color color, Color hoverColor) {
            super(text);
            this.normalColor = color;
            this.hoverColor = hoverColor;
            setBackground(color);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setPreferredSize(new Dimension(180, 45));
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(hoverColor);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(normalColor);
                }
            });
        }
    }
}