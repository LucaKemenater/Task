package org.jhipster.task.repository;

import java.util.List;
import org.jhipster.task.domain.Posts;
import org.jhipster.task.domain.enumeration.PostStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Posts entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {
    @Query("select p from Posts p where p.status = 'PUBLISHED' or (p.status = 'DRAFT' and p.authorLogin = ?1)")
    List<Posts> findPublishedAndOwnDrafts(String currentUserLogin);
}
