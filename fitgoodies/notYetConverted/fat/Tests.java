// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

package fat;

import fit.*;

import java.net.URL;
import java.io.*;

public class Tests extends Fixture {

    Parse heads;
    String page;

    public void doRows(Parse rows) {
        heads = rows.parts;
        super.doRows(rows.more);
    }

    public void doCell(Parse cell, int column) {
        switch (column) {
            case(0):
                page = cell.text();
                break;
            default:
                String language = heads.at(column).text();
                String runscript = (String) Frameworks.runscripts.get(language);
                performTest(cell, runscript, page);
        }
    }

    public void performTest(Parse cell, String runscript, String page) {
        if (runscript == null || runscript.equals("null")  || page.startsWith("?")) {
            ignore(cell);
            return;
        }
        String pageUrl = "http://fit.c2.com/wiki.cgi?" + page;
        String testUrl = runscript + pageUrl;
        try {
            String testResult = get(new URL(testUrl));
            Parse data = testResult.indexOf("<wiki>")>=0
                    ? new Parse(testResult, new String[]{"wiki", "td"}).parts
                    : new Parse(testResult, new String[]{"td"});
            Counts c = count(data);
            String message = anchor(c.right+"/"+c.wrong+"/"+c.exceptions+"&nbsp;", testUrl);
            cell.addToBody(message);
            if (c.right>0 && c.wrong==0 && c.exceptions==0) {
                right(cell);
            } else {
                wrong(cell);
                cell.addToBody(data.footnote());
            }
        } catch (Throwable e) {
            if (e.getMessage().indexOf("Can't find tag: td")>=0) {
                cell.addToBody("Can't parse <a href=\"" + testUrl + "\">page</a>");
                ignore(cell);
            } else {
                exception(cell, e);
            }
        }
    }

    protected String anchor(String body, String link) {
        return "<a href=\"" + link + "\">" + body + "</a>";
    }

    protected String get(URL url) throws IOException {
        InputStream stream =  (InputStream) url.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer(10000);
        for (String line; (line = reader.readLine()) != null; ) {
            buffer.append(line);
            buffer.append('\n');
        }
        return buffer.toString();
    }

    protected Counts count(Parse data) throws Exception {
        Counts counts = new Counts();
        while (data != null) {
            Color c = Color.parse(data);
            data = data.more;
            if (c == null)          {continue;}
            else if (c.isGreen())   {counts.right++;}
            else if (c.isRed())     {counts.wrong++;}
            else if (c.isYellow())  {counts.exceptions++;}
            else if (c.isGray())    {counts.ignores++;}
        }
        return counts;
    }

    public static class Color {

        int r,g,b;

        public Color (int r, int g, int b) {
            this.r=r; this.g=g; this.b=b;
        }

        public static Color parse (Parse cell) throws Exception {
            if (cell==null) {
                return null;
            } else {
                String pattern = " bgcolor=";
                String tag = cell.tag.toLowerCase();
                int index = tag.indexOf(pattern);
                if (index >= 0) {
                    try {
                        index += pattern.length();
                        if (tag.charAt(index) == '"') index++;
                        if (tag.charAt(index) == '#') index++;
                        String hex = tag.substring(index, index+6);
                        int rgb = Integer.parseInt(hex, 16);
                        return new Color(rgb>>16&255, rgb>>8&255, rgb&255);
                    } catch (Exception e) {
                        throw new Exception ("Can't parse bgcolor in: "+cell.tag);
                    }
                } else {
                    return null;
                }
            }
        }

        boolean isRed()     {return r > g && r > b;}
        boolean isGreen()   {return g > r && g > b;}
        boolean isYellow()  {return r > b && g > b;}
        boolean isGray()    {return r == b && g == b;}
    }

}
