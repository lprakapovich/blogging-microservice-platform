package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Blog;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.lprakapovich.blog.publicationservice.util.BlogUtil.getBlog;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
class BlogRepositoryTest {

    @Autowired
    private BlogRepository blogRepository;

    @Test
    void whenFetchingBlogsBySearchCriteria_paginationWorksCorrectly() {

        Blog excludedResult = getBlog("result", "principal");

        Blog result1 = getBlog("result1", "principal1");
        Blog result2 = getBlog("result2", "principal2");
        Blog result3 = getBlog("result3", "principal3");

        Blog result4 = getBlog("result4", "principal4");
        Blog result5 = getBlog("result5", "principal5");
        Blog result6 = getBlog("result6", "principal6");

        Blog result7 = getBlog("result7", "principal7");
        Blog result8 = getBlog("result8", "principal8");
        Blog result9 = getBlog("result9", "principal9");

        Blog result10 = getBlog("result10", "principal10");
        Blog result11 = getBlog("result11", "principal11");
        Blog result12 = getBlog("result12", "principal12");

        blogRepository.saveAll(List.of(
                excludedResult,
                result1,
                result2,
                result3,
                result4,
                result5,
                result6,
                result7,
                result8,
                result9,
                result10,
                result11,
                result12
        ));

        String searchCriteria = "result";
        int pageSize = 3;

        List<Blog> page1 = blogRepository.findByCriteriaExcludingUsernameAndPageable(
                searchCriteria,
                "principal",
                pageSize,
                0 * pageSize
        );

        List<Blog> page2 = blogRepository.findByCriteriaExcludingUsernameAndPageable(
                searchCriteria,
                "principal",
                pageSize,
                1 * pageSize
        );

        List<Blog> page3 = blogRepository.findByCriteriaExcludingUsernameAndPageable(
                searchCriteria,
                "principal",
                pageSize,
                2 * pageSize
        );

        List<Blog> page4 = blogRepository.findByCriteriaExcludingUsernameAndPageable(
                searchCriteria,
                "principal",
                pageSize,
                3 * pageSize
        );

        assertThat(page1).containsExactly(result1, result2, result3);
        assertThat(page2).containsExactly(result4, result5, result6);
        assertThat(page3).containsExactly(result7, result8, result9);
        assertThat(page4).containsExactly(result10, result11, result12);
    }
}
