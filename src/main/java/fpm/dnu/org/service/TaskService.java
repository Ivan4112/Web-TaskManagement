package fpm.dnu.org.service;

import fpm.dnu.org.model.Tasks;
import fpm.dnu.org.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository repository;

    @Autowired
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public void addTask(Tasks tasks){
        repository.save(tasks);
    }

    public List<Tasks> findAll(){
        return repository.findAll();
    }

    public List<Tasks> findByTitle(String title){
        return repository.findByTitle(title);
    }
    public Optional<Tasks> findById(String id){
        return repository.findById(id);
    }

    public void update(Tasks newTask) {
        repository.save(newTask);
    }

    public void deleteById(String id){
        repository.deleteById(id);
    }

    public List<Tasks> findByStatus(String status) {
        return repository.findByStatus(status);
    }
}
