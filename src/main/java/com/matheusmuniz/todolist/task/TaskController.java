package com.matheusmuniz.todolist.task;

import com.matheusmuniz.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final String userIdName = "idUser";

    @Autowired
    private ITaskRepository taskRepository;
    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){

        var idUser = request.getAttribute(userIdName);

        taskModel.setId((UUID) idUser);

        var currentDate = LocalDateTime.now();

        boolean datesAreInconsistent = currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt());

        if(datesAreInconsistent)  {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início / término deve ser maior do que a data atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getStartAt()))  {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de término deve após a data de início");
        }

        TaskModel task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute(userIdName);
        return this.taskRepository.findByIdUser((UUID) idUser);

    }

    @PutMapping("/{id}")
    public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        var idUser =  request.getAttribute(userIdName);

        var task = this.taskRepository.findById(id).orElse(null);

        Utils.copyNonNullProperties(taskModel, task);

        taskModel.setId((UUID) idUser);
        taskModel.setId(id);

        return this.taskRepository.save(task);
    }
}
