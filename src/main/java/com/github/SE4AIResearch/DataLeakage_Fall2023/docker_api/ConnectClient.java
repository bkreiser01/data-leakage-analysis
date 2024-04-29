package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

// Import DockerJavaAPI library

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * This class creates a Docker client to connect to the hosts docker daemon
 */
public class ConnectClient {

    private static DockerClient dockerClient;

    /**
     * Constructor for ConnectClient object
     * https://github.com/docker-java/docker-java/blob/main/docs/getting_started.md
     */
    public ConnectClient() {
        // Create a default client config
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        // Create a default http client
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(URI.create("npipe:////./pipe/docker_engine"))
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        // Create the docker client builder
        dockerClient = DockerClientBuilder.getInstance()
                .withDockerHttpClient(httpClient).build();
    }

    /**
     * Checks if the bkreiser/leakage-analysis image is available in docker
     *
     * @return boolean true if the image is on the machine false otherwise
     */
    public boolean checkImageOnMachine() {
        List<Image> images = dockerClient.listImagesCmd().exec();
        for (Image i : images) {
            if (i.getRepoTags()[0].equals("bkreiser01/leakage-analysis:latest"))
                return true;
        }
        return false;
    }

    /**
     * Pulls the bkreiser/leakage-analysis image from dockerhub
     *
     * @return true if pullled, false if not
     */
    public boolean pullImage() throws InterruptedException {
        List<SearchItem> items = dockerClient.searchImagesCmd("bkreiser01/leakage-analysis").exec();
        return dockerClient.pullImageCmd("bkreiser01/leakage-analysis")
                .withTag("latest")
                .exec(new PullImageResultCallback())
                .awaitCompletion(600, TimeUnit.SECONDS);
    }

    /**
     * Checks for the image and pulls if it is not on the machine
     *
     * @return Boolean - True on successful pull. False if failed
     * */
    public boolean checkThenPull() throws InterruptedException {
        if (!this.checkImageOnMachine()) {
            this.pullImage();
        }
        return true;
    }

    /**
     * This function creates and run the LAT docker container
     *
     * @param filePath - The path to the file to run the LAT on
     * @param fileName - The name of the file to run the LAT on
     * @return String containing the container ID
     */
    public Boolean runLeakageAnalysis(File filePath, String fileName) throws RuntimeException, InterruptedException {
        // Get the path to the file on the users machine
        final Boolean[] success = {true}; // Making this an array just so it can be accessed in final context

        String path2file = filePath.toString();
        List<String> commands = Arrays.asList("/execute/" + fileName, "-o");

        // Create the container
        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd("leakage")
                .withImage("bkreiser01/leakage-analysis")
                .withBinds(Bind.parse(path2file + ":/execute"))
                .withCmd(commands).exec();

        // Get the container's ID
        String containerId = createContainerResponse.getId();

        // Execute the container by ID
        dockerClient.startContainerCmd(containerId).exec();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        final String[] exceptionString = new String[1];

        ResultCallback<Frame> resultCallback = new ResultCallback<>() {
            @Override
            public void onStart(Closeable closeable) {
                success[0] = true;
            }

            @Override
            public void onNext(Frame object) {
                String objString = object.toString();
                if(objString.contains("SyntaxError")) {
                    success[0] = false;
                    exceptionString[0] = "Syntax Error";
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("YOU FOUND ME!");
                countDownLatch.countDown();
            }

            @Override
            public void onComplete() {
                countDownLatch.countDown();
            }

            @Override
            public void close() throws IOException {
                // Close any resources here
            }
        };

        dockerClient.attachContainerCmd(containerId)
              .withStdOut(true)
              .withStdErr(true)
              .withFollowStream(true)
              .exec(resultCallback);

        countDownLatch.await(120, TimeUnit.SECONDS); // Adjust timeout as per your requirement

        close(containerId);

        if (exceptionString[0] != null) {
            throw new RuntimeException(exceptionString[0]);
        }

        return success[0];
    }

    private ArrayList<String> getRunningContainers() {
        // Get all running docker containers
        List<Container> runningContainers = dockerClient.listContainersCmd().withStatusFilter(Collections.singleton("running")).exec();

        // Store all running containers into an arraylist
        ArrayList<String> runningContainerIds = new ArrayList<String>();
        runningContainers.forEach(container -> runningContainerIds.add(container.getId()));

        return runningContainerIds;
    }

    public void close(String containerId) throws InterruptedException {
        // If the container is running, wait until it stops running
        while (getRunningContainers().contains(containerId)) {
            TimeUnit.SECONDS.sleep(1);
        }

        // Remove the container
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
