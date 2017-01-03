package jp.toastkid.ratpack.models;

import org.junit.Test;

/**
 * Word cloud lib.
 *
 * @author Toast kid
 */
public class WordCloudTest {

    /**
     * test.
     */
    @Test
    public void test_count() {
        System.out.println(new WordCloud().count("東京特許許可局"));
    }


}