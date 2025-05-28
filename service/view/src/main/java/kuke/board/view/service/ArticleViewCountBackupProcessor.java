package kuke.board.view.service;

import kuke.board.common.event.EventType;
import kuke.board.common.event.payload.ArticleViewedEventPayload;
import kuke.board.common.outboxmessagerelay.OutboxEventPublisher;
import kuke.board.view.entity.ArticleViewCount;
import kuke.board.view.repository.ArticleViewCountBackupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackupProcessor {
    private final ArticleViewCountBackupRepository articleViewCountBackupRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public void backup(Long articleId, Long viewCount) {
        int result = articleViewCountBackupRepository.updateViewCount(articleId, viewCount);
        if(result == 0) {
            articleViewCountBackupRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> { },
                            () -> articleViewCountBackupRepository.save(ArticleViewCount.init(articleId, viewCount))
                            );
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_VIEWED,
                ArticleViewedEventPayload.builder()
                        .articleId(articleId)
                        .articleViewCount(viewCount)
                        .build(),
                articleId
        );
    }
}
