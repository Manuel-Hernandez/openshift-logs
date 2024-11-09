package org.acme.controller;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

import org.acme.service.FileService;

@Path("/logs")
@ApplicationScoped
public class OpenShiftLogs {

    @Inject
    FileService fileService;

    private final String apiUrl = "api.###########";
    private final String token = "#############";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getLogs() {
        System.out.println("iniciando logs......");

        Config config = new ConfigBuilder()
                .withMasterUrl(apiUrl)
                .withOauthToken(token)
                .build();

        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {
            List<Pod> pods = client.pods().list().getItems();
            System.out.println("total de pods " + pods.size());
            for (Pod pod : pods) {
                String podName = pod.getMetadata().getName();
                    System.out.println(podName);
                    pod.getSpec().getContainers().forEach(container -> {
                        String containerName = container.getName();    
                            try {
                                System.out.println("pod.getMetadata().getNamespace() " + pod.getMetadata().getNamespace());
                                System.out.println("podName " + podName);
                                System.out.println("Contenedor: " + containerName);
                                String logs = client.pods()
                                        .inNamespace(pod.getMetadata().getNamespace())
                                        .withName(podName)
                                        .inContainer(containerName)
                                        .getLog(); 

                                String fileName = podName+"+"+containerName;
                                fileService.createDirectoryWithTimestamp(fileName,logs);
                            } catch (KubernetesClientException e) {
                                System.out.println("Error al obtener logs de " + containerName + ": " + e.getMessage());
                            } catch (Exception e) {
                                System.out.println("Error inesperado: " + e.getMessage());
                            }
                        

                    });

                

            }
        }

        return "Iniciando logs";
    }

}