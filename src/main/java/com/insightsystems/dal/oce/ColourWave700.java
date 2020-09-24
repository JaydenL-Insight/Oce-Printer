package com.insightsystems.dal.oce;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.snmp.SnmpEntry;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.BaseDevice;

import java.util.*;


public class ColourWave700 extends BaseDevice implements Monitorable {
    private final List<String> snmpOids = new ArrayList<String>(){{
        add(".1.3.6.1.2.1.25.3.2.1.3.1"); //DeviceType
        add(".1.3.6.1.2.1.1.3.0"); //Uptime
        add(".1.3.6.1.2.1.2.2.1.6.1"); //MacAddress
        add(".1.3.6.1.2.1.1.5.0"); //Ip Address
        add(".1.3.6.1.2.1.25.3.2.1.5.1"); //Device Status
        add(".1.3.6.1.2.1.25.3.2.1.6.1");//Device Errors Count
        add(".1.3.6.1.2.1.25.3.5.1.1.1"); // Printer Status
        add(".1.3.6.1.2.1.25.3.5.1.2.1");// Printer Errors
        add(".1.3.6.1.4.1.1552.21.3.1.1.5.3.0");//Total Toner Cyan
        add(".1.3.6.1.4.1.1552.21.3.1.1.5.4.0");
        add(".1.3.6.1.4.1.1552.21.3.1.1.5.5.0");
        add(".1.3.6.1.4.1.1552.21.3.1.1.5.6.0");//Total Toner Black
        add(".1.3.6.1.4.1.1552.21.3.1.1.5.7.0");//total printed m2
        add(".1.3.6.1.4.1.1552.21.3.1.1.5.8.0");//total printed length m
        add(".1.3.6.1.2.1.43.11.1.1.9.1.1"); //Toner Cyan
        add(".1.3.6.1.2.1.43.11.1.1.9.1.2");
        add(".1.3.6.1.2.1.43.11.1.1.9.1.3");
        add(".1.3.6.1.2.1.43.11.1.1.9.1.4"); //Toner Magenta
        add(".1.3.6.1.2.1.43.8.2.1.13.1.1");//Input Name 1 ie "Roll 1"
        add(".1.3.6.1.2.1.43.8.2.1.13.1.2");
        add(".1.3.6.1.2.1.43.8.2.1.13.1.3");
        add(".1.3.6.1.2.1.43.8.2.1.13.1.4");
        add(".1.3.6.1.2.1.43.8.2.1.13.1.5");
        add(".1.3.6.1.2.1.43.8.2.1.13.1.6");//Input Name 6
        add(".1.3.6.1.2.1.43.6.1.1.2.1.1"); //Cover Name 1
        add(".1.3.6.1.2.1.43.6.1.1.2.1.2");
        add(".1.3.6.1.2.1.43.6.1.1.2.1.3");//Cover Name 3
    }};
    private final List<String> snmpNames = new ArrayList<String>(){{
        add("DeviceType");
        add("Uptime");
        add("PhysicalAddress");
        add("IpAddress");
        add("DeviceStatus"); // INTEGER {unknown(1),running(2),warning(3),testing(4),down(5)}
        add("DeviceErrorsCount"); //INT CountOfErrors
        add("PrintStatus"); // INTEGER {other(1),unknown(2),idle(3),printing(4),warmup(5)}
        add("PrinterErrors"); //Special case (BIT level errors)
        add("TotalTonerUsageCyan"); //Grams
        add("TotalTonerUsageMagenta"); //Grams
        add("TotalTonerUsageYellow"); //Grams
        add("TotalTonerUsageBlack"); //Grams
        add("TotalPrintedArea"); //m2
        add("TotalPrintedLength"); //m
        add("TonerLevelCyan (%)");
        add("TonerLevelYellow (%)");
        add("TonerLevelBlack (%)");
        add("TonerLevelMagenta (%)");
        add("InputName1");
        add("InputName2");
        add("InputName3");
        add("InputName4");
        add("InputName5");
        add("InputName6");
        add("CoverDesc1");
        add("CoverDesc2");
        add("CoverDesc3");
    }};

