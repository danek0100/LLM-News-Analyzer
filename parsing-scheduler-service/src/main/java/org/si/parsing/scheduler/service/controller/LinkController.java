package org.si.parsing.scheduler.service.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.si.parsing.scheduler.service.model.Link;
import org.si.parsing.scheduler.service.service.LinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/links/")
public class LinkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);
    private final LinkService linkService;

    /**
     * Get all links
     * @return Flux of all links
     */
    @GetMapping
    public Flux<Link> getAllLinks() {
        return linkService.getAllLinks()
            .doOnSubscribe(subscription -> LOGGER.info("Fetching all links"));
    }

    /**
     * Get a specific link by URL
     * @param url The URL of the link to search
     * @return Mono of Link
     */
    @GetMapping("/search")
    public Mono<Link> getLink(@RequestParam @NotBlank String url) {
        if (url.isEmpty()) {
            return Mono.error(new IllegalArgumentException("URL must not be empty"));
        }
        return linkService.getLinkByUrl(url)
            .doOnSubscribe(subscription -> LOGGER.info("Fetching link with URL: {}", url))
            .onErrorResume(error -> Mono.error(new RuntimeException("Error fetching link: " + error.getMessage())));
    }

    /**
     * Add a new link to the system
     * @param url The URL to add
     * @return Mono of the created Link
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Link> addLink(@RequestParam @NotBlank String url) {
        return linkService.addLink(url)
            .doOnSubscribe(subscription -> LOGGER.info("Adding link with URL: {}", url))
            .onErrorResume(error -> Mono.error(new RuntimeException("Error adding link: " + error.getMessage())));
    }

    /**
     * Update the "lastChecked" timestamp of a link
     * @param url The URL of the link to update
     * @return Mono of the updated Link
     */
    @PutMapping
    public Mono<Link> updateLastChecked(@RequestParam @NotBlank String url) {
        return linkService.updateLastChecked(url)
            .doOnSubscribe(subscription ->
                LOGGER.info("Updating lastChecked for link with URL: {}", url))
            .onErrorResume(error ->
                Mono.error(new RuntimeException("Error updating lastChecked: " + error.getMessage())));
    }

    /**
     * Delete a link by URL
     * @param url The URL of the link to delete
     * @return Mono of Boolean indicating success or failure
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Boolean> deleteLink(@RequestParam @NotBlank String url) {
        return linkService.deleteLink(url)
            .doOnSubscribe(subscription -> LOGGER.info("Deleting link with URL: {}", url))
            .onErrorResume(error -> Mono.error(new RuntimeException("Error deleting link: " + error.getMessage())));
    }
}
