package kuke.board.article.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageLimitCalculator {
    public static Long calculatePageLimit(Long page, Long pageSize, Long movabePageCount) {
        return (((page - 1) / movabePageCount) + 1) * pageSize * movabePageCount + 1;
    }
}