    @Override
    public List<Statistics> getMultipleStatistics() throws Exception {
        ExtendedStatistics extStats = new ExtendedStatistics();
        Map<String,String> stats = new LinkedHashMap<>();
        Collection<SnmpEntry> snmpEntries = querySnmp(snmpOids);
        Iterator<SnmpEntry> snmpIt = snmpEntries.iterator();
        Iterator<String> nameIt = snmpNames.iterator();

        while (snmpIt.hasNext() && nameIt.hasNext()) {
            final String value = snmpIt.next().getValue();
            final String name = nameIt.next();

            System.out.println("[SNMP Entry] " + name + " : " + value);

            if (value.equals("noSuchObject") || value.equals("Request timed out")){
                continue;
            }

            switch(name){
                case "TotalTonerUsageCyan":
                case "TotalTonerUsageMagenta":
                case "TotalTonerUsageYellow":
                case "TotalTonerUsageBlack":
                    stats.put(name,value + " grams"); break;

                case "DeviceType":
                case "Uptime":
                case "DeviceErrorsCount":
                case "TonerLevelCyan (%)":
                case "TonerLevelYellow (%)":
                case "TonerLevelBlack (%)":
                case "TonerLevelMagenta (%)":
                case "PhysicalAddress":
                case "IpAddress":
                    stats.put(name,value);
                    break;

                case "TotalPrintedArea":   stats.put(name,value + " m²"); break;
                case "TotalPrintedLength": stats.put(name,value + " m"); break;

                case "DeviceStatus":
                    switch(value){
                        case "1": stats.put(name,"Unknown"); break;
                        case "2": stats.put(name,"Running"); break;
                        case "3": stats.put(name,"Warning"); break;
                        case "4": stats.put(name,"Testing"); break;
                        case "5": stats.put(name,"Down"   ); break;
                    }
                    break;

                case "PrintStatus":
                    switch (value){
                        case "1": stats.put(name,"Other"   ); break;
                        case "2": stats.put(name,"Unknown" ); break;
                        case "3": stats.put(name,"Idle"    ); break;
                        case "4": stats.put(name,"Printing"); break;
                        case "5": stats.put(name,"Warmup"  ); break;
                    }
                break;

                case "PrinterErrors": addToMap(stats,name,getBitLevelErrors(value));break;
                case "InputName1"   : addToMap(stats,value + " Media",querySnmp(new ArrayList<String>(){{add(".1.3.6.1.2.1.43.8.2.1.12.1.1");}}).iterator().next().getValue()); break;
                case "InputName2"   : addToMap(stats,value + " Media",querySnmp(new ArrayList<String>(){{add(".1.3.6.1.2.1.43.8.2.1.12.1.2");}}).iterator().next().getValue()); break;
                case "InputName3"   : addToMap(stats,value + " Media",querySnmp(new ArrayList<String>(){{add(".1.3.6.1.2.1.43.8.2.1.12.1.3");}}).iterator().next().getValue()); break;
                case "InputName4"   : addToMap(stats,value + " Media",querySnmp(new ArrayList<String>(){{add(".1.3.6.1.2.1.43.8.2.1.12.1.4");}}).iterator().next().getValue()); break;
                case "InputName5"   : addToMap(stats,value + " Media",querySnmp(new ArrayList<String>(){{add(".1.3.6.1.2.1.43.8.2.1.12.1.5");}}).iterator().next().getValue()); break;
                case "InputName6"   : addToMap(stats,value + " Media",querySnmp(new ArrayList<String>(){{add(".1.3.6.1.2.1.43.8.2.1.12.1.6");}}).iterator().next().getValue()); break;
                case "CoverDesc1"   : addToMap(stats,value + " Status",getCoverStatus(".1.3.6.1.2.1.43.6.1.1.3.1.1")); break;
                case "CoverDesc2"   : addToMap(stats,value + " Status",getCoverStatus(".1.3.6.1.2.1.43.6.1.1.3.1.2")); break;
                case "CoverDesc3"   : addToMap(stats,value + " Status",getCoverStatus(".1.3.6.1.2.1.43.6.1.1.3.1.3")); break;
                case "CoverDesc4"   : addToMap(stats,value + " Status",getCoverStatus(".1.3.6.1.2.1.43.6.1.1.3.1.4")); break;
                case "CoverDesc5"   : addToMap(stats,value + " Status",getCoverStatus(".1.3.6.1.2.1.43.6.1.1.3.1.5")); break;
                case "CoverDesc6"   : addToMap(stats,value + " Status",getCoverStatus(".1.3.6.1.2.1.43.6.1.1.3.1.6")); break;
            }
        }
        extStats.setStatistics(stats);
        return Collections.singletonList(extStats);
    }

