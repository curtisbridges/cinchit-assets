package com.curtisbridges.asset.io;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import com.curtisbridges.asset.ConsolidatedAsset;
import com.curtisbridges.util.StringUtils;


public class HtmlAssetWriter extends AbstractAssetWriter {
    private static final long KILOBYTE = 1024;
    private static final long MEGABYTE = 1024 * KILOBYTE;
    private static final long GIGABYTE = 1024 * MEGABYTE;
    private static final long TERABYTE = 1024 * GIGABYTE;
    
    private static final float VER_WINDOWS_SERVER_2008_R2 = 6.1760117514f;
    private static final float VER_WINDOWS_SERVER_2008 = 6.06f;
    private static final float VER_WINDOWS_SERVER_2003 = 5.23790f;
    private static final float VER_WINDOWS_8 = 6.2f;
    private static final float VER_WINDOWS_7 = 6.1f;
    private static final float VER_WINDOWS_VISTA = 6.0f;
    private static final float VER_WINDOWS_XP = 5.1f;
    private static final float VER_WINDOWS_2000 = 5.0f;
    private static final float VER_WINDOWS_ME = 4.9f;
    private static final float VER_WINDOWS_98 = 4.1f;
    private static final float VER_WINDOWS_NT = 4.0f;
    
    // Asset Name, Bios Name, User, Model, Serial, Ram, OS
    @Override
    protected String getString(ConsolidatedAsset asset) {
        String name = asset.getName();
        Map<String, String> props = asset.getProperties();
        
        StringBuffer buffer = new StringBuffer();
        
        String color = getColorString(asset);
        
        if(color.isEmpty())
            buffer.append("<tr>");
        else
            buffer.append("<tr style=\"background-color: " + color + ";\">");
        
        buffer.append("<td>");
        buffer.append(name);
        buffer.append("</td><td>");
        buffer.append(getProp(props, PROP_NETBIOS));
        buffer.append("</td><td>");
        buffer.append(getProp(props, PROP_USER));
        buffer.append("</td><td>");
        buffer.append(getProp(props, PROP_MODEL));
        buffer.append("</td><td>");
        buffer.append(getProp(props, PROP_SN));
        buffer.append("</td><td>");
        buffer.append(getProp(props, PROP_MEM));
        buffer.append("</td><td>");
        buffer.append(getProp(props, PROP_OS));
        buffer.append("</td></tr>\n");
        
        return buffer.toString();
    }

    @Override
    protected String getHeader() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html>\n");
        buffer.append("<title>Computer Services and Support Worcester MA | Cinch IT Inc.</title>\n");
        buffer.append("<table width=\"100%\">\n");
        buffer.append("<tr><td align=\"left\">");
        buffer.append("<img src=\"https://lh5.googleusercontent.com/-ISM4oB5WR0E/UMUuh7P4ToI/AAAAAAAAJ70/tCVIbHvfEiU/s222/image001.jpg\">");
        buffer.append("</td><td align=\"right\">");
        Date date = new Date();
        DateFormat fmt = DateFormat.getDateInstance();
        buffer.append("Reported: " + fmt.format(date));
        buffer.append("</td></tr>\n</table>\n");
        
        buffer.append("<table><tr>");
        for(String header : HEADERS) {
            buffer.append("<th>");
            buffer.append(header);
            buffer.append("</th>");
        }
        buffer.append("</tr>");
        return buffer.toString();
    }

    @Override
    protected String getFooter() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("</table>");
        buffer.append("<p align=\"center\" class=\"footernote\">Cinch IT offers Computer Service in Worcester, Westborough, Framingham, Natick, Shrewsbury, Marlborough, Auburn, Waltham, as well as all other areas in Massachusetts.</p>");
        buffer.append("</html>");
        
        return buffer.toString();
    }
    
    private double getMegaBytesOfRam(ConsolidatedAsset asset) {
        Map<String, String> props = asset.getProperties();
        if(props.containsKey(PROP_MEM)) {
            long multiplier = 1;
            
            String ram = asset.getProperties().get(PROP_MEM);
            ram = ram.toUpperCase();
            if(ram.endsWith("GB")) {
                multiplier = GIGABYTE;
            }
            else if(ram.endsWith("TB")) {
                multiplier = TERABYTE;
            }
            else if(ram.endsWith("MB")) {
                multiplier = MEGABYTE;
            }
            else if(ram.endsWith("KB"))
                multiplier = KILOBYTE;
            
            double value = Double.parseDouble(ram.substring(0, ram.length()-2));
            return value * multiplier;
        }
        else {
            return 0;
        }
    }
    
    private String getColorString(ConsolidatedAsset asset) {
        double ram = getMegaBytesOfRam(asset);
        if(isWindows(asset)) {
            float version = getWindowsVersion(asset);
            if(version < VER_WINDOWS_VISTA) {
                // Red = less than 2gb AND xp
                if(ram < (2 * GIGABYTE))
                    return "#FF6B6B";
                // orange = less than 3gb AND xp
                else if(ram < (3 * GIGABYTE))
                    return "orange";
                // yellow = 3gb+ AND XP
                else
                    return "#D4DB48";
            }
            else if(version > VER_WINDOWS_VISTA) {
                // green = 3gb + and win7
                if(ram >= (3 * GIGABYTE))
                    return "#6FC965";
                // blue = less than 3gb and Win 7    
                else
                    return "#6B93FF";
            }
            // not sure how it can get here, but flag it!
            return "pink";
        }
        else {
            return "";
        }
    }
    
    private boolean isWindows(ConsolidatedAsset asset) {
        String os = asset.getProperties().get(PROP_OS);
        if(os != null) {
            return os.indexOf("Windows") >= 0;
        }
        else {
            return false;
        }
    }
    
    private float getWindowsVersion(ConsolidatedAsset asset) {
        if(isWindows(asset)) {
            String os = asset.getProperties().get(PROP_OS);
            
            if(StringUtils.stringContainsSubstring(os, "Windows Server 2008 R2"))
                return VER_WINDOWS_SERVER_2008_R2;
            else if(StringUtils.stringContainsSubstring(os, "Windows Server 2008"))
                return VER_WINDOWS_SERVER_2008;
            else if(StringUtils.stringContainsSubstring(os, "Windows Server 2003"))
                return VER_WINDOWS_SERVER_2003;
            else if(StringUtils.stringContainsSubstring(os, "Windows 8"))
                return VER_WINDOWS_8;
            else if(StringUtils.stringContainsSubstring(os, "Windows 7"))
                return VER_WINDOWS_7;
            else if(StringUtils.stringContainsSubstring(os, "Windows Vista"))
                return VER_WINDOWS_7;
            else if(StringUtils.stringContainsSubstring(os, "Windows XP"))
                return VER_WINDOWS_XP;
            else if(StringUtils.stringContainsSubstring(os, "Windows 2000"))
                return VER_WINDOWS_2000;
            else if(StringUtils.stringContainsSubstring(os, "Windows ME"))
                return VER_WINDOWS_ME;
            else if(StringUtils.stringContainsSubstring(os, "Windows 98"))
                return VER_WINDOWS_98;
            else if(StringUtils.stringContainsSubstring(os, "Windows NT"))
                return VER_WINDOWS_NT;
            else
                return 1.0f;
        }
        else {
            return 0.0f;
        }
    }
}