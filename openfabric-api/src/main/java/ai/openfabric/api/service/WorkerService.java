package ai.openfabric.api.service;

import ai.openfabric.api.model.Worker;
import ai.openfabric.api.model.WorkerStatistics;
import ai.openfabric.api.repository.WorkerRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Service
public class WorkerService {

    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("tcp://127.0.0.1:2375")
            .build();

    DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();
    DockerClient dockerClient = DockerClientBuilder.getInstance(config).withDockerHttpClient(httpClient)
            .build();


    @Autowired
    private WorkerRepository workerRepository;

    public List<Worker> listWorkers() {
        try {
            int page = 1;
            int pageSize = 10;
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            Page<Worker> workerPage = workerRepository.findAll(pageable);
            return workerPage.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public Worker startWorker(Worker worker) {
        CreateContainerResponse response = dockerClient.createContainerCmd(worker.getImage())
                .withName(worker.getName())
                .withExposedPorts(worker.getExposedPorts()
                        .stream()
                        .map(ExposedPort::tcp)
                        .collect(Collectors.toList()))
                        .exec();
        String containerId = response.getId();
        dockerClient.startContainerCmd(containerId).exec();
        worker.setContainerId(containerId);
        worker.setStatus("Running");
        return workerRepository.save(worker);
    }


    public Worker getWorker(String id) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            workerRepository.save(worker);
            return worker;
        } else {
            throw new NotFoundException("Worker not found");
        }
    }





    public void stopWorker(String id) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            dockerClient.stopContainerCmd(worker.getContainerId()).exec();
            worker.setStatus("stopped");
            workerRepository.save(worker);
        } else {
            throw new NotFoundException("Worker not found");
        }
    }

    public void startWorkerById(String id) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            dockerClient.startContainerCmd(worker.getContainerId()).exec();
            worker.setStatus("Running");
            workerRepository.save(worker);
        } else {
            throw new NotFoundException("Worker not found");
        }
    }


//    public WorkerStatistics getWorkerStats(String id) {
//        Optional<Worker> optionalWorker = workerRepository.findById(id);
//        if (optionalWorker.isPresent()) {
//            Worker worker = optionalWorker.get();
//
//

//           WorkerStatistics workerStats = new WorkerStatistics();
//            CountDownLatch latch = new CountDownLatch(1);
//
//            dockerClient.statsCmd(worker.getContainerId()).exec(new ResultCallback<Statistics>() {
//
//                @Override
//               public void onStart(Closeable closeable) {
////
////                    //System.out.println("Stat Fetch Started");
//                }
//
//                @Override
//                public void onNext(Statistics stats) {
//                    //System.out.println("Inside onNext to fetch stats");
//                   // WorkerStatistics workerStats = new WorkerStatistics();
//                    workerStats.setCpuUsage(stats.getCpuStats().getCpuUsage().getTotalUsage());
//                   // System.out.println("Got CPU Stats"+workerStats.getCpuUsage());
//                    workerStats.setMemoryUsage(stats.getMemoryStats().getUsage());
//                   // System.out.println("Got Memory Stats"+workerStats.getMemoryUsage());
//                    workerStats.setNetworkUsage(stats.getNetworks().get("eth0").getRxBytes());
//                   // System.out.println(("Got network stats"+workerStats.getNetworkUsage()));
//                    //workerStats.setTimestamp(stats.gett);
//                    workerStats.setWorkerId((worker.getId()));
//                    if(workerStats.getCpuUsage()!=null && workerStats.getMemoryUsage()!=null && workerStats.getNetworkUsage()!=null){
//                      this.onComplete();
//                        //latch.countDown();
//                    }
//
//
//
//                }
//
//
//
//                @Override
//                public void onError(Throwable throwable) {
//                    throw new RuntimeException("Failed to get worker stats", throwable);
//                }
//
//                @Override
//                public void onComplete() {
//                    System.out.println("Inside on  Complete");
//
//                    latch.countDown();
//                    try {
//                        this.close();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//                @Override
//                public void close() throws IOException {
//
//
//                }
//            });
//
//
//
//            try {
//                latch.await();
//            } catch (InterruptedException e) {
//                throw new RuntimeException("Interrupted while waiting for worker stats", e);
//            }
//
//            return workerStats;
//
//        } else {
//            throw new NotFoundException("Worker not found");
//        }
//    }

    public class StatisticsResultCallback extends ResultCallbackTemplate<StatisticsResultCallback, Statistics> {
        private WorkerStatistics workerStats;
        private CountDownLatch latch;
        private boolean receivedStatistics;
        public StatisticsResultCallback() {
            this.workerStats = new WorkerStatistics();
            this.latch = new CountDownLatch(1);
            this.receivedStatistics = false;
        }

        @Override
        public void onStart(Closeable closeable) {

        }


        @Override
        public void onNext(Statistics stats) {
            if (!receivedStatistics) {
                workerStats.setCpuUsage(stats.getCpuStats().getCpuUsage().getTotalUsage());
                workerStats.setMemoryUsage(stats.getMemoryStats().getUsage());
                workerStats.setNetworkUsage(stats.getNetworks().get("eth0").getRxBytes());

                if (workerStats.getCpuUsage() != null && workerStats.getMemoryUsage() != null
                        && workerStats.getNetworkUsage() != null) {
                    receivedStatistics = true; // Set the flag to true

                    latch.countDown();
                    try {
                        this.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }



    @Override
        public void onError(Throwable throwable) {
            throw new RuntimeException("Failed to get worker stats", throwable);
        }

        @Override
        public void onComplete() {

        }

        @Override
        public void close() throws IOException {
            System.out.println("Inside Close");

        }

        public WorkerStatistics getWorkerStats() {
            return workerStats;
        }

        public StatisticsResultCallback awaitCompletion() throws InterruptedException {
            latch.await();

            try {

                this.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }
    }

    public WorkerStatistics getWorkerStats(String id) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            WorkerStatistics workerStats = new WorkerStatistics();
            StatisticsResultCallback callback = new StatisticsResultCallback();

            try {
                dockerClient.statsCmd(worker.getContainerId()).exec(callback);
                callback.awaitCompletion();
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while waiting for worker stats", e);
            }

            return callback.getWorkerStats();
        } else {
            throw new NotFoundException("Worker not found");
        }
    }
}