    /**
     * Adds key value pair to HashMap if the value is not null
     * @param map Map to add values to
     * @param key
     * @param value
     */
    void addToMap(Map<String,String> map,String key,String value){
        if (value == null) return;
        map.put(key,value);
    }

    /**
     * Enumerates coverStatus number to matching String
     * @param oid oid for retrieving the state of the specific cover
     * @return Current state of the cover enumerated
     */
    String getCoverStatus(String oid) throws Exception {
        final SnmpEntry entry = querySnmp(new ArrayList<String>(){{add(oid);}}).iterator().next();
        switch (entry.getValue()){
            case "1": return "Other";
            case "3": return "Door Open";
            case "4": return "Door Closed";
            case "5": return "Interlock Open";
            case "6": return "Interlock Closed";
            default : return "Unknown";
        }
    }

    /**
     * Extract the errors encoded into the hex string

     * @param value String returned from 'DetectedErrorState' query. Format- 2 bytes as hex string separated by :
     * @return String representation of all errors or empty string
     *         Condition         Bit #
     *         lowPaper              0
     *         noPaper               1
     *         lowToner              2
     *         noToner               3
     *         doorOpen              4
     *         jammed                5
     *         offline               6
     *         serviceRequested      7
     *         inputTrayMissing      8
     *         outputTrayMissing     9
     *         markerSupplyMissing  10
     *         outputNearFull       11
     *         outputFull           12
     *         inputTrayEmpty       13
     *         overduePreventMaint  14
     */
    private String getBitLevelErrors(String value){
        final String[] stringBytes = value.split(":");
        byte[] bytes = new byte[2];
        for (int i = 0; i < 2; i++) {
            try {
                bytes[i] = (byte) Integer.parseInt(stringBytes[i], 16);
            } catch (Exception e){
                //Invalid return value from the printer
                return null;
            }
        }

        if ((bytes[0] | bytes[1]) == 0) {return "";}//if no bytes are 1 there are no errors

        String errors = "";
        //Start with the most significant bit of first byte and continue to lsb of second byte
        // 00000000 00000000
        // ^ ->
        if ((bytes[0] & (1<<7)) != 0) errors += "Low Paper\n";
        if ((bytes[0] & (1<<6)) != 0) errors += "No Paper\n";
        if ((bytes[0] & (1<<5)) != 0) errors += "Low Toner\n";
        if ((bytes[0] & (1<<4)) != 0) errors += "No Timer\n";
        if ((bytes[0] & (1<<3)) != 0) errors += "Door Open\n";
        if ((bytes[0] & (1<<2)) != 0) errors += "Jammed\n";
        if ((bytes[0] & (1<<1)) != 0) errors += "Offline\n";
        if ((bytes[0] & (1   )) != 0) errors += "Service Requested\n";
        //Second Byte
        if ((bytes[1] & (1<<7)) != 0) errors += "Input Tray Missing\n";
        if ((bytes[1] & (1<<6)) != 0) errors += "Output Tray Missing\n";
        if ((bytes[1] & (1<<5)) != 0) errors += "Marker Supply Missing\n";
        if ((bytes[1] & (1<<4)) != 0) errors += "Output Near Full\n";
        if ((bytes[1] & (1<<3)) != 0) errors += "Output Full\n";
        if ((bytes[1] & (1<<2)) != 0) errors += "Input Tray Empty\n";
        if ((bytes[1] & (1<<1)) != 0) errors += "Overdue Prev Maint\n";

        errors = errors.replaceFirst("\n$","");
        return errors;
    }

    public static void main(String[] args) throws Exception {
        ColourWave700 dev = new ColourWave700();
        dev.setHost("10.220.96.36");
        dev.setSnmpCommunity("public");
        dev.init();
        ((ExtendedStatistics)dev.getMultipleStatistics().get(0)).getStatistics().forEach((k,v)-> System.out.println(k + " : " + v));
    }
}
