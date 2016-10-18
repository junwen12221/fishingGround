package cn.lightFish.staticBlog;

import lombok.Value;

import java.text.Format;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import static cn.lightFish.staticBlog.K.simpleTemplate;

@Value
class PostSummary {
    final static Format df = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).toFormat();
    final static Format dfIso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.toFormat();
    String url;
    String title;
    LocalDateTime date;

    public static String toLink(String tpl, PostSummary ps) {
        Map<String, String> map = new HashMap<>();
        map.put("ps.url", ps.getUrl());
        map.put("ps.title", ps.getTitle());
        map.put("datetime", dfIso.format(ps.getDate()));
        map.put("time", df.format(ps.getDate()));
        return simpleTemplate(tpl, map);
    }
}