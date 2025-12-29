package nu.larsson.stefan.tools;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Demo MCP Tools showcasing different parameter types and use cases.
 *
 * Tools are functions that the AI can invoke to perform actions or retrieve computed data.
 * They're the most commonly used MCP feature.
 */
@Component
public class DemoTools {

    private final Random random = new Random();

    // ==================== Basic Calculator Tools ====================

    @McpTool(name = "add", description = "Add two numbers together")
    public double add(
            @McpToolParam(description = "First number", required = true) double a,
            @McpToolParam(description = "Second number", required = true) double b) {
        return a + b;
    }

    @McpTool(name = "subtract", description = "Subtract the second number from the first")
    public double subtract(
            @McpToolParam(description = "First number", required = true) double a,
            @McpToolParam(description = "Second number", required = true) double b) {
        return a - b;
    }

    @McpTool(name = "multiply", description = "Multiply two numbers")
    public double multiply(
            @McpToolParam(description = "First number", required = true) double a,
            @McpToolParam(description = "Second number", required = true) double b) {
        return a * b;
    }

    @McpTool(name = "divide", description = "Divide the first number by the second")
    public String divide(
            @McpToolParam(description = "Dividend (number to be divided)", required = true) double a,
            @McpToolParam(description = "Divisor (number to divide by)", required = true) double b) {
        if (b == 0) {
            return "Error: Cannot divide by zero";
        }
        return String.valueOf(a / b);
    }

    // ==================== Text Manipulation Tools ====================

    @McpTool(name = "format_text", description = "Format text in various ways")
    public String formatText(
            @McpToolParam(description = "The text to format", required = true) String text,
            @McpToolParam(description = "Format type: uppercase, lowercase, titlecase, reverse", required = true) String format) {
        return switch (format.toLowerCase()) {
            case "uppercase" -> text.toUpperCase();
            case "lowercase" -> text.toLowerCase();
            case "titlecase" -> toTitleCase(text);
            case "reverse" -> new StringBuilder(text).reverse().toString();
            default -> "Unknown format: " + format + ". Use: uppercase, lowercase, titlecase, or reverse";
        };
    }

    @McpTool(name = "count_words", description = "Count words, characters, and lines in text")
    public Map<String, Integer> countWords(
            @McpToolParam(description = "The text to analyze", required = true) String text) {
        String[] words = text.trim().split("\\s+");
        int wordCount = text.isBlank() ? 0 : words.length;
        int charCount = text.length();
        int charCountNoSpaces = text.replace(" ", "").length();
        int lineCount = text.split("\n", -1).length;

        return Map.of(
            "words", wordCount,
            "characters", charCount,
            "characters_no_spaces", charCountNoSpaces,
            "lines", lineCount
        );
    }

    // ==================== Date/Time Tools ====================

    @McpTool(name = "get_current_time", description = "Get the current date and time in a specific timezone")
    public Map<String, String> getCurrentTime(
            @McpToolParam(description = "Timezone ID (e.g., 'UTC', 'America/New_York', 'Europe/Stockholm'). Defaults to system timezone.", required = false) String timezone) {
        ZoneId zoneId = timezone != null ? ZoneId.of(timezone) : ZoneId.systemDefault();
        LocalDateTime now = LocalDateTime.now(zoneId);

        return Map.of(
            "datetime", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "date", now.format(DateTimeFormatter.ISO_LOCAL_DATE),
            "time", now.format(DateTimeFormatter.ISO_LOCAL_TIME),
            "timezone", zoneId.getId(),
            "day_of_week", now.getDayOfWeek().toString()
        );
    }

    // ==================== Mock Data Tools ====================

    @McpTool(name = "get_weather", description = "Get mock weather data for a city (demo purposes)")
    public Map<String, Object> getWeather(
            @McpToolParam(description = "City name", required = true) String city,
            @McpToolParam(description = "Temperature unit: celsius or fahrenheit", required = false) String unit) {
        // Mock weather data for demo purposes
        int tempCelsius = random.nextInt(35) - 5; // -5 to 30
        String[] conditions = {"Sunny", "Cloudy", "Rainy", "Partly Cloudy", "Snowy", "Windy"};
        String condition = conditions[random.nextInt(conditions.length)];
        int humidity = random.nextInt(60) + 30; // 30-90%

        double temperature = "fahrenheit".equalsIgnoreCase(unit)
            ? tempCelsius * 9.0 / 5.0 + 32
            : tempCelsius;
        String tempUnit = "fahrenheit".equalsIgnoreCase(unit) ? "°F" : "°C";

        return Map.of(
            "city", city,
            "temperature", Math.round(temperature * 10) / 10.0,
            "unit", tempUnit,
            "condition", condition,
            "humidity", humidity + "%",
            "note", "This is mock data for demonstration purposes"
        );
    }

    @McpTool(name = "roll_dice", description = "Roll dice with specified number of sides")
    public Map<String, Object> rollDice(
            @McpToolParam(description = "Number of dice to roll", required = true) int count,
            @McpToolParam(description = "Number of sides per die (default: 6)", required = false) Integer sides) {
        int numSides = sides != null ? sides : 6;
        if (count < 1 || count > 100) {
            return Map.of("error", "Count must be between 1 and 100");
        }
        if (numSides < 2 || numSides > 100) {
            return Map.of("error", "Sides must be between 2 and 100");
        }

        List<Integer> rolls = random.ints(count, 1, numSides + 1).boxed().toList();
        int total = rolls.stream().mapToInt(Integer::intValue).sum();

        return Map.of(
            "rolls", rolls,
            "total", total,
            "dice", count + "d" + numSides
        );
    }

    @McpTool(name = "generate_uuid", description = "Generate a random UUID")
    public String generateUuid() {
        return java.util.UUID.randomUUID().toString();
    }

    // ==================== List/Array Tools ====================

    @McpTool(name = "sort_numbers", description = "Sort a list of numbers")
    public List<Double> sortNumbers(
            @McpToolParam(description = "List of numbers to sort", required = true) List<Double> numbers,
            @McpToolParam(description = "Sort order: asc or desc (default: asc)", required = false) String order) {
        List<Double> sorted = new java.util.ArrayList<>(numbers);
        sorted.sort("desc".equalsIgnoreCase(order)
            ? java.util.Comparator.reverseOrder()
            : java.util.Comparator.naturalOrder());
        return sorted;
    }

    // ==================== Helper Methods ====================

    private String toTitleCase(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }
}
