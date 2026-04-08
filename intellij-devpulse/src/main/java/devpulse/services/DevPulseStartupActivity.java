package devpulse.services;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * Registers project-level services on project open.
 * The actual services are registered via plugin.xml — this just
 * triggers an initial TODO scan in the background.
 */
public class DevPulseStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        // Background TODO scan on project open
        com.intellij.openapi.application.ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                TodoScannerService scanner = project.getService(TodoScannerService.class);
                if (scanner != null) {
                    scanner.scan(project);
                }
            } catch (Exception ignored) {
                // Don't block project opening on scan errors
            }
        });
    }
}
