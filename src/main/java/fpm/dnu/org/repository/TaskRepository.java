package fpm.dnu.org.repository;

import fpm.dnu.org.model.Tasks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Tasks, String> {
    @Query("{title: ?0}")
    List<Tasks> findByTitle(String title);

    @Query("{status: ?0}")
    List<Tasks> findByStatus(String status);
}
