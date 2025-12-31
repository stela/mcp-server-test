# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the bootable JAR
./gradlew bootJar

# Run the MCP server (STDIO mode)
java -jar build/libs/mcp-server-test-1.0-SNAPSHOT.jar

# Run tests
./gradlew test
```

## Architecture

This is an MCP (Model Context Protocol) server built with Spring AI 2.0.0-M1 and Spring Boot 4.0.1. It runs in STDIO mode, communicating via stdin/stdout.

### Technology Stack
- Java 25
- Spring Boot 4.0.1 / Spring Framework 7.0
- Spring AI 2.0.0-M1 (milestone release from `repo.spring.io/milestone`)
- MCP Java SDK 0.17.0

### MCP Components

The server exposes three types of MCP primitives via Spring AI annotations from `org.springaicommunity.mcp.annotation`:

1. **Tools** (`@McpTool`) - Functions the AI can invoke
   - `DemoTools.java` - Basic tools: calculator, text formatting, time, weather, dice
   - `AdvancedDemoTools.java` - Demonstrates logging (`McpSyncServerExchange.loggingNotification`) and progress notifications (`progressNotification`)

2. **Resources** (`@McpResource`) - Data the AI can read via URI templates
   - `DemoResources.java` - Document store (`document://{id}`), server status, timestamps

3. **Prompts** (`@McpPrompt`) - Pre-defined prompt templates
   - `DemoPrompts.java` - Code review, concept explanation, debug helper templates

### Key Patterns

- All MCP components are Spring `@Component` beans, auto-discovered by Spring Boot
- Tools use `@McpToolParam` for parameter descriptions
- Resources use URI templates with path parameters
- Prompts use `@McpArg` for arguments and return `GetPromptResult` with `PromptMessage` list
- Advanced tools inject `McpSyncServerExchange` to send notifications back to the client

### Configuration

`application.properties` configures STDIO mode:
- `spring.main.web-application-type=none` - No web server
- `spring.ai.mcp.server.stdio=true` - Enable STDIO transport
- Banner and console logging disabled to avoid corrupting STDIO protocol
