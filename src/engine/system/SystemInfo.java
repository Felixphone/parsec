package engine.system;

import engine.system.dataItems.enums.SystemType;

import java.util.ArrayList;

public class SystemInfo {

    SystemType systemType;

    private String hostName;
    private String osName;
    private String osVersion;
    private String osManufacturer;
    private String systemManufacturer;
    private String systemModel;
    private String systemArchitecture;

    public SystemInfo(SystemType systemType) {
        this.systemType = systemType;
    }

    public SystemType getSystemType() {
        return systemType;
    }

    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("System type: " + systemType);
        info.add("System name: ");
        return info;
    }

    @Override
    public String toString() {
        return "System type: " + systemType + " \n" +
                "System name: ";
    }
}
