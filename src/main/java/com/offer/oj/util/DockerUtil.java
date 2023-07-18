package com.offer.oj.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.offer.oj.domain.dto.CodeResultDTO;
import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.enums.CodeStatusEnum;
import com.offer.oj.domain.enums.SeparatorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
@Component
public class DockerUtil {
    private static volatile DockerClient dockerClient;

    @Value("${docker.host}")
    private String dockerHost;

    @Value("${docker.api.version}")
    private String dockerApiVersion;

    @Value("${docker.cert.path}")
    private String dockerCertPath;

    @Value("${docker.host.bind.source.path}")
    private String configBindSourcePath;

    @Value("${docker.host.bind.target.path}")
    private String configBindTargetPath;

    @Value("${docker.host.bind.source.port}")
    private Integer configBindSourcePort;

    @Value("${docker.host.bind.target.port}")
    private Integer configBindTargetPort;

    private static final Long Memory = 512 * 1024 * 1024L;

    private DockerUtil() {
    }

    public DockerClient getDockerClientInstance() {
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

    private DockerClient createDockerClient(String dockerHost, String dockerApiVersion, String dockerCertPath) {
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

    private CreateContainerResponse createContainers(SubmitCodeDTO submitCodeDTO) throws IllegalAccessException {
        DockerClient client = getDockerClientInstance();
        HostConfig hostConfig = HostConfig.newHostConfig().withRestartPolicy(RestartPolicy.noRestart());
        //绑定挂载数据卷和配置文件
        hostConfig.withBinds(new Bind(configBindSourcePath, new Volume(configBindTargetPath)));
        //端口绑定 参数说明： 宿主机端口 -> 容器端口
        hostConfig.withPortBindings(new PortBinding(Ports.Binding.bindPort(configBindSourcePort), ExposedPort.tcp(configBindTargetPort)));
        //内存配额 单位为Byte
        hostConfig.withMemory(Memory);
        String fileWholePath = configBindTargetPath + submitCodeDTO.getAuthorId() + SeparatorEnum.SLASH.getSeparator();
        String fileWholeName = fileWholePath + submitCodeDTO.getFileName();
        String fileWholeNameWithType = fileWholeName + SeparatorEnum.DOT.getSeparator() + submitCodeDTO.getType().getValue();
        switch (submitCodeDTO.getType()) {
            case JAVA -> {
                return client.createContainerCmd("openjdk:11")
                        .withUser("root")
                        .withCmd("bash", "-c", "javac " + fileWholeNameWithType
                                + " && java -cp " + configBindTargetPath + " " + submitCodeDTO.getFileName()
                                + " > " + fileWholeName + ".out"
                        )
                        .withHostConfig(hostConfig)
                        .exec();
            }
            case PYTHON -> {
                return client.createContainerCmd("python:3.9")
                        .withUser("root")
                        .withCmd("bash", "-c", "python3 " + fileWholeNameWithType + " > " + fileWholeName + ".out")
                        .withHostConfig(hostConfig)
                        .exec();
            }
            case C_PLUS_PLUS -> {
                return client.createContainerCmd("codenvy/cpp_gcc:latest")
                        .withUser("root")
                        .withCmd("bash", "-c", "g++ -o " + fileWholePath + "program " + fileWholeNameWithType + "&&" + fileWholePath + "program >" + fileWholeName + " .out")
                        .withHostConfig(hostConfig)
                        .exec();
            }
            default -> throw new IllegalAccessException("Unexpected code type:" + submitCodeDTO.getType().getValue());
        }
    }


    public CodeResultDTO executeCodeAndGetResult(SubmitCodeDTO submitCodeDTO) throws IllegalAccessException, InterruptedException, IOException {
        CodeResultDTO codeResult = new CodeResultDTO();
        codeResult.setFileName(submitCodeDTO.getFileName());
        CreateContainerResponse createContainerResponse;
        try {
            createContainerResponse = createContainers(submitCodeDTO);
        } catch (Exception e) {
            throw new IllegalAccessException("Create containers exception" + e.getMessage());
        }
        String containerId = createContainerResponse.getId();
        StringBuilder result = new StringBuilder();
        WaitContainerResultCallback waitCallback = new WaitContainerResultCallback();
        CountDownLatch latch = new CountDownLatch(1);

        // 监听容器输出
        ResultCallback<Frame> callback = new ResultCallback<>() {
            @Override
            public void onStart(Closeable closeable) {
            }

            @Override
            public void onNext(Frame item) {
                result.append(item.toString().replace("STDOUT:", ""));
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error" + throwable.toString());
            }

            @Override
            public void onComplete() {
                latch.countDown();
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
                    codeResult.setTime(endTime - startTime + "ms");
                } else {
                    // 函数执行出错
                    codeResult.setCode(exitCode);
                    codeResult.setStatus(CodeStatusEnum.COMPILE_ERROR.getStatus());
                }
            } catch (Exception e) {
                dockerClient.stopContainerCmd(containerId).exec();
                codeResult.setCode(-1);
                codeResult.setStatus(CodeStatusEnum.TIME_LIMIT_EXCEEDED.getStatus());
                codeResult.setTime(CodeStatusEnum.TIME_LIMIT_EXCEEDED.getLimit());
                log.error("Docker Client Exception: Time out");
                e.printStackTrace();
            }
        } catch (Exception e) {
            codeResult.setStatus(CodeStatusEnum.RUNTIME_ERROR.getStatus());
            log.error("Docker Client Exception: Unknown");
            e.printStackTrace();
        } finally {
            latch.await();
            dockerClient.removeContainerCmd(containerId).exec();
            if (ObjectUtils.isEmpty(codeResult.getStatus())) {
                String fileWholePath = configBindTargetPath + submitCodeDTO.getAuthorId() + SeparatorEnum.SLASH.getSeparator();
                String fileWholeName = fileWholePath + submitCodeDTO.getFileName();
                String resultName = configBindTargetPath + "result/" + submitCodeDTO.getQuestionId() + ".txt";
                if (JudgeUtil.compareFiles(fileWholeName + ".out", resultName)) {
                    codeResult.setStatus(CodeStatusEnum.ACCEPT.getStatus());
                } else {
                    codeResult.setStatus(CodeStatusEnum.WRONG_ANSWER.getStatus());
                }
            }
        }
        return codeResult;
    }
}
