package engine.system;

import engine.system.dataItems.enums.SystemType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SystemManager {

    public static SystemInfo getSystemInfo() {
        return new SystemInfo(getSystemType());
    }

    private static SystemType getSystemType() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            return SystemType.WINDOWS;
        } else if (osName.startsWith("Linux") || osName.startsWith("FreeBSD") || osName.startsWith("SunOS") || osName.startsWith("Unix")) {
            return SystemType.LINUX;
        } else if (osName.startsWith("Mac OS X") || osName.startsWith("Darwin")) {
            return SystemType.MACOS;
        } else {
            return SystemType.UNKNOWN;
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("OS --> " + System.getProperty("os.name"));   //OS Name such as Windows/Linux

        System.out.println("JRE Architecture --> " + System.getProperty("sun.arch.data.model") + " bit.");       // JRE architecture i.e 64 bit or 32 bit JRE

        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "systeminfo");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        String result = getStringFromInputStream(p.getInputStream());

        System.out.println(result);
        if (result.contains("64"))
            System.out.println("OS Architecture --> is 64 bit");  //The OS Architecture
        else
            System.out.println("OS Architecture --> is 32 bit");

    }


    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}
