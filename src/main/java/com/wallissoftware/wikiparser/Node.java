package com.wallissoftware.wikiparser;

import com.wallissoftware.byos.pack.UrlInfo;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class Node {

    private final Node parent;

    private final String name;

    private final StringBuilder text = new StringBuilder();

    private final List<Node> children = new ArrayList<>();

    public Node(Node parent, @NonNull String name) {
        this.parent = parent;
        this.name = name;
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(name).append(":");
        String textToString = text.toString().trim();
        if (!textToString.isEmpty()) {
            string.append(textToString);
        }
        if (!children.isEmpty()) {
            string.append("children:");
            string.append(children);
        }
        return string.toString();
    }

    Optional<UrlInfo> getUrlInfo() {
        if (!"page".equals(name)) {
            return Optional.empty();
        }
        final UrlInfo urlInfo = new UrlInfo();
        getChildText("title").ifPresent(title -> {

                    String[] titleWords = StringUtils.splitByCharacterTypeCamelCase(title);
                    if (titleWords.length > 0) {
                        titleWords[0] = StringUtils.capitalize(titleWords[0]);
                        for (String key: titleWords) {
                            urlInfo.addKeyWeight(key, 20L);
                        }
                        urlInfo.setUrl("https://en.wikipedia.org/wiki/" + title);
                        urlInfo.setTitle(StringUtils.join(titleWords, ' '));
                    }
                }
        );
        if (urlInfo.getTitle() == null) {
            return Optional.empty();
        } else {
            return Optional.of(urlInfo);
        }
    }

    private Optional<Node> getChild(String name) {
        return children.parallelStream().filter(n -> n.getName().equals(name)).findFirst();
    }

    private Optional<String> getChildText(String name) {
        return getChild(name).map(c -> c.getText().toString());
    }


}
