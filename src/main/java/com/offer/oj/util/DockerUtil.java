package com.offer.oj.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.offer.oj.domain.dto.CodeResultDTO;
import com.offer.oj.domain.enums.CodeStatusEnum;
import com.offer.oj.domain.enums.CodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DockerUtil {
    private static volatile DockerClient dockerClient;

    @Value("${docker.host}")
    private static String dockerHost;

    @Value("${docker.api.version}")
    private static String dockerApiVersion;

    @Value("${docker.cert.path}")
    private static String dockerCertPath;

    @Value("${docker.host.bind.source.path}")
    private static String configBindSourcePath;

    @Value("${docker.host.bind.target.path}")
    private static String configBindTargetPath;

    @Value("${docker.host.bind.source.port}")
    private static Integer configBindSourcePort;

    @Value("${docker.host.bind.target.port}")
    private static Integer configBindTargetPort;

    private static final Long Memory = 512 * 1024 * 1024L;

    private DockerUtil() {
    }

    public static DockerClient getDockerClientInstance() {
        Objects.requireNonNull(dockerHost, "Docker 主机地址不能为空.");
        Objects.requireNonNull(dockerApiVersion, "Docker API 版本不能为空.");
        if (dockerClient == null) {
            synchronized (DockerUtil.class) {
                if (dockerClient == null) {
                    dockerClient = createDockerClient(dockerHost, dockerApiVersion, dockerCertPath);
                }
            }
        }
        return dockerClient;
    }

    private static DockerClient createDockerClient(String dockerHost, String dockerApiVersion, String dockerCertPath) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withApiVersion(dockerApiVersion)
                .withDockerHost(dockerHost)
                .withDockerTlsVerify(true)
                .withDockerCertPath(dockerCertPath)
                .build();
        DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(1000)
                .connectionTimeout(Duration.ofSeconds(60))
                .responseTimeout(Duration.ofMinutes(30))
                .build();
        return DockerClientImpl.getInstance(config, httpClient);
    }


    /**
     * 获取所有 Docker 容器的信息
     *
     * @return 所有 Docker 容器的信息列表
     */
    public List<Container> listContainers() {
        log.info("开始获取所有 Docker 容器信息.");
        try {
            ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
            return listContainersCmd.exec();
        } catch (Exception e) {
            log.error("获取所有 Docker 容器信息失败: {}", e.getMessage());
            throw new RuntimeException("获取所有 Docker 容器信息失败: " + e.getMessage());
        }
    }

    private static CreateContainerResponse createContainers(CodeTypeEnum codeType) throws IllegalAccessException {
        DockerClient client = getDockerClientInstance();
        HostConfig hostConfig = HostConfig.newHostConfig().withRestartPolicy(RestartPolicy.noRestart());
        //绑定挂载数据卷和配置文件
        hostConfig.withBinds(new Bind(configBindTargetPath, new Volume(configBindSourcePath)));
        //端口绑定 参数说明： 宿主机端口 -> 容器端口
        hostConfig.withPortBindings(new PortBinding(Ports.Binding.bindPort(configBindSourcePort), ExposedPort.tcp(configBindTargetPort)));
        //内存配额 单位为Byte
        hostConfig.withMemory(Memory);
        switch (codeType) {
            case JAVA -> {
                return client.createContainerCmd("openjdk:11")
//                    .withName("Java")
                        .withUser("root")
                        .withCmd("javac", "/data/Main.java")
                        .withCmd("java", "-cp", "/data", "Main")
                        .withHostConfig(hostConfig)
                        .exec();
            }
            case PYTHON -> {
                return client.createContainerCmd("python:3.9")
//                        .withName("testpython2")
                        .withUser("root")
                        .withCmd("python3", "/data/1.py")
                        .withHostConfig(hostConfig)
                        .exec();
            }
            case C_PLUS_PLUS -> {
                return client.createContainerCmd("codenvy/cpp_gcc:latest")
//                        .withName("testcpp")
                        .withUser("root")
                        .withCmd("sh", "-c", "g++ -o /data/program /data/main.cpp && /data/program")
                        .withHostConfig(hostConfig)
                        .exec();
            }
            default -> throw new IllegalAccessException("Unexpected code type:" + codeType.getType());
        }
    }


    public static CodeResultDTO executeCodeAndGetResult(CodeTypeEnum codeType) throws IllegalAccessException {
        CodeResultDTO codeResult = new CodeResultDTO();
        CreateContainerResponse createContainerResponse;
        try {
            createContainerResponse = createContainers(codeType);
        } catch (Exception e) {
            throw new IllegalAccessException(e.getMessage());
        }
        String containerId = createContainerResponse.getId();
        StringBuilder result = new StringBuilder();
        WaitContainerResultCallback waitCallback = new WaitContainerResultCallback();

        // 监听容器输出
        ResultCallback<Frame> callback = new ResultCallback<>() {
            @Override
            public void onStart(Closeable closeable) {
            }

            @Override
            public void onNext(Frame item) {
                result.append(item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error" + throwable.toString());
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void close() {
            }
        };

        try {
            dockerClient.startContainerCmd(containerId).exec();
            long startTime = System.currentTimeMillis();
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(true)
                    .exec(callback);
            dockerClient.waitContainerCmd(containerId).exec(waitCallback);
            try {
                Integer exitCode = waitCallback.awaitStatusCode(3L, TimeUnit.SECONDS);
                if (exitCode == 0) {
                    // 函数执行成功
                    long endTime = System.currentTimeMillis();
                    codeResult.setCode(0);
                    codeResult.setStatus(CodeStatusEnum.ACCEPT.getStatus());
                    codeResult.setResult(String.valueOf(result));
                    codeResult.setTime(endTime-startTime+"ms");
                } else {
                    // 函数执行出错
                    codeResult.setCode(exitCode);
                    codeResult.setStatus(CodeStatusEnum.COMPILE_ERROR.getStatus());
                    codeResult.setResult(result.toString());
                }
            } catch (Exception e) {
                dockerClient.stopContainerCmd(containerId).exec();
                codeResult.setCode(-1);
                codeResult.setStatus(CodeStatusEnum.TIME_LIMIT_EXCEEDED.getStatus());
                codeResult.setTime(CodeStatusEnum.TIME_LIMIT_EXCEEDED.getLimit());
                throw new DockerClientException("Docker Client Exception: Time out\n", e);
            }
        } catch (Exception e) {
            throw new DockerClientException("Docker Client Exception:", e);
        } finally {
            dockerClient.removeContainerCmd(containerId).exec();
            return codeResult;
        }
    }
}
