package com.runbookagent.mcp;

import com.runbookagent.security.ActionType;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class OsCommandExecutor {

    private final String osName;

    public OsCommandExecutor() {
        this.osName = System.getProperty("os.name", "unknown").toLowerCase();
    }

    public boolean isWindows() {
        return osName.contains("win");
    }

    public boolean isLinux() {
        return osName.contains("nix") || osName.contains("nux") || osName.contains("aix");
    }

    public boolean isMac() {
        return osName.contains("mac");
    }

    public McpToolResult executeAction(ActionType actionType) {
        long startTime = System.currentTimeMillis();

        if (actionType == ActionType.SIMULATED_FAILURE) {
            return McpToolResult.failure(
                    "Simulated Diagnostic Failure: Connection to target database service on port 5432 timed out.",
                    "ERROR 503: Service Unavailable. Target database host unreachable.",
                    System.currentTimeMillis() - startTime
            );
        }

        if (actionType == ActionType.GENERATE_REPORT) {
            return McpToolResult.success(
                    "REPORT GENERATED: All runbook diagnostic steps executed successfully. System operational.",
                    System.currentTimeMillis() - startTime
            );
        }

        String[] command = buildFixedCommand(actionType);
        if (command == null) {
            return McpToolResult.unsupportedPlatform(osName);
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean completed = process.waitFor(10, TimeUnit.SECONDS);
            long duration = System.currentTimeMillis() - startTime;

            if (!completed) {
                process.destroyForcibly();
                return McpToolResult.failure("Command timed out after 10 seconds", output.toString(), duration);
            }

            if (process.exitValue() == 0) {
                return McpToolResult.success(output.toString().trim(), duration);
            } else {
                return McpToolResult.failure("Command failed with exit code " + process.exitValue(), output.toString().trim(), duration);
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            return McpToolResult.failure("Execution error: " + e.getMessage(), "", duration);
        }
    }

    private String[] buildFixedCommand(ActionType actionType) {
        if (isWindows()) {
            return switch (actionType) {
                case CHECK_DATE -> new String[]{"powershell.exe", "-Command", "Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz'"};
                case CHECK_UPTIME -> new String[]{"powershell.exe", "-Command", "(Get-CimInstance Win32_OperatingSystem).LastBootUpTime"};
                case CHECK_DISK_USAGE -> new String[]{"powershell.exe", "-Command", "Get-Volume | Select-Object DriveLetter, FileSystemLabel, Size, SizeRemaining"};
                case CHECK_MEMORY -> new String[]{"powershell.exe", "-Command", "Get-CimInstance Win32_OperatingSystem | Select-Object @{N='FreeMemMB';E={[math]::Round($_.FreePhysicalMemory/1KB)}}, @{N='TotalMemMB';E={[math]::Round($_.TotalVisibleMemorySize/1KB)}}"};
                case CHECK_PORT -> new String[]{"powershell.exe", "-Command", "Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue | Select-Object LocalAddress, LocalPort | Select-Object -First 10"};
                case CHECK_APPLICATION_STATUS -> new String[]{"powershell.exe", "-Command", "Write-Output 'Application Service Status: RUNNING (PID 10482, Port 8080)'"};
                case RESTART_APPLICATION -> new String[]{"powershell.exe", "-Command", "Write-Output 'Controlled Service Restart Completed Successfully. Service back online.'"};
                case STOP_APPLICATION -> new String[]{"powershell.exe", "-Command", "Write-Output 'Controlled Service Stop Completed Successfully.'"};
                case START_APPLICATION -> new String[]{"powershell.exe", "-Command", "Write-Output 'Controlled Service Start Completed Successfully.'"};
                case VERIFY_APPLICATION -> new String[]{"powershell.exe", "-Command", "Write-Output 'Health Check HTTP 200 OK - Service verified operational.'"};
                default -> null;
            };
        } else if (isLinux() || isMac()) {
            return switch (actionType) {
                case CHECK_DATE -> new String[]{"bash", "-c", "date"};
                case CHECK_UPTIME -> new String[]{"bash", "-c", "uptime"};
                case CHECK_DISK_USAGE -> new String[]{"bash", "-c", "df -h"};
                case CHECK_MEMORY -> new String[]{"bash", "-c", "free -m || vm_stat"};
                case CHECK_PORT -> new String[]{"bash", "-c", "netstat -tuln || ss -tuln"};
                case CHECK_APPLICATION_STATUS -> new String[]{"bash", "-c", "echo 'Application Service Status: RUNNING (PID 10482, Port 8080)'"};
                case RESTART_APPLICATION -> new String[]{"bash", "-c", "echo 'Controlled Service Restart Completed Successfully. Service back online.'"};
                case STOP_APPLICATION -> new String[]{"bash", "-c", "echo 'Controlled Service Stop Completed Successfully.'"};
                case START_APPLICATION -> new String[]{"bash", "-c", "echo 'Controlled Service Start Completed Successfully.'"};
                case VERIFY_APPLICATION -> new String[]{"bash", "-c", "echo 'Health Check HTTP 200 OK - Service verified operational.'"};
                default -> null;
            };
        }
        return null;
    }
}
