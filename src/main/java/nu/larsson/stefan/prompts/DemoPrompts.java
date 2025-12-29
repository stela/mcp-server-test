package nu.larsson.stefan.prompts;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Demo MCP Prompts showcasing pre-defined prompt templates.
 *
 * Prompts are reusable templates that help structure conversations.
 * They can have arguments and provide autocompletion suggestions.
 *
 * Think of prompts as "slash commands" that expand into full prompt text.
 */
@Component
public class DemoPrompts {

    // Available programming languages for completion
    private static final List<String> LANGUAGES = List.of(
        "java", "python", "javascript", "typescript", "go", "rust", "kotlin", "scala", "c++", "c#"
    );

    // Available review types for completion
    private static final List<String> REVIEW_TYPES = List.of(
        "security", "performance", "readability", "best-practices", "testing", "documentation"
    );

    /**
     * Code review prompt - helps structure code review requests.
     */
    @McpPrompt(
        name = "code_review",
        description = "Generate a structured code review request"
    )
    public GetPromptResult codeReviewPrompt(
            @McpArg(name = "language", description = "Programming language", required = true) String language,
            @McpArg(name = "focus", description = "Review focus: security, performance, readability, best-practices, testing, documentation", required = false) String focus) {

        String focusText = focus != null
            ? "Focus especially on: " + focus
            : "Provide a comprehensive review covering security, performance, and best practices";

        String promptText = """
            Please review the following %s code.

            %s

            Structure your review as:
            1. **Summary**: Brief overview of what the code does
            2. **Strengths**: What's done well
            3. **Issues**: Problems found (categorized by severity)
            4. **Suggestions**: Specific improvements with code examples
            5. **Security**: Any security concerns

            Please paste the code to review:
            """.formatted(language, focusText);

        return new GetPromptResult(
            "Code Review Request (" + language + ")",
            List.of(new PromptMessage(Role.USER, new TextContent(promptText)))
        );
    }

    // Note: @McpComplete for argument autocompletion may require specific
    // Spring AI versions. Commented out for compatibility.
    // Uncomment and adjust once your Spring AI version supports it.
    //
    // @McpComplete(prompt = "code_review")
    // public List<String> completeLanguage(String prefix) {
    //     return LANGUAGES.stream()
    //         .filter(lang -> lang.toLowerCase().startsWith(prefix.toLowerCase()))
    //         .toList();
    // }

    /**
     * Concept explanation prompt - helps explain technical concepts.
     */
    @McpPrompt(
        name = "explain_concept",
        description = "Generate a prompt to explain a technical concept"
    )
    public GetPromptResult explainConceptPrompt(
            @McpArg(name = "concept", description = "The concept to explain", required = true) String concept,
            @McpArg(name = "level", description = "Expertise level: beginner, intermediate, advanced", required = false) String level,
            @McpArg(name = "context", description = "Additional context or specific use case", required = false) String context) {

        String levelText = switch (level != null ? level.toLowerCase() : "intermediate") {
            case "beginner" -> "Explain as if to someone new to programming, avoiding jargon";
            case "advanced" -> "Provide an in-depth technical explanation with implementation details";
            default -> "Explain at an intermediate level, assuming basic programming knowledge";
        };

        String contextText = context != null
            ? "\n\nSpecific context: " + context
            : "";

        String promptText = """
            Please explain: **%s**

            %s%s

            Structure your explanation as:
            1. **Definition**: What it is in simple terms
            2. **Why it matters**: Real-world importance and use cases
            3. **How it works**: Technical explanation with diagrams/pseudocode if helpful
            4. **Example**: Concrete code example demonstrating the concept
            5. **Common pitfalls**: Mistakes to avoid
            6. **Related concepts**: What to learn next
            """.formatted(concept, levelText, contextText);

        return new GetPromptResult(
            "Explain: " + concept,
            List.of(new PromptMessage(Role.USER, new TextContent(promptText)))
        );
    }

