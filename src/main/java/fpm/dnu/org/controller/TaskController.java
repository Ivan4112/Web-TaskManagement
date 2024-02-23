package fpm.dnu.org.controller;

import fpm.dnu.org.model.Tasks;
import fpm.dnu.org.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/task")
    public String getAllTasks(Model model){
        List<Tasks> tasksList = taskService.findAll();
        model.addAttribute("taskItem", tasksList);
        return "task-list";
    }

    @GetMapping("/task/add")
    public String pageAdd(){
        return "add-task";
    }

    @GetMapping("/task/remove/{id}")
    public String removeTask(@PathVariable("id") String id){
        taskService.deleteById(id);
        return "redirect:/api/task";
    }

    @GetMapping("/task/update/{id}")
    public String updatePageTask(@PathVariable("id") String id, Model model){
        Optional<Tasks> tasksEdit = taskService.findById(id);
        model.addAttribute("taskObj", tasksEdit.get());
        return "edit-task";
    }

    @PostMapping("/task/edit-task/{id}")
    public String updateTask(@PathVariable("id") String id, Tasks updatedTask){
        if(LocalDate.now().isAfter(updatedTask.getDueDate())){
            throw new IllegalArgumentException("Time isn't correct");
        }else {
            Optional<Tasks> task = taskService.findById(id);
            Tasks newTask = task.get();
            newTask.setTitle(updatedTask.getTitle());
            newTask.setDescription(updatedTask.getDescription());
            newTask.setDueDate(updatedTask.getDueDate());
            newTask.setStatus(updatedTask.getStatus());
            taskService.update(newTask);
            return "redirect:/api/task";
        }
    }

    @PostMapping("/task/search")
    public String getTaskByTitle(@RequestParam(value = "title", required = false) String title,
                                 @RequestParam(value = "status", required = false) String status, Model model) {
        if(status != null) {
            if (status.equals("none")) {
                return "found-task";
            }
            model.addAttribute("foundTask", taskService.findByStatus(status));
        } else if (title != null) {
            model.addAttribute("foundTask", taskService.findByTitle(title));
        }
        return "found-task";
    }

    @PostMapping("/task/add")
    public String addTask(@RequestParam("title") String title,
                          @RequestParam("description") String description,
                          @RequestParam("dueDate") LocalDate dueDate,
                          @RequestParam("status") String status) {
        if(LocalDate.now().isAfter(dueDate)){
            throw new IllegalArgumentException("Time isn't correct");
        } else {
            Tasks newTask = new Tasks();
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setDueDate(dueDate);
            newTask.setStatus(status);
            taskService.addTask(newTask);
            return "redirect:/api/task";
        }
    }

    /*@GetMapping("/task")
    public ResponseEntity<?> getAllTask(){
        List<Tasks> list = taskService.findAll();
        if(list.size() > 0){
            return new ResponseEntity<>(list, HttpStatus.OK);
        }else {
            return new ResponseEntity<>("No available task", HttpStatus.NOT_FOUND);
        }
    }*/
}
