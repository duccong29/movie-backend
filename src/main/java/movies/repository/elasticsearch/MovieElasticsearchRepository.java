package movies.repository.elasticsearch;

import movies.document.MovieDocument;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
public interface MovieElasticsearchRepository extends ElasticsearchRepository<MovieDocument, String> {
}
