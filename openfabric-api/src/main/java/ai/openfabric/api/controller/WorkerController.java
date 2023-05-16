package ai.openfabric.api.controller;

import ai.openfabric.api.model.Worker;
import ai.openfabric.api.model.WorkerStatistics;
import ai.openfabric.api.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {


    @Autowired
    WorkerService workerService;



    @GetMapping("/workers")
    public List<Worker> listWorkers() {
        return workerService.listWorkers();
    }

    @PostMapping("/StartWorkers")
    public ResponseEntity<Worker> startWorker(@RequestBody Worker worker) {
        Worker workerObj  = workerService.startWorker(worker);
        return ResponseEntity.status(HttpStatus.CREATED).body(workerObj);
    }

    @GetMapping("/workers/{id}")
    public ResponseEntity<Worker> getWorker(@PathVariable String id) {
        Worker worker =  workerService.getWorker(id);
        return ResponseEntity.ok(worker);
    }



    @PutMapping("/workers/{id}/stop")
    public void stopWorker(@PathVariable String id) {
                workerService.stopWorker(id);
    }

    @PutMapping("/workers/{id}/start")
    public void startWorkerById(@PathVariable String id) {

        workerService.startWorkerById(id);
    }

    @GetMapping("/workers/{id}/stats")
    public ResponseEntity<WorkerStatistics> getWorkerStats(@PathVariable String id) {

        WorkerStatistics workerStatistics =  workerService.getWorkerStats(id);
        return ResponseEntity.ok(workerStatistics);
    }


}
