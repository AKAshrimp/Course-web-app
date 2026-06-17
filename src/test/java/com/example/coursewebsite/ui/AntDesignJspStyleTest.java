package com.example.coursewebsite.ui;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class AntDesignJspStyleTest {

    private static final Path PROJECT_ROOT = Path.of("").toAbsolutePath();

    @Test
    void globalStylesUseAdminFrontendAntDesignTokens() throws IOException {
        String styles = Files.readString(PROJECT_ROOT.resolve("src/main/resources/static/css/styles.css"));

        assertThat(styles).contains("--primary-color: #1f4ed8");
        assertThat(styles).contains("--border-radius: 10px");
        assertThat(styles).contains("--ant-layout-bg: #f4f7fb");
        assertThat(styles).contains("--ant-card-shadow: 0 8px 24px rgba(20, 34, 64, 0.08)");
        assertThat(styles).doesNotContain("#2E8B57", "#3CB371", "#006400", "#4CAF50", "#F0FFF0");
    }

    @Test
    void jspPagesDoNotUseLegacyGreenGradientInlineStyles() throws IOException {
        Path jspRoot = PROJECT_ROOT.resolve("src/main/webapp/WEB-INF/jsp");

        try (Stream<Path> paths = Files.walk(jspRoot)) {
            assertThat(paths
                    .filter(path -> path.toString().endsWith(".jsp"))
                    .map(path -> {
                        try {
                            return Files.readString(path);
                        } catch (IOException exception) {
                            throw new IllegalStateException(exception);
                        }
                    }))
                    .noneMatch(content -> content.contains("linear-gradient(90deg, var(--primary-color), var(--primary-dark))"))
                    .noneMatch(content -> content.contains("-webkit-text-fill-color: transparent"));
        }
    }

    @Test
    void layoutUsesStickyFooterStructure() throws IOException {
        String styles = Files.readString(PROJECT_ROOT.resolve("src/main/resources/static/css/styles.css"));
        String base = Files.readString(PROJECT_ROOT.resolve("src/main/webapp/WEB-INF/jsp/layout/base.jsp"));
        String header = Files.readString(PROJECT_ROOT.resolve("src/main/webapp/WEB-INF/jsp/layout/header.jsp"));

        assertThat(styles).contains("body {");
        assertThat(styles).contains("display: flex;");
        assertThat(styles).contains("min-height: 100vh;");
        assertThat(styles).contains("flex-direction: column;");
        assertThat(styles).contains(".app-main {");
        assertThat(styles).contains("flex: 1 0 auto;");
        assertThat(styles).contains("footer {");
        assertThat(styles).contains("flex-shrink: 0;");
        assertThat(base).contains("class=\"container my-4 app-main\"");
        assertThat(header).contains("class=\"container mt-4 app-main\"");
    }
}
