package fpm.dnu.org.controller;

import fpm.dnu.org.model.Tasks;
import fpm.dnu.org.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Mock
    TaskService taskService;
    @InjectMocks
    private TaskController taskController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void getAllTasksTest() throws Exception {
        Tasks task1 = new Tasks("1", "Task 1", "Description 1", LocalDate.now(), "active");
        Tasks task2 = new Tasks("2", "Task 2", "Description 2", LocalDate.now(), "done");
        when(taskService.findAll()).thenReturn(Arrays.asList(task1, task2));

        // Perform GET request
        mockMvc.perform(get("/api/task"))
                // Validate status code
                .andExpect(status().isOk())
                // Validate view name
                .andExpect(view().name("task-list"))
                // Validate model attribute
                .andExpect(model().attributeExists("taskItem"))
                // Validate model attribute value
                .andExpect(model().attribute("taskItem", Arrays.asList(task1, task2)));
    }

    @Test
    void getPageAddTest() throws Exception {
        mockMvc.perform(get("/api/task/add"))
                .andExpect(view().name("add-task"));
    }

    @Test
    void removeTaskTest() throws Exception {
        String taskId = "id1";
        doNothing().when(taskService).deleteById(taskId);

        // Perform GET request
        mockMvc.perform(get("/api/task/remove/{id}", taskId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/task"));
    }

    @Test
    void updatePageTaskTest() throws Exception {
        String taskId = "id1";
        Tasks task = new Tasks(taskId, "Task 1", "Description 1", LocalDate.now(), "active");
        when(taskService.findById(taskId)).thenReturn(Optional.of(task));

        // Perform GET request
        mockMvc.perform(get("/api/task/update/{id}", taskId))
                // Validate status code
                .andExpect(status().isOk())
                // Validate view name
                .andExpect(view().name("edit-task"))
                // Validate model attribute
                .andExpect(model().attributeExists("taskObj"))
                // Validate model attribute value
                .andExpect(model().attribute("taskObj", task));
    }

    @Test
    void updateTaskValidDueDateTest() throws Exception {
        String taskId = "1";
        String title = "Updated Task";
        String description = "Updated Description";
        LocalDate dueDate = LocalDate.now().plusDays(1);
        String status = "done";

        Optional<Tasks> existingTask = Optional.of(new Tasks(taskId, "Task 1", "Description 1", LocalDate.now(), "active"));
        when(taskService.findById(taskId)).thenReturn(existingTask);

        mockMvc.perform(post("/api/task/edit-task/{id}", taskId)
                        .param("id", taskId)
                        .param("title", title)
                        .param("description", description)
                        .param("dueDate", dueDate.toString())
                        .param("status", status))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/task"));

        verify(taskService, times(1)).update(existingTask.get());
    }

    @Test
    public void updateTaskInvalidDueDateTest() throws Exception {
        String taskId = "1";
        String title = "Updated Task";
        String description = "Updated Description";
        LocalDate dueDate = LocalDate.now().minusMonths(1);
        String status = "done";

        mockMvc.perform(post("/api/task/edit-task/{id}", taskId)
                        .param("id", taskId)
                        .param("title", title)
                        .param("description", description)
                        .param("dueDate", dueDate.toString())
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "Time isn't correct"));

        verify(taskService, never()).update(any());
    }


    @Test
    public void getTaskByStatusTest() throws Exception {
        String status = "active";
        Tasks task1 = new Tasks("1", "Task 1", "Description 1", LocalDate.now(), status);
        Tasks task2 = new Tasks("2", "Task 2", "Description 2", LocalDate.now(), status);
        when(taskService.findByStatus(status)).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(post("/api/task/search")
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(view().name("found-task"))
                .andExpect(model().attributeExists("foundTask"))
                .andExpect(model().attribute("foundTask", Arrays.asList(task1, task2)));
    }

    @Test
    public void getTaskByTitleTest() throws Exception {
        String title = "Task 1";
        Tasks task1 = new Tasks("1", "Task 1", "Description 1", LocalDate.now(), "active");
        Tasks task2 = new Tasks("2", "Task 2", "Description 2", LocalDate.now(), "done");
        when(taskService.findByTitle(title)).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(post("/api/task/search")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(view().name("found-task"))
                .andExpect(model().attributeExists("foundTask"))
                .andExpect(model().attribute("foundTask", Arrays.asList(task1, task2)));
    }

    @Test
    void addTaskValidDateTest() throws Exception {
        String title = "New Task";
        String description = "Description";
        LocalDate dueDate = LocalDate.now().plusDays(1);
        String status = "active";

        mockMvc.perform(post("/api/task/add")
                        .param("title", title)
                        .param("description", description)
                        .param("dueDate", dueDate.toString())
                        .param("status", status))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/task"));

        verify(taskService, times(1)).addTask(any(Tasks.class));
    }

    @Test
    void addTaskInvalidDateTest() throws Exception {
        String title = "New Task";
        String description = "Description";
        LocalDate dueDate = LocalDate.now().minusMonths(1);
        String status = "active";

        mockMvc.perform(post("/api/task/add")
                        .param("title", title)
                        .param("description", description)
                        .param("dueDate", dueDate.toString())
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "Time isn't correct"));

        verify(taskService, never()).addTask(any(Tasks.class));
    }
}
