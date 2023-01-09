package com.think.core.util.shell;

import com.think.core.util.IoUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class ShellUtils {

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    public static ShellResult exec(String command){
        return exec(new String[]{command}, false);
    }

    public static ShellResult exec(String[] commands, boolean isRoot) {
        if (commands == null || commands.length == 0) {
            return new ShellResult(-1, null, null);
        }
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) continue;
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            int result = process.waitFor();
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
            return new ShellResult(result, successMsg.toString(), errorMsg.toString());
        }catch (Exception e) {
            return new ShellResult(-1, null, e.getMessage());
        }finally {
            if (process != null){
                process.destroy();
            }
            IoUtils.close(os, successResult, errorResult);
        }
    }
}
