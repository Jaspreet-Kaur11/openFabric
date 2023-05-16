package ai.openfabric.api.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WorkerStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String workerId;

    private Long cpuUsage;
    private Long memoryUsage;
    private Long networkUsage;

    // getters and setters
    // ...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }



    public Long getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Long long1) {
        this.cpuUsage = long1;
    }

    public Long getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Long memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Long getNetworkUsage() {
        return networkUsage;
    }

    public void setNetworkUsage(Long networkUsage) {
        this.networkUsage = networkUsage;
    }
}
