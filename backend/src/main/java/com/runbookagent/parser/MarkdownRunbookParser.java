package com.runbookagent.parser;

import com.runbookagent.dto.RunbookDto;
import com.runbookagent.dto.RunbookStepDto;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MarkdownRunbookParser {

    private final Parser parser;
    private static final Pattern NUMBERED_STEP_PATTERN = Pattern.compile("^(\\d+)\\.\\s+(.*)$");

    public MarkdownRunbookParser() {
        this.parser = Parser.builder().build();
    }

    public RunbookDto parse(String markdownContent) {
        if (markdownContent == null || markdownContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Runbook content cannot be empty");
        }

        Node document = parser.parse(markdownContent);
        RunbookVisitor visitor = new RunbookVisitor();
        document.accept(visitor);

        String title = visitor.getTitle();
        if (title == null || title.isBlank()) {
            title = "Untitled Runbook";
        }

        List<RunbookStepDto> steps = visitor.getSteps();

        // Fallback line-by-line step parser if AST list visitor found 0 steps
        if (steps.isEmpty()) {
            steps = parseStepsLineByLine(markdownContent);
        }

        return RunbookDto.builder()
                .title(title)
                .description(visitor.getDescription() != null ? visitor.getDescription() : "Runbook Procedure")
                .steps(steps)
                .build();
    }

    private List<RunbookStepDto> parseStepsLineByLine(String markdownContent) {
        List<RunbookStepDto> steps = new ArrayList<>();
        String[] lines = markdownContent.split("\\r?\\n");
        int autoStepNumber = 1;

        for (String line : lines) {
            String trimmed = line.trim();
            Matcher matcher = NUMBERED_STEP_PATTERN.matcher(trimmed);
            if (matcher.find()) {
                int stepNum = Integer.parseInt(matcher.group(1));
                String desc = matcher.group(2).trim();
                steps.add(RunbookStepDto.builder()
                        .stepNumber(stepNum)
                        .description(desc)
                        .build());
            }
        }
        return steps;
    }

    private static class RunbookVisitor extends AbstractVisitor {
        private String title;
        private String description;
        private String currentHeader;
        private final List<RunbookStepDto> steps = new ArrayList<>();
        private int stepCounter = 1;

        @Override
        public void visit(Heading heading) {
            String text = extractText(heading);
            if (heading.getLevel() == 1 && title == null) {
                title = text;
            } else {
                currentHeader = text;
            }
            super.visit(heading);
        }

        @Override
        public void visit(Paragraph paragraph) {
            if (title != null && description == null) {
                String text = extractText(paragraph);
                if (!text.isBlank() && !text.equalsIgnoreCase(currentHeader)) {
                    description = text;
                }
            }
            super.visit(paragraph);
        }

        @Override
        public void visit(ListItem listItem) {
            String itemText = extractText(listItem).trim();
            if (!itemText.isBlank()) {
                Matcher matcher = NUMBERED_STEP_PATTERN.matcher(itemText);
                int stepNum;
                String desc;

                if (matcher.find()) {
                    stepNum = Integer.parseInt(matcher.group(1));
                    desc = matcher.group(2).trim();
                } else {
                    stepNum = stepCounter++;
                    desc = itemText;
                }

                steps.add(RunbookStepDto.builder()
                        .stepNumber(stepNum)
                        .description(desc)
                        .sectionName(currentHeader)
                        .build());
            }
        }

        private String extractText(Node node) {
            StringBuilder sb = new StringBuilder();
            Node child = node.getFirstChild();
            while (child != null) {
                if (child instanceof Text textNode) {
                    sb.append(textNode.getLiteral());
                } else if (child instanceof Code codeNode) {
                    sb.append(codeNode.getLiteral());
                } else {
                    sb.append(extractText(child));
                }
                child = child.getNext();
            }
            return sb.toString();
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public List<RunbookStepDto> getSteps() {
            return steps;
        }
    }
}
