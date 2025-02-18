package org.si.news.parser.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.si.news.parser.service.dto.ArticleDto;
import org.si.news.parser.service.dto.LinkDto;
import org.si.news.parser.service.service.LinkProcessingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@Slf4j
@RestController
@RequestMapping("/api/v1/parser")
@RequiredArgsConstructor
public class LinkProcessingController {

    private final LinkProcessingService linkProcessingService;

    /**
     * Endpoint for parsing the link and returning the articles.
     *
     * @param linkDto JSON containing the URL and lastParsedTime
     * @return a Flux of parsed articles
     */
    @PostMapping("/parse")
    public Flux<ArticleDto> parseLink(@RequestBody LinkDto linkDto) {
        return linkProcessingService.parseLink(linkDto)
            .doOnError(error -> log.error("Failed to parse link", error));
    }
}
