package de.ageofflair.hycremental.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Utility class for formatting large numbers
 * Converts numbers to readable format (1.5M, 2.3B, etc.)
 * 
 * @author Kielian
 */
public class NumberFormatter {
    
    private static final String[] SUFFIXES = {
        "",      // 1
        "K",     // Thousand (1,000)
        "M",     // Million (1,000,000)
        "B",     // Billion (1,000,000,000)
        "T",     // Trillion (1,000,000,000,000)
        "Qa",    // Quadrillion
        "Qi",    // Quintillion
        "Sx",    // Sextillion
        "Sp",    // Septillion
        "Oc",    // Octillion
        "No",    // Nonillion
        "Dc",    // Decillion
        "UDc",   // Undecillion
        "DDc",   // Duodecillion
        "TDc",   // Tredecillion
        "QaDc",  // Quattuordecillion
        "QiDc"   // Quindecillion
    };
    
    private final DecimalFormat decimalFormat;
    
    public NumberFormatter() {
        this.decimalFormat = new DecimalFormat("#.##");
        this.decimalFormat.setRoundingMode(RoundingMode.DOWN);
    }
    
    /**
     * Format a BigDecimal to readable string
     * 
     * @param number The number to format
     * @return Formatted string (e.g., "1.5M", "2.3B")
     */
    public String format(BigDecimal number) {
        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        
        // Handle negative numbers
        boolean negative = number.compareTo(BigDecimal.ZERO) < 0;
        if (negative) {
            number = number.negate();
        }
        
        // Find the appropriate suffix
        int suffixIndex = 0;
        BigDecimal divisor = BigDecimal.ONE;
        BigDecimal thousand = new BigDecimal("1000");
        
        while (number.compareTo(thousand) >= 0 && suffixIndex < SUFFIXES.length - 1) {
            number = number.divide(thousand, 2, RoundingMode.DOWN);
            divisor = divisor.multiply(thousand);
            suffixIndex++;
        }
        
        // Format the number
        String formatted = decimalFormat.format(number);
        if (negative) {
            formatted = "-" + formatted;
        }
        
        return formatted + SUFFIXES[suffixIndex];
    }
    
    /**
     * Format a long to readable string
     */
    public String format(long number) {
        return format(new BigDecimal(number));
    }
    
    /**
     * Format a double to readable string
     */
    public String format(double number) {
        return format(BigDecimal.valueOf(number));
    }
    
    /**
     * Parse a formatted string back to BigDecimal
     * 
     * @param formatted Formatted string (e.g., "1.5M")
     * @return BigDecimal value
     */
    public BigDecimal parse(String formatted) {
        if (formatted == null || formatted.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        formatted = formatted.trim();
        
        // Find suffix
        String suffix = "";
        StringBuilder numberPart = new StringBuilder();
        
        for (int i = 0; i < formatted.length(); i++) {
            char c = formatted.charAt(i);
            if (Character.isDigit(c) || c == '.' || c == '-') {
                numberPart.append(c);
            } else {
                suffix = formatted.substring(i).toUpperCase();
                break;
            }
        }
        
        // Parse number
        BigDecimal number;
        try {
            number = new BigDecimal(numberPart.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
        
        // Find multiplier for suffix
        BigDecimal multiplier = BigDecimal.ONE;
        BigDecimal thousand = new BigDecimal("1000");
        
        for (int i = 0; i < SUFFIXES.length; i++) {
            if (SUFFIXES[i].equalsIgnoreCase(suffix)) {
                for (int j = 0; j < i; j++) {
                    multiplier = multiplier.multiply(thousand);
                }
                break;
            }
        }
        
        return number.multiply(multiplier);
    }
    
    /**
     * Format with custom decimal places
     */
    public String format(BigDecimal number, int decimalPlaces) {
        DecimalFormat customFormat = new DecimalFormat("#." + "#".repeat(decimalPlaces));
        customFormat.setRoundingMode(RoundingMode.DOWN);
        
        if (number.compareTo(new BigDecimal("1000")) < 0) {
            return customFormat.format(number);
        }
        
        return format(number);
    }
}