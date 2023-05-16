package ai.openfabric.api.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="worker")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Worker extends Datable implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")

    public String id;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public List<Integer> getExposedPorts() {
        return exposedPorts;
    }

    public void setExposedPorts(List<Integer> exposedPorts) {
        this.exposedPorts = exposedPorts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name="name")
    public String name;

    @Column(name="image")
    public String image;

    @Column(name="container_id")
    public String containerId;

    // @Type(type = ExposedPortListType)
//	@Column(name="exposed_ports")

    @ElementCollection(targetClass=Integer.class)
    @CollectionTable(name = "worker_exposed_ports", joinColumns = @JoinColumn(name = "worker_id"))
    @Column(name = "exposed_port")
    public List<Integer> exposedPorts;

    @Column(name="status")
    public String status;





}
