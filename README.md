# MCP Server Demo

A comprehensive demo of the **Model Context Protocol (MCP)** using Spring AI 2.0.0-M1 and Spring Boot 4.0.1.

This project demonstrates all major MCP server features that you can test with Claude Code and Claude Desktop.

## Features Demonstrated

### Tools (`@McpTool`)
Functions the AI can invoke:

| Tool | Description |
|------|-------------|
| `add`, `subtract`, `multiply`, `divide` | Calculator operations |
| `format_text` | Text formatting (uppercase, lowercase, titlecase, reverse) |
| `count_words` | Count words, characters, and lines |
| `get_current_time` | Get time in any timezone |
| `get_weather` | Mock weather data for any city |
| `roll_dice` | Roll dice (customizable count and sides) |
| `generate_uuid` | Generate random UUIDs |
| `sort_numbers` | Sort a list of numbers |
| `demo_logging` | Demonstrates MCP logging |
| `demo_progress` | Demonstrates progress notifications |
| `mcp_capabilities_info` | Explains MCP capabilities |
| `demo_error_handling` | Shows error handling patterns |

### Resources (`@McpResource`)
Data the AI can read:

| URI | Description |
|-----|-------------|
| `document://{id}` | Access stored documents (readme, config, guide) |
| `status://server` | Server status and statistics |
| `timestamp://now` | Current timestamp in various formats |
| `echo://{message}` | Echoes message with metadata |

### Prompts (`@McpPrompt`)
Pre-defined prompt templates:

| Prompt | Description |
|--------|-------------|
| `code_review` | Structured code review request |
| `explain_concept` | Technical concept explanation |
| `debug_helper` | Debugging assistance |
| `pair_programming` | Pair programming session setup |
| `commit_message` | Git commit message generation |

## Requirements

- Java 25
- Gradle 9+

## Building

```bash
./gradlew bootJar
```

## Running

### As a JAR (STDIO mode)

Build and run the JAR directly:

```bash
./gradlew bootJar
java -jar build/libs/mcp-server-test-1.0-SNAPSHOT.jar
```

## Configuring MCP Clients

### Claude Desktop

Add to your Claude Desktop configuration (`claude_desktop_config.json`):

```json
{
  "mcpServers": {
    "mcp-demo": {
      "command": "java",
      "args": ["-jar", "/path/to/mcp-server-test-1.0-SNAPSHOT.jar"]
    }
  }
}
```

### Claude Code

Add the server configuration:

```bash
claude mcp add --transport stdio mcp-demo -- java -jar /path/to/mcp-server-test-1.0-SNAPSHOT.jar
```

Or edit `~/.claude/settings.json`:

```json
{
  "mcpServers": {
    "mcp-demo": {
      "command": "java",
      "args": ["-jar", "/path/to/mcp-server-test-1.0-SNAPSHOT.jar"]
    }
  }
}
```

## Testing the Server

Once configured, try these prompts in Claude:

### Tools
- "Add 42 and 17"
- "What's the weather in Stockholm?"
- "Roll 3 six-sided dice"
- "Format 'hello world' as titlecase"
- "What time is it in Tokyo?"

### Resources
- "Read the readme document"
- "What's the server status?"
- "Get the current timestamp"

### Prompts
- "Use the code_review prompt for java code"
- "Explain the concept of dependency injection at beginner level"
- "Help me debug a runtime error"

### Advanced Features
- "Demo the logging capabilities with message 'Hello MCP!'"
- "Show me MCP capabilities info"
- "Run the progress demo with 5 steps"

## MCP Protocol Overview

The **Model Context Protocol (MCP)** enables standardized communication between AI applications and external tools/data.

### Server Capabilities (this server provides)
- **Tools**: Functions the AI can invoke to perform actions
- **Resources**: Data/content the AI can read
- **Prompts**: Pre-defined prompt templates
- **Logging**: Server can send log messages to client
- **Progress**: Server can report progress on long operations

### Client Capabilities (depends on your MCP client)
- **Roots**: Client can provide filesystem roots
- **Sampling**: Client can generate AI content on server's behalf
- **Elicitation**: Client can request user input for server

## Project Structure

```
src/main/java/nu/larsson/stefan/
├── Main.java                      # Spring Boot application
├── tools/
│   ├── DemoTools.java            # Basic MCP tools
│   └── AdvancedDemoTools.java    # Logging, progress demos
├── resources/
│   └── DemoResources.java        # MCP resources
└── prompts/
    └── DemoPrompts.java          # MCP prompts
```

## Technology Stack

- Spring Boot 4.0.1
- Spring Framework 7.0
- Spring AI 2.0.0-M1
- MCP Java SDK 0.17.0
- Java 25

## References

- [Model Context Protocol Specification](https://modelcontextprotocol.io/)
- [Spring AI MCP Documentation](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html)
- [MCP Annotations](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-annotations-overview.html)
