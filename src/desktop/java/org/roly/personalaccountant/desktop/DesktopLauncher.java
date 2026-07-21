package org.roly.personalaccountant.desktop;

/**
 * Plain launcher for running the desktop app directly from an IDE.
 *
 * <p>Because {@link DesktopApp} extends {@code javafx.application.Application} and JavaFX is
 * a classpath library here (not on the module path), running {@code DesktopApp} directly
 * fails with "JavaFX runtime components are missing". This launcher does <em>not</em> extend
 * {@code Application}, which skips the JVM's module check and lets JavaFX start from the
 * classpath.
 *
 * <p>In IntelliJ: enable the {@code desktop} Maven profile, start the Spring Boot server,
 * then run this class's {@code main}. (The {@code ./mvnw -Pdesktop javafx:run} goal works too
 * and doesn't need this launcher.)
 */
public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        DesktopApp.main(args);
    }
}
