package jp.toastkid.ratpack.models;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.toastkid.libs.tinysegmenter.TinySegmenter;

/**
 * Word cloud lib.
 *
 * @author Toast kid
 */
public class WordCloud {

    /** line separator. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** tiny segmenter. */
    private final TinySegmenter ts = TinySegmenter.getInstance();

    /**
     * count words in sentence.
     * @param sentence
     * @return
     */
    public String count(final String sentence) {
        final Map<String, Integer> data = new HashMap<>(100);
        ts.isAllowHiragana = false;
        ts.isAllowChar     = false;
        ts.isAllowNum      = false;
        final List<String> list = ts.segment(sentence);
        for (String item : list) {
            item = item.replaceAll("\\s", "");
            if (item.contains(LINE_SEPARATOR)) {
                continue;
            }
            int count = 1;
            if (data.containsKey(item)) {
                count = data.get(item) + count;
            }
            data.put(item, count);
        }
        return extract(data);
    }

    /**
     * extract string from data map.
     * @param data
     * @return
     */
    private String extract(final Map<String, Integer> data) {
        final StringBuilder dataStr = new StringBuilder();
        dataStr.append("[");
        if (!data.isEmpty()) {
            for (final String key : data.keySet()) {
                if (1 < dataStr.length()) {
                    dataStr.append(", ");
                }
                dataStr.append("{\"word\"")
                    .append(": ")
                    .append(doubleQuote(key))
                    .append(",")
                    .append("\"count\": ")
                    .append(doubleQuote(Integer.toString(data.get(key))))
                    .append("}");
            }
        }
        dataStr.append("]");
        return dataStr.toString();
    }

    /**
     * surround double quote.
     * @param str
     * @return
     */
    private static final String doubleQuote(final String str) {
        return "\"" + str + "\"";
    }

}