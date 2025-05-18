package kuke.board.view.repository;

import kuke.board.view.entity.ArticleViewCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleViewCountBackupRepository extends JpaRepository<ArticleViewCount, Long> {

    @Query(
            value = "UPDATE article_view_count SET view_count = :viewCount " +
                    "WHERE article_id = :articleId " +
                    "AND view_count < :viewCount ", // 100, 200 -> 300 동시 요청올 시 더 낮은 수로 조회수가 집계될 수도 있음 : 이에 대한 방어 코드
            nativeQuery = true
    )
    @Modifying
    int updateViewCount(
            @Param("articleId") Long articleId,
            @Param("viewCount") Long viewCount
    );
}
