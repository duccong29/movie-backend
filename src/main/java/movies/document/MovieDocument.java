package movies.document;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "movies")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDocument {
    @Id
    String id;

    @Field(type = FieldType.Text)
    String title;

    @Field(type = FieldType.Text)
    String description;

    @Field(type = FieldType.Integer)
    Integer durationMinutes;

    @Field(type = FieldType.Date)
    LocalDate releaseDate;

    @Field(type = FieldType.Keyword)
    String posterUrl;

    @Field(type = FieldType.Keyword)
    String country;

    @Field(type = FieldType.Double)
    Double averageRating;

    @Field(type = FieldType.Keyword)
    Set<String> genres;

    @Field(type = FieldType.Date)
    LocalDate createdAt;

    @Field(type = FieldType.Date)
    LocalDate updatedAt;
}
