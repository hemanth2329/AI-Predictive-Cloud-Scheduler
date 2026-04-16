package com.eco.cloud_dispatcher;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EcoSchedulingService {


    private int getMlPrediction(int hourOfDay) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String pythonUrl = "http://127.0.0.1:8001/api/predict?hour_of_day=" + hourOfDay;


            Map<String, Object> response = restTemplate.getForObject(pythonUrl, Map.class);

            if (response != null && response.containsKey("predicted_tasks")) {
                return (Integer) response.get("predicted_tasks");
            }
        } catch (Exception e) {
            System.out.println("⚠️ ML Service is down. Falling back to default traffic.");
        }
        return 500;
    }


    public String runIntelligentSimulation(int hourOfDay) {


        int predictedTasks = getMlPrediction(hourOfDay);


        int requiredVms = Math.max(1, predictedTasks / 500);

        CloudSimPlus simulation = new CloudSimPlus();
        Datacenter datacenter = createDatacenter(simulation);
        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);


        List<Vm> vmList = createAutoScaledVms(requiredVms);
        List<Cloudlet> cloudletList = createCloudlets(predictedTasks);

        broker.submitVmList(vmList);
        broker.submitCloudletList(cloudletList);

        simulation.start();

        double makespan = broker.getCloudletFinishedList().stream()
                .mapToDouble(Cloudlet::getFinishTime)
                .max().orElse(0.0);

        return String.format(
                "🧠 ML Predicted Tasks: %d | ⚡ Auto-Scaled VMs: %d | ⏱️ Final Makespan: %.2f seconds",
                predictedTasks, requiredVms, makespan
        );
    }


    private Datacenter createDatacenter(CloudSimPlus simulation) {
        List<Host> hostList = new ArrayList<>();

        for(int i = 0; i < 20; i++) {
            List<Pe> peList = new ArrayList<>();
            peList.add(new PeSimple(2000));
            hostList.add(new HostSimple(10000, 8000, 1000000, peList).setVmScheduler(new VmSchedulerTimeShared()));
        }
        return new DatacenterSimple(simulation, hostList);
    }


    private List<Vm> createAutoScaledVms(int vmCount) {
        List<Vm> list = new ArrayList<>();
        for (int i = 0; i < vmCount; i++) {
            list.add(new VmSimple(1000, 1).setRam(1024).setBw(1000).setSize(10000)
                    .setCloudletScheduler(new CloudletSchedulerSpaceShared()));
        }
        return list;
    }

    private List<Cloudlet> createCloudlets(int count) {
        List<Cloudlet> list = new ArrayList<>();
        for(int i=0; i<count; i++) {
            list.add(new CloudletSimple(4000, 1));
        }
        return list;
    }
}