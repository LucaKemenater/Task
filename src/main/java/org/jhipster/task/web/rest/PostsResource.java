package org.jhipster.task.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jhipster.task.domain.Posts;
import org.jhipster.task.domain.enumeration.PostStatus;
import org.jhipster.task.repository.PostsRepository;
import org.jhipster.task.security.SecurityUtils;
import org.jhipster.task.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.jhipster.task.domain.Posts}.
 */
@RestController
@RequestMapping("/api/posts")
@Transactional
public class PostsResource {

    private static final Logger LOG = LoggerFactory.getLogger(PostsResource.class);

    private static final String ENTITY_NAME = "posts";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostsRepository postsRepository;

    public PostsResource(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    /**
     * {@code POST  /posts} : Create a new posts.
     *
     * @param posts the posts to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new posts, or with status {@code 400 (Bad Request)} if the posts has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Posts> createPosts(@Valid @RequestBody Posts posts) throws URISyntaxException {
        LOG.debug("REST request to save Posts : {}", posts);
        if (posts.getId() != null) {
            throw new BadRequestAlertException("A new posts cannot already have an ID", ENTITY_NAME, "idexists");
        }
        posts = postsRepository.save(posts);
        return ResponseEntity.created(new URI("/api/posts/" + posts.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, posts.getId().toString()))
            .body(posts);
    }

    /**
     * {@code PUT  /posts/:id} : Updates an existing posts.
     *
     * @param id the id of the posts to save.
     * @param posts the posts to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated posts,
     * or with status {@code 400 (Bad Request)} if the posts is not valid,
     * or with status {@code 500 (Internal Server Error)} if the posts couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Posts> updatePosts(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Posts posts)
        throws URISyntaxException {
        LOG.debug("REST request to update Posts : {}, {}", id, posts);
        if (posts.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, posts.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        posts = postsRepository.save(posts);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, posts.getId().toString()))
            .body(posts);
    }

    /**
     * {@code PATCH  /posts/:id} : Partial updates given fields of an existing posts, field will ignore if it is null
     *
     * @param id the id of the posts to save.
     * @param posts the posts to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated posts,
     * or with status {@code 400 (Bad Request)} if the posts is not valid,
     * or with status {@code 404 (Not Found)} if the posts is not found,
     * or with status {@code 500 (Internal Server Error)} if the posts couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Posts> partialUpdatePosts(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Posts posts
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Posts partially : {}, {}", id, posts);
        if (posts.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, posts.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Posts> result = postsRepository
            .findById(posts.getId())
            .map(existingPosts -> {
                if (posts.getTitle() != null) {
                    existingPosts.setTitle(posts.getTitle());
                }
                if (posts.getSlug() != null) {
                    existingPosts.setSlug(posts.getSlug());
                }
                if (posts.getContent() != null) {
                    existingPosts.setContent(posts.getContent());
                }
                if (posts.getPublishedAt() != null) {
                    existingPosts.setPublishedAt(posts.getPublishedAt());
                }
                if (posts.getStatus() != null) {
                    existingPosts.setStatus(posts.getStatus());
                }
                if (posts.getAuthorLogin() != null) {
                    existingPosts.setAuthorLogin(posts.getAuthorLogin());
                }

                return existingPosts;
            })
            .map(postsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, posts.getId().toString())
        );
    }

    /**
     * {@code GET  /posts} : get all the posts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of posts in body.
     */
    @GetMapping("")
    public List<Posts> getAllPosts() {
        LOG.debug("REST request to get all Posts");
        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new RuntimeException("Current user login not found"));
        return postsRepository.findPublishedAndOwnDrafts(currentUserLogin);
    }

    /**
     * {@code GET  /posts/:id} : get the "id" posts.
     *
     * @param id the id of the posts to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the posts, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Posts> getPosts(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Posts : {}", id);
        Optional<Posts> posts = postsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(posts);
    }

    /**
     * {@code DELETE  /posts/:id} : delete the "id" posts.
     *
     * @param id the id of the posts to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosts(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Posts : {}", id);
        postsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
