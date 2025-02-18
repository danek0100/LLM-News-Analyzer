package org.si.news.parser.service.parser;

import java.util.Map;
import org.springframework.stereotype.Component;


/**
 * Factory class to return the appropriate {@link NewsParser} based on the URL.
 * It holds a map of available parsers and uses URL matching to determine the correct one.
 */
@Component
public class ParserFactory {

    private final Map<String, NewsParser> parserMap;

    /**
     * Constructor that initializes the factory with available parsers.
     *
     * @param rbcParser RBC parser
     * @param tradingViewParser TradingView parser
     * @param alenkaCapitalParser Alenka Capital parser
     */
    public ParserFactory(RBCParser rbcParser,
                         TradingViewParser tradingViewParser,
                         AlenkaCapitalParser alenkaCapitalParser) {
        this.parserMap = Map.of(
            "rbc", rbcParser,
            "tradingview", tradingViewParser,
            "alenka", alenkaCapitalParser
        );
    }

    /**
     * Returns the appropriate parser for the given URL.
     * If no matching parser is found, an exception is thrown.
     *
     * @param url the URL for which a parser is needed
     * @return the corresponding {@link NewsParser} instance
     * @throws RuntimeException if no parser is found for the provided URL
     */
    public NewsParser getParser(String url) {
        return parserMap.entrySet().stream()
                .filter(entry -> url.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parser found for URL: " + url));
    }
}