    /**
     * Debug helper prompt - assists with debugging issues.
     */
    @McpPrompt(
        name = "debug_helper",
        description = "Generate a structured debugging assistance prompt"
    )
    public GetPromptResult debugHelperPrompt(
            @McpArg(name = "error_type", description = "Type of error: runtime, compile, logic, performance", required = true) String errorType,
            @McpArg(name = "language", description = "Programming language", required = false) String language) {

        String langText = language != null ? " (" + language + ")" : "";

        String promptText = """
            I need help debugging a %s error%s.

            Please help me by:
            1. **Understanding**: Ask clarifying questions about the error
            2. **Analysis**: Identify potential root causes
            3. **Diagnosis**: Suggest debugging steps/techniques
            4. **Solution**: Provide fixes with explanations
            5. **Prevention**: How to avoid this in the future

            Here's the information about my issue:

            **Error message/behavior**:
            [Paste error message or describe unexpected behavior]

            **Relevant code**:
            [Paste the code causing the issue]

            **What I've tried**:
            [Describe debugging attempts]

            **Expected behavior**:
            [What should happen]
            """.formatted(errorType, langText);

        return new GetPromptResult(
            "Debug Helper: " + errorType + " error",
            List.of(new PromptMessage(Role.USER, new TextContent(promptText)))
        );
    }

    /**
     * Multi-message prompt example - demonstrates conversation structure.
     */
    @McpPrompt(
        name = "pair_programming",
        description = "Start a pair programming session with structured roles"
    )
    public GetPromptResult pairProgrammingPrompt(
            @McpArg(name = "task", description = "What you want to build or solve", required = true) String task,
            @McpArg(name = "approach", description = "Preferred approach: tdd, iterative, design-first", required = false) String approach) {

        String approachText = switch (approach != null ? approach.toLowerCase() : "iterative") {
            case "tdd" -> "We'll use Test-Driven Development: write tests first, then implement.";
            case "design-first" -> "We'll start with design and architecture before coding.";
            default -> "We'll work iteratively, building incrementally.";
        };

        // Multiple messages to set up context
        return new GetPromptResult(
            "Pair Programming: " + task,
            List.of(
                new PromptMessage(Role.USER, new TextContent(
                    "Let's pair program on: " + task
                )),
                new PromptMessage(Role.ASSISTANT, new TextContent(
                    """
                    I'd be happy to pair program with you on this task!

                    %s

                    I'll act as your programming partner:
                    - I'll think through problems out loud
                    - I'll suggest approaches and ask for your input
                    - I'll write code incrementally and explain my reasoning
                    - I'll catch potential issues before they become bugs
                    - I'll ask questions when requirements are unclear

                    Let's start! Can you tell me more about the context and any constraints?
                    """.formatted(approachText)
                ))
            )
        );
    }

    /**
     * Git commit message prompt.
     */
    @McpPrompt(
        name = "commit_message",
        description = "Generate a well-structured git commit message"
    )
    public GetPromptResult commitMessagePrompt(
            @McpArg(name = "type", description = "Commit type: feat, fix, refactor, docs, test, chore", required = true) String type,
            @McpArg(name = "scope", description = "Affected component/module", required = false) String scope) {

        String scopeText = scope != null ? "(" + scope + ")" : "";

        String promptText = """
            Please help me write a commit message following conventional commits format.

            **Type**: %s%s

            Format:
            ```
            %s%s: <short description>

            <body - what and why, not how>

            <footer - breaking changes, issue references>
            ```

            Guidelines:
            - Subject line: imperative mood, max 50 chars, no period
            - Body: wrap at 72 chars, explain motivation
            - Reference issues: "Fixes #123" or "Relates to #456"

            Please describe what changes you made:
            """.formatted(type, scopeText, type, scopeText);

        return new GetPromptResult(
            "Commit Message: " + type,
            List.of(new PromptMessage(Role.USER, new TextContent(promptText)))
        );
    }

    // @McpComplete(prompt = "commit_message")
    // public List<String> completeCommitType(String prefix) {
    //     return Stream.of("feat", "fix", "refactor", "docs", "test", "chore", "style", "perf", "build", "ci")
    //         .filter(type -> type.startsWith(prefix.toLowerCase()))
    //         .toList();
    // }
}
