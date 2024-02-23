package fpm.dnu.org.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("Tasks")
public class Tasks {
    @Id
    String id;
    String title;
    String description;
    LocalDate dueDate;
    String status;
}
