package nu.larsson.stefan.tools;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ProgressNotification;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

/**
 * Advanced MCP Tools demonstrating:
 * - Logging (server sends log messages to client)
 * - Progress notifications (server reports progress to client)
 *
 * These features enable communication from server to client during tool execution.
 *
 * NOTE: Not all MCP clients support all features.
 * - Claude Desktop: Supports logging and progress
 * - Claude Code: Supports logging and progress
 *
 * Advanced features like Sampling and Elicitation require specific client support
 * and may not be available in all configurations. Check Spring AI documentation
 * for the latest on these features.
 */
@Component
public class AdvancedDemoTools {

    // ==================== Logging Demo ====================

    /**
     * Demonstrates logging - sending log messages to the MCP client.
     * The client can display these in its UI or save to logs.
     *
     * MCP defines these log levels:
     * - debug: Detailed debugging information
     * - info: General informational messages
     * - notice: Normal but significant events
     * - warning: Warning conditions
     * - error: Error conditions
     * - critical: Critical conditions
     * - alert: Action must be taken immediately
     * - emergency: System is unusable
     */
    @McpTool(name = "demo_logging", description = "Demonstrates MCP logging capabilities - sends different log levels to the client")
    public String demoLogging(
            @McpToolParam(description = "A message to include in the logs", required = true) String message,
            McpSyncServerExchange exchange) {

        // Send debug level log
        exchange.loggingNotification(new LoggingMessageNotification(
            LoggingLevel.DEBUG,
            "DemoTools",
            "Debug: Starting operation with message: " + message
        ));

        // Send info level log
        exchange.loggingNotification(new LoggingMessageNotification(
            LoggingLevel.INFO,
            "DemoTools",
            "Info: Processing message: " + message
        ));

        // Send warning level log
        exchange.loggingNotification(new LoggingMessageNotification(
            LoggingLevel.WARNING,
            "DemoTools",
            "Warning: This is a demo warning for: " + message
        ));

        return """
            Sent log messages at debug, info, and warning levels!

            Check your MCP client's log output to see the messages.

            MCP Log Levels (RFC 5424 severity):
            - debug: Detailed debugging info
            - info: General information
            - notice: Normal but significant
            - warning: Warning conditions
            - error: Error conditions
            - critical: Critical conditions
            - alert: Immediate action needed
            - emergency: System unusable
            """;
    }

    // ==================== Progress Notifications Demo ====================

    /**
     * Demonstrates progress notifications - showing work progress to the client.
     * Useful for long-running operations.
     *
     * Progress notifications include:
     * - progressToken: Links the notification to the original request
     * - progress: Current progress value
     * - total: Total expected value (optional)
     * - message: Human-readable status message (optional)
     */
    @McpTool(name = "demo_progress", description = "Demonstrates progress notifications - simulates a long-running task with progress updates")
    public String demoProgress(
            @McpToolParam(description = "Number of steps to simulate (1-10)", required = true) int steps,
            @McpToolParam(description = "Delay between steps in milliseconds (100-2000)", required = false) Integer delayMs,
            @McpToolParam(description = "Progress token from the request (if available)", required = false) String progressToken,
            McpSyncServerExchange exchange) {

        // Validate inputs
        steps = Math.max(1, Math.min(10, steps));
        int delay = delayMs != null ? Math.max(100, Math.min(2000, delayMs)) : 500;

        // Log start
        exchange.loggingNotification(new LoggingMessageNotification(
            LoggingLevel.INFO,
            "DemoTools",
            "Starting progress demo with " + steps + " steps"
        ));

        // Note: Progress tokens are typically provided by the client in the request
        // For demo purposes, we'll generate a mock token if none provided
        String token = progressToken != null ? progressToken : "demo-progress-" + System.currentTimeMillis();

        // Initial progress notification
        exchange.progressNotification(new ProgressNotification(
            token,
            0.0,
            (double) steps,
            "Starting task..."
        ));

        // Simulate work with progress updates
        for (int i = 1; i <= steps; i++) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Interrupted at step " + i;
            }

            // Send progress update
            exchange.progressNotification(new ProgressNotification(
                token,
                (double) i,
                (double) steps,
                "Completed step " + i + " of " + steps
            ));

            // Also log each step completion
            exchange.loggingNotification(new LoggingMessageNotification(
                LoggingLevel.DEBUG,
                "DemoTools",
                "Step " + i + " complete"
            ));
        }

        return "Task completed! Processed " + steps + " steps with progress tracking.\n\n" +
               "Check your MCP client to see if progress was displayed.";
    }

    // ==================== Capability Info Tool ====================

    /**
     * Returns information about MCP capabilities.
     * Useful for understanding what features are available.
     */
    @McpTool(name = "mcp_capabilities_info", description = "Explains MCP server and client capabilities")
    public String mcpCapabilitiesInfo() {
        return """
            MCP (Model Context Protocol) Capabilities
            ==========================================

            SERVER CAPABILITIES (what this server provides):
            ------------------------------------------------
            ✅ Tools: Functions the AI can call (like this one!)
            ✅ Resources: Data the AI can read (document://, status://, etc.)
            ✅ Prompts: Pre-defined prompt templates
            ✅ Logging: Server can send log messages to client
            ✅ Progress: Server can report progress on long operations

            CLIENT CAPABILITIES (depends on your MCP client):
            ------------------------------------------------
            📋 Roots: Client can provide filesystem roots
            🔔 Sampling: Client can generate AI content on server's behalf
            📝 Elicitation: Client can request user input for server

            ADVANCED FEATURES:
            ------------------
            - Sampling allows the server to ask the CLIENT's AI to generate
              content, useful for creative tasks without server-side AI.

            - Elicitation allows the server to pause and ask the USER for
              input, useful for confirmations or gathering data.

            Try these tools to explore:
            - demo_logging: See logging in action
            - demo_progress: Watch progress notifications
            - add, subtract, multiply, divide: Basic calculator
            - format_text, count_words: Text manipulation
            - get_weather, roll_dice: Fun mock data
            """;
    }

    // ==================== Error Handling Demo ====================

    /**
     * Demonstrates error handling in MCP tools.
     * Shows how errors are reported back to the client.
     */
    @McpTool(name = "demo_error_handling", description = "Demonstrates how MCP handles errors in tools")
    public String demoErrorHandling(
            @McpToolParam(description = "Type of error to simulate: none, validation, runtime, custom", required = true) String errorType,
            McpSyncServerExchange exchange) {

        exchange.loggingNotification(new LoggingMessageNotification(
            LoggingLevel.INFO,
            "DemoTools",
            "Testing error handling with type: " + errorType
        ));

        return switch (errorType.toLowerCase()) {
            case "none" -> "No error! Everything worked perfectly.";
            case "validation" -> {
                exchange.loggingNotification(new LoggingMessageNotification(
                    LoggingLevel.WARNING,
                    "DemoTools",
                    "Validation error simulation"
                ));
                yield "Validation Error: This simulates a validation failure.\n" +
                      "In real tools, you'd check inputs and return helpful messages.";
            }
            case "runtime" -> {
                exchange.loggingNotification(new LoggingMessageNotification(
                    LoggingLevel.ERROR,
                    "DemoTools",
                    "About to throw runtime exception for demo"
                ));
                throw new RuntimeException("This is a simulated runtime error to show how exceptions are handled!");
            }
            case "custom" -> {
                exchange.loggingNotification(new LoggingMessageNotification(
                    LoggingLevel.ERROR,
                    "DemoTools",
                    "Custom error condition"
                ));
                yield "Custom Error: Operation failed due to [simulated reason].\n" +
                      "Suggestion: Try a different approach or check your inputs.";
            }
            default -> "Unknown error type: " + errorType + "\n" +
                       "Valid types: none, validation, runtime, custom";
        };
    }
}
