package cloudsim;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;
import java.util.*;

public class CloudSimSetup {
    public static Datacenter createDatacenter(String name) {
        // ✅ FIX: Use CloudSim 3.0.3 supported classes
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000))); // ✅ Use PeProvisionerSimple

        List<Host> hostList = new ArrayList<>();
        int ram = 2048;  // 2 GB RAM
        long storage = 1000000;  // 1 TB Storage
        int bw = 10000;  // 10 Gbps Bandwidth

        Host host = new Host(
            0,
            new RamProvisionerSimple(ram),  // ✅ Use RamProvisionerSimple
            new BwProvisionerSimple(bw),   // ✅ Use BwProvisionerSimple
            storage,
            peList,
            new VmSchedulerTimeShared(peList) // ✅ Provide `peList`
        );

        hostList.add(host);

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            "x86", "Linux", "Xen",
            hostList,
            10.0,  // Time zone
            3.0,   // Cost per second
            0.05,  // Cost per memory
            0.1,   // Cost per storage
            0.1    // Cost per bandwidth
        );

        try {
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            throw new RuntimeException("[ERROR] Datacenter creation failed: " + e.getMessage());
        }
    }
}
