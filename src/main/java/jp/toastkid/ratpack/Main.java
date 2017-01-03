package jp.toastkid.ratpack;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import groovy.text.StreamingTemplateEngine;
import groovy.text.Template;
import jp.toastkid.ratpack.models.WordCloud;
import ratpack.exec.Promise;
import ratpack.form.Form;
import ratpack.handling.Chain;
import ratpack.http.MediaType;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;

/**
 * Word cloud web app powered by Ratpack.
 * @author Toast kid
 *
 */
public class Main {

    /** width. */
    private static final String WIDTH  = "900";

    /** height. */
    private static final String HEIGHT = "600";

    /** path to html template. */
    private static final String PATH_TO_TEMPLATE = "src/main/resources/wordcloud.html";

    /**
     * main method.
     * @param args
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        final Template template = new StreamingTemplateEngine()
                .createTemplate(Paths.get(PATH_TO_TEMPLATE).toFile());

        RatpackServer.start(server -> server
                .serverConfig(
                        ServerConfig.embedded()
                            .port(8940)
                            .onError(e -> e.printStackTrace())
                            .baseDir(BaseDir.find())
                            )
                .handlers(chain -> chain
                  .get("wc", context -> {
                      final Map<String, String> renderArgs = new HashMap<>();
                      renderArgs.put("wcData",        "");
                      renderArgs.put("paramSentence", null);
                      renderArgs.put("width",         WIDTH);
                      renderArgs.put("height",        HEIGHT);
                      context.getResponse()
                          .send(MediaType.TEXT_HTML, template.make(renderArgs).toString());
                  })
                  .post("wc/result", context -> {
                      final Map<String, String> renderArgs = new HashMap<>();
                      final Promise<Form> form = context.parse(Form.class);
                      form.then(f -> {
                          final String sentence = f.get("sentence");
                          if (sentence == null) {
                              context.getResponse()
                                  .send(MediaType.PLAIN_TEXT_UTF8, "sentence is null.");
                              return;
                          }
                          renderArgs.put("wcData",        new WordCloud().count(sentence));
                          renderArgs.put("paramSentence", sentence);
                          renderArgs.put("width",         WIDTH);
                          renderArgs.put("height",        HEIGHT);
                          context.getResponse()
                              .send(MediaType.TEXT_HTML, template.make(renderArgs).toString());
                        });
                  })
                  .prefix("public/images",
                          nested -> nested.fileSystem("public/images",      Chain::files))
                  .prefix("public/javascripts",
                          nested -> nested.fileSystem("public/javascripts", Chain::files))
                  .prefix("public/stylesheets",
                          nested -> nested.fileSystem("public/stylesheets", Chain::files))
                )
              );
    }
}