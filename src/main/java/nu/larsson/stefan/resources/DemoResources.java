package nu.larsson.stefan.resources;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demo MCP Resources showcasing static and dynamic content access.
 *
 * Resources provide data that the AI can read. Unlike tools (which perform actions),
 * resources are for providing content/data to the AI model.
 *
 * Resources use URI templates - the AI requests a specific URI and gets content back.
 */
@Component
public class DemoResources {

    // Simulated document store
    private final Map<String, Document> documents = new ConcurrentHashMap<>();

    public DemoResources() {
        // Initialize with some demo documents
        documents.put("readme", new Document(
            "README",
            "text/plain",
            """
            Welcome to the MCP Demo Server!

            This server demonstrates the Model Context Protocol features:
            - Tools: Functions the AI can call
            - Resources: Data the AI can read
            - Prompts: Pre-defined prompt templates

            Try asking the AI to use these features!
            """
        ));

        documents.put("config", new Document(
            "Configuration",
            "application/json",
            """
            {
              "server": {
                "name": "mcp-demo-server",
                "version": "1.0.0",
                "features": ["tools", "resources", "prompts"]
              },
              "settings": {
                "debug": false,
                "maxConnections": 10
              }
            }
            """
        ));

        documents.put("guide", new Document(
            "User Guide",
            "text/markdown",
            """
            # MCP Demo User Guide

            ## Available Tools
            - `add`, `subtract`, `multiply`, `divide` - Calculator operations
            - `format_text` - Text formatting (uppercase, lowercase, etc.)
            - `get_weather` - Mock weather data
            - `roll_dice` - Dice rolling simulation
            - `get_current_time` - Current time in any timezone

            ## Available Resources
            - `document://{id}` - Access stored documents
            - `status://server` - Server status information
            - `timestamp://now` - Current timestamp

            ## Available Prompts
            - `code_review` - Code review prompt template
            - `explain_concept` - Concept explanation template
            - `debug_helper` - Debugging assistance template
            """
        ));
    }

    /**
     * Dynamic document resource with URI template.
     * Access documents by ID: document://readme, document://config, etc.
     */
    @McpResource(
        uri = "document://{id}",
        name = "Document Store",
        description = "Access stored documents by ID. Available IDs: readme, config, guide"
    )
    public ReadResourceResult getDocument(String id) {
        Document doc = documents.get(id.toLowerCase());

        if (doc == null) {
            return new ReadResourceResult(List.of(
                new TextResourceContents(
                    "document://" + id,
                    "text/plain",
                    "Document not found. Available documents: " + String.join(", ", documents.keySet())
                )
            ));
        }

        return new ReadResourceResult(List.of(
            new TextResourceContents(
                "document://" + id,
                doc.mimeType(),
                doc.content()
            )
        ));
    }

    /**
     * Static resource providing server status.
     */
    @McpResource(
        uri = "status://server",
        name = "Server Status",
        description = "Get current server status and statistics"
    )
    public ReadResourceResult getServerStatus() {
        String status = """
            Server Status Report
            ====================
            Status: Running
            Uptime: %s
            Document Count: %d
            Available Documents: %s
            Java Version: %s
            Memory Used: %d MB
            Memory Free: %d MB
            """.formatted(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                documents.size(),
                String.join(", ", documents.keySet()),
                System.getProperty("java.version"),
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024),
                Runtime.getRuntime().freeMemory() / (1024 * 1024)
            );

        return new ReadResourceResult(List.of(
            new TextResourceContents("status://server", "text/plain", status)
        ));
    }

    /**
     * Timestamp resource - always returns current time.
     */
    @McpResource(
        uri = "timestamp://now",
        name = "Current Timestamp",
        description = "Get the current server timestamp in various formats"
    )
    public ReadResourceResult getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        String content = """
            {
              "iso": "%s",
              "date": "%s",
              "time": "%s",
              "epoch_millis": %d,
              "day_of_week": "%s",
              "day_of_year": %d
            }
            """.formatted(
                now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                now.format(DateTimeFormatter.ISO_LOCAL_DATE),
                now.format(DateTimeFormatter.ISO_LOCAL_TIME),
                System.currentTimeMillis(),
                now.getDayOfWeek(),
                now.getDayOfYear()
            );

        return new ReadResourceResult(List.of(
            new TextResourceContents("timestamp://now", "application/json", content)
        ));
    }

    /**
     * Resource with exchange context for logging.
     */
    @McpResource(
        uri = "echo://{message}",
        name = "Echo Resource",
        description = "Echoes back the provided message with metadata"
    )
    public ReadResourceResult echoMessage(String message, McpSyncServerExchange exchange) {
        // Log the access using the exchange context
        exchange.loggingNotification(new LoggingMessageNotification(
            LoggingLevel.INFO,
            "DemoResources",
            "Echo resource accessed with message: " + message
        ));

        String content = """
            Echo Response
            =============
            Original: %s
            Reversed: %s
            Length: %d
            Uppercase: %s
            """.formatted(
                message,
                new StringBuilder(message).reverse().toString(),
                message.length(),
                message.toUpperCase()
            );

        return new ReadResourceResult(List.of(
            new TextResourceContents("echo://" + message, "text/plain", content)
        ));
    }

    // Helper record for documents
    private record Document(String title, String mimeType, String content) {}
}
