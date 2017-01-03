package jp.toastkid.libs.tinysegmenter;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 形態素分解のできる JavaScript ライブラリ
 * <a href="http://chasen.org/~taku/software/TinySegmenter/">TinySegmenter</a> の Java 移植版
 * <HR>
 * (130616) 先頭文字列とストップ文字とのマッチング処理追加<BR>
 * (130414) 条件次第でフィルタを実行するよう修正<BR>
 * (130404) 作成、遅くて使い物にならないので要改良<BR>
 * @author Toast kid
 *
 */
public final class TinySegmenter {
    /** バイアス */
    public static final int BIAS__ = -332;
    /** 学習スコアのマップ. */
    private final ScoreMap sMap;
    /** 結果に含めない文字の正規表現. */
    private final Pattern stopChars = Pattern.compile(
                "[。\\?\\*、,\\.．「」\\(\\)（）\\-\\[\\]\\|…・＜＞/_#%&/:『』]", Pattern.DOTALL);
    /** 文字種別の判別器. */
    private final CharacterClassifier cc;
    /** ひらがなを結果に含めるか否か */
    public boolean isAllowHiragana  = true;
    /** 1文字を結果に含めるか否か */
    public boolean isAllowChar      = true;
    /** 数値を結果に含めるか否か */
    public boolean isAllowNum       = true;
    /** このクラス唯一のインスタンス. */
    private static TinySegmenter ts = new TinySegmenter();;

    /**
     * init singleton instance.
     */
    private TinySegmenter(){
        this.cc   = new CharacterClassifier();
        this.sMap = new ScoreMap();
    }

    /**
     * return singleton instance.
     * @return
     */
    public static synchronized final TinySegmenter getInstance() {
        return ts;
    }

    /**
     * 引数の文字列を形態素分解し、Listに格納して返す。
     * <HR>
     * (130404) 作成開始<BR>
     * @param input
     * @return List
     */
    public List<String> segment(final String input){
        if (input == null || input.isEmpty()) {
            return null;
        }
        // 返却する形態素
        final List<String> result = new ArrayList<String>(100);
        final List<String> seg    = new ArrayList<String>(100);
        seg.add("B3");
        seg.add("B2");
        seg.add("B1");
        final List<String> ctype  = new ArrayList<String>(100);
        ctype.add("O");
        ctype.add("O");
        ctype.add("O");
        final String[] o = input.split("");
        for (int i = 0; i < o.length; i++) {
            seg.add(o[i]);
            ctype.add(cc.classify(o[i]));
        }
        seg.add("E1");
        seg.add("E2");
        seg.add("E3");
        ctype.add("O");
        ctype.add("O");
        ctype.add("O");
        final StringBuilder word = new StringBuilder(200);
        word.append(seg.get(3));
        String p1 = "U";
        String p2 = "U";
        String p3 = "U";
        for (int i = 4; i < seg.size() - 3; i++) {
            int score = BIAS__;
            final String w1 = seg.get(i - 3);
            final String w2 = seg.get(i - 2);
            final String w3 = seg.get(i - 1);
            final String w4 = seg.get(i);
            final String w5 = seg.get(i + 1);
            final String w6 = seg.get(i + 2);
            final String c1 = ctype.get(i  -3);
            final String c2 = ctype.get(i - 2);
            final String c3 = ctype.get(i - 1);
            final String c4 = ctype.get(i);
            final String c5 = ctype.get(i + 1);
            final String c6 = ctype.get(i + 2);
            // スコア計算
            score += score(sMap.UP1, p1);
            score += score(sMap.UP2, p2);
            score += score(sMap.UP3, p3);
            score += score(sMap.BP1, p1 + p2);
            score += score(sMap.BP2, p2 + p3);
            score += score(sMap.UW1, w1);
            score += score(sMap.UW2, w2);
            score += score(sMap.UW3, w3);
            score += score(sMap.UW4, w4);
            score += score(sMap.UW5, w5);
            score += score(sMap.UW6, w6);
            score += score(sMap.BW1, w2 + w3);
            score += score(sMap.BW2, w3 + w4);
            score += score(sMap.BW3, w4 + w5);
            score += score(sMap.TW1, w1 + w2 + w3);
            score += score(sMap.TW2, w2 + w3 + w4);
            score += score(sMap.TW3, w3 + w4 + w5);
            score += score(sMap.TW4, w4 + w5 + w6);
            score += score(sMap.UC1, c1);
            score += score(sMap.UC2, c2);
            score += score(sMap.UC3, c3);
            score += score(sMap.UC4, c4);
            score += score(sMap.UC5, c5);
            score += score(sMap.UC6, c6);
            score += score(sMap.BC1, c2 + c3);
            score += score(sMap.BC2, c3 + c4);
            score += score(sMap.BC3, c4 + c5);
            score += score(sMap.TC1, c1 + c2 + c3);
            score += score(sMap.TC2, c2 + c3 + c4);
            score += score(sMap.TC3, c3 + c4 + c5);
            score += score(sMap.TC4, c4 + c5 + c6);
            score += score(sMap.UQ1, p1 + c1);
            score += score(sMap.UQ2, p2 + c2);
            score += score(sMap.UQ3, p3 + c3);
            score += score(sMap.BQ1, p2 + c2 + c3);
            score += score(sMap.BQ2, p2 + c3 + c4);
            score += score(sMap.BQ3, p3 + c2 + c3);
            score += score(sMap.BQ4, p3 + c3 + c4);
            score += score(sMap.TQ1, p2 + c1 + c2 + c3);
            score += score(sMap.TQ2, p2 + c2 + c3 + c4);
            score += score(sMap.TQ3, p3 + c1 + c2 + c3);
            score += score(sMap.TQ4, p3 + c2 + c3 + c4);
            String p = "O";
            if (0 < score ) {
                if (isAllowWord(word.toString().trim())){
                    result.add(word.toString());
                }
                word.delete(0, word.length());
                p = "B";
            }
            p1 = p2;
            p2 = p3;
            p3 = p;
            word.append(seg.get(i));
        }
        //result.add(word.toString());
        return result;
    }

    /**
     * 対応するスコアを返す。
     * <HR>
     * (130406) 作成<BR>
     * @param str
     * @return score
     */
    public final int score(
            final Map<String,Integer> map,
            final String str
            ) {
        if (map.containsKey(str)){
            return map.get(str);
        }
        return 0;
    }

    /**
     * 結果に含めてよい形態素か否かを判定して返す。
     * <HR>
     * (130616) 先頭文字列とストップ文字とのマッチング処理追加<BR>
     * (130414) 作成<BR>
     * @param word
     * @return 結果に含める形態素なら true
     */
    private boolean isAllowWord(final String word) {
        if (!isAllowHiragana && "I".equals(cc.classify(word))){
            return false;
        }
        String firstChar = word;
        if (1 < word.length() ){
            firstChar = word.substring(0,1);
        }
        if (!isAllowChar
                && ( stopChars.matcher(firstChar).matches()) ){
            return false;
        }
        return !(word.toString() == null || word.toString().isEmpty());
    }

    /**
     * @param args
    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ
        final long start = System.currentTimeMillis();
        final TinySegmenter ts = new TinySegmenter();
        for (int i = 0; i < 100; i++) {
            ts.segment("松屋の牛めし30円引きです。");
        }
        System.out.println(ColleUtil.getStringFromList(new TinySegmenter().segment("松屋の牛めし30円引きです。"), "|"));
        System.out.println((System.currentTimeMillis() - start) + "[ms]");
    }
     */
}