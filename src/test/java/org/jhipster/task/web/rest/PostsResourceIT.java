package org.jhipster.task.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.jhipster.task.domain.PostsAsserts.*;
import static org.jhipster.task.web.rest.TestUtil.createUpdateProxyForBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.jhipster.task.IntegrationTest;
import org.jhipster.task.domain.Posts;
import org.jhipster.task.domain.enumeration.PostStatus;
import org.jhipster.task.repository.PostsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PostsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PostsResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_PUBLISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final PostStatus DEFAULT_STATUS = PostStatus.DRAFT;
    private static final PostStatus UPDATED_STATUS = PostStatus.PUBLISHED;

    private static final String DEFAULT_AUTHOR_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_LOGIN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostsMockMvc;

    private Posts posts;

    private Posts insertedPosts;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posts createEntity() {
        return new Posts()
            .title(DEFAULT_TITLE)
            .slug(DEFAULT_SLUG)
            .content(DEFAULT_CONTENT)
            .publishedAt(DEFAULT_PUBLISHED_AT)
            .status(DEFAULT_STATUS)
            .authorLogin(DEFAULT_AUTHOR_LOGIN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posts createUpdatedEntity() {
        return new Posts()
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .content(UPDATED_CONTENT)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .status(UPDATED_STATUS)
            .authorLogin(UPDATED_AUTHOR_LOGIN);
    }

    @BeforeEach
    void initTest() {
        posts = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPosts != null) {
            postsRepository.delete(insertedPosts);
            insertedPosts = null;
        }
    }

    @Test
    @Transactional
    void createPosts() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Posts
        var returnedPosts = om.readValue(
            restPostsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posts)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Posts.class
        );

        // Validate the Posts in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPostsUpdatableFieldsEquals(returnedPosts, getPersistedPosts(returnedPosts));

        insertedPosts = returnedPosts;
    }

    @Test
    @Transactional
    void createPostsWithExistingId() throws Exception {
        // Create the Posts with an existing ID
        posts.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posts)))
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        posts.setTitle(null);

        // Create the Posts, which fails.

        restPostsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posts)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        posts.setSlug(null);

        // Create the Posts, which fails.

        restPostsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posts)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublishedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        posts.setPublishedAt(null);

        // Create the Posts, which fails.

        restPostsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posts)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        posts.setStatus(null);

        // Create the Posts, which fails.

        restPostsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posts)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAuthorLoginIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        posts.setAuthorLogin(null);

        // Create the Posts, which fails.

        restPostsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posts)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPosts() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        // Get all the postsList
        restPostsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(posts.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].publishedAt").value(hasItem(DEFAULT_PUBLISHED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].authorLogin").value(hasItem(DEFAULT_AUTHOR_LOGIN)));
    }

    @Test
    @Transactional
    void getPosts() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        // Get the posts
        restPostsMockMvc
            .perform(get(ENTITY_API_URL_ID, posts.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(posts.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.publishedAt").value(DEFAULT_PUBLISHED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.authorLogin").value(DEFAULT_AUTHOR_LOGIN));
    }

    @Test
    @Transactional
    void getNonExistingPosts() throws Exception {
        // Get the posts
        restPostsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPosts() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the posts
        Posts updatedPosts = postsRepository.findById(posts.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPosts are not directly saved in db
        em.detach(updatedPosts);
        updatedPosts
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .content(UPDATED_CONTENT)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .status(UPDATED_STATUS)
            .authorLogin(UPDATED_AUTHOR_LOGIN);

        restPostsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPosts.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPosts))
            )
            .andExpect(status().isOk());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPostsToMatchAllProperties(updatedPosts);
    }

    @Test
    @Transactional
    void putNonExistingPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(put(ENTITY_API_URL_ID, posts.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posts)))
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(posts))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posts)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostsWithPatch() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the posts using partial update
        Posts partialUpdatedPosts = new Posts();
        partialUpdatedPosts.setId(posts.getId());

        partialUpdatedPosts.content(UPDATED_CONTENT).publishedAt(UPDATED_PUBLISHED_AT).authorLogin(UPDATED_AUTHOR_LOGIN);

        restPostsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPosts.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPosts))
            )
            .andExpect(status().isOk());

        // Validate the Posts in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostsUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPosts, posts), getPersistedPosts(posts));
    }

    @Test
    @Transactional
    void fullUpdatePostsWithPatch() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the posts using partial update
        Posts partialUpdatedPosts = new Posts();
        partialUpdatedPosts.setId(posts.getId());

        partialUpdatedPosts
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .content(UPDATED_CONTENT)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .status(UPDATED_STATUS)
            .authorLogin(UPDATED_AUTHOR_LOGIN);

        restPostsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPosts.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPosts))
            )
            .andExpect(status().isOk());

        // Validate the Posts in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostsUpdatableFieldsEquals(partialUpdatedPosts, getPersistedPosts(partialUpdatedPosts));
    }

    @Test
    @Transactional
    void patchNonExistingPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, posts.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(posts))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(posts))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(posts)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePosts() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the posts
        restPostsMockMvc
            .perform(delete(ENTITY_API_URL_ID, posts.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return postsRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Posts getPersistedPosts(Posts posts) {
        return postsRepository.findById(posts.getId()).orElseThrow();
    }

    protected void assertPersistedPostsToMatchAllProperties(Posts expectedPosts) {
        assertPostsAllPropertiesEquals(expectedPosts, getPersistedPosts(expectedPosts));
    }

    protected void assertPersistedPostsToMatchUpdatableProperties(Posts expectedPosts) {
        assertPostsAllUpdatablePropertiesEquals(expectedPosts, getPersistedPosts(expectedPosts));
    }
}
