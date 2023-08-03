package com.offer.oj.util.pack;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.offer.oj.domain.dto.CodeResultDTO;
import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.enums.CodeResultEnum;
import com.offer.oj.domain.enums.CodeStatusEnum;
import com.offer.oj.domain.enums.CodeTypeEnum;
import com.offer.oj.domain.enums.SeparatorEnum;
import com.offer.oj.util.JudgeUtil;
import com.offer.oj.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("${docker.host.bind.source.result.path}")
    private String configBindSourceResultPath;

    @Value("${docker.host.bind.target.result.path}")
    private String configBindTargetResultPath;

    @Value("${docker.host.bind.source.input.path}")
    private String configBindSourceInputPath;

    @Value("${docker.host.bind.target.input.path}")
    private String configBindTargetInputPath;

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
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withApiVersion(dockerApiVersion).withDockerHost(dockerHost).withDockerTlsVerify(true).withDockerCertPath(dockerCertPath).build();
        DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder().dockerHost(config.getDockerHost()).sslConfig(config.getSSLConfig()).maxConnections(1000).connectionTimeout(Duration.ofSeconds(60)).responseTimeout(Duration.ofMinutes(30)).build();
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

    private CreateContainerResponse createContainers(SubmitCodeDTO submitCodeDTO) {
        DockerClient client = getDockerClientInstance();
        HostConfig hostConfig = HostConfig.newHostConfig().withRestartPolicy(RestartPolicy.noRestart());
        //绑定挂载数据卷和配置文件
        hostConfig.withBinds(new Bind(configBindSourcePath, new Volume(configBindTargetPath)));
        //端口绑定 参数说明： 宿主机端口 -> 容器端口
        hostConfig.withPortBindings(new PortBinding(Ports.Binding.bindPort(configBindSourcePort), ExposedPort.tcp(configBindTargetPort)));
        //内存配额 单位为Byte
        hostConfig.withMemory(Memory);
        switch (submitCodeDTO.getType()) {
            case JAVA -> {
                return client.createContainerCmd("openjdk:11").withUser("root")
//                        .withCmd("bash", "-c", "javac " + fileWholeNameWithType
//                                + " && java -cp " + configBindTargetPath + " " + submitCodeDTO.getFileName()
//                                + " > " + fileWholeName + ".out"
//                        )
                        .withHostConfig(hostConfig).withTty(true).exec();
            }
            case PYTHON -> {
                return client.createContainerCmd("python:time").withUser("root")
//                        .withCmd("bash", "-c", "python3 " + fileWholeNameWithType + " > " + fileWholeName + ".out")
//                        .withCmd("bash", "-c","python3 /data/1.py")
                        .withHostConfig(hostConfig).withTty(true).exec();
            }
            case C_PLUS_PLUS -> {
                return client.createContainerCmd("cpp:time").withUser("root")
//                        .withCmd("bash", "-c", "g++ -o " + fileWholePath + "program " + fileWholeNameWithType
//                                + "&&" + fileWholePath + "program >" + fileWholeName + ".out"
//                        )
                        .withHostConfig(hostConfig).withTty(true).exec();
            }
            default -> throw new RuntimeException("Unexpected code type:" + submitCodeDTO.getType().getValue());
        }
    }


    public CodeResultDTO executeCodeAndGetResult(SubmitCodeDTO submitCodeDTO) {
        //组装代码路径
        String codeFileWholePath;
        if (submitCodeDTO.getIsResult()) {
            codeFileWholePath = configBindTargetResultPath;
        } else {
            codeFileWholePath = configBindTargetPath + submitCodeDTO.getAuthorId() + SeparatorEnum.SLASH.getSeparator() + "code" + SeparatorEnum.SLASH.getSeparator();
        }
        String codeFileWholeName = codeFileWholePath + submitCodeDTO.getFileName();
        String codeFileWholeNameWithType = codeFileWholeName + SeparatorEnum.DOT.getSeparator() + submitCodeDTO.getType().getValue();

        //组装输入路径
        String inputFileWholePath = configBindTargetInputPath + "question" + submitCodeDTO.getQuestionId() + SeparatorEnum.SLASH.getSeparator();
        //组装输出路径
        String outputFileWholePath = configBindTargetPath + submitCodeDTO.getAuthorId() + SeparatorEnum.SLASH.getSeparator() + "output" + SeparatorEnum.SLASH.getSeparator();
        //组装答案路径
        String resultFileWholePath = configBindSourceResultPath + "question" + submitCodeDTO.getQuestionId() + SeparatorEnum.SLASH.getSeparator();
        String resultCodeFileWholePath = resultFileWholePath + "code" + SeparatorEnum.SLASH.getSeparator();
        String resultOutputFileWholePath = resultFileWholePath + "output" + SeparatorEnum.SLASH.getSeparator();

        CodeResultDTO codeResult = new CodeResultDTO();
        codeResult.setFileName(submitCodeDTO.getFileName());

        //创建容器
        CreateContainerResponse createContainerResponse = createContainers(submitCodeDTO);
        String containerId = createContainerResponse.getId();
        StringBuilder logs = new StringBuilder();
        WaitContainerResultCallback waitCallback = new WaitContainerResultCallback();
        CountDownLatch latch = new CountDownLatch(1);

        // 监听容器输出
        ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<>() {
            @Override
            public void onStart(Closeable closeable) {
            }

            @Override
            public void onNext(Frame item) {
                System.out.println("next"+item.toString());
                logs.append(item.toString().replace("STDOUT:", ""));
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.toString());
            }

            @Override
            public void onComplete() {
                System.out.println("complete");
                latch.countDown();
//                log.info(String.valueOf(logs));
            }

            @Override
            public void close() {
            }
        };

        try {
            dockerClient.startContainerCmd(containerId).exec();
            CodeResultEnum resultEnum = CodeResultEnum.ACCEPT;
            for (int i = 0; i < 1; i++) {
                String outputFileName = i + ".out";
                String inputFileName = i + ".input";
                ExecCreateCmd execCreateCmd = dockerClient.execCreateCmd(containerId);
                execCreateCmd.withAttachStdout(true);
                execCreateCmd.withAttachStderr(true);
                execCreateCmd.withTty(true);
//                execCreateCmd.withCmd("bash", "-c", "time -p python3 /data/1.py </data/input/question1/input%s.txt >/data/%s.out".formatted(i + 1, i + 1));
                switch (submitCodeDTO.getType()) {
                    case PYTHON ->
//                            execCreateCmd.withCmd("bash", "-c", "/usr/bin/time -f \"Time: %Us, Memory: %MKB\" python3 " + codeFileWholeNameWithType + " < " + inputFileWholePath + inputFileName + " > " + outputFileWholePath + outputFileName);
                            execCreateCmd.withCmd("bash", "-c", "/usr/bin/time -f \"Time: %Us, Memory: %MKB\" python3 /data/2.py");
                    case C_PLUS_PLUS ->
//                            execCreateCmd.withCmd("bash", "-c", "g++ -o " + codeFileWholeName + "program " + codeFileWholeNameWithType + "&&" + codeFileWholePath + "program " + " < " + inputFileWholePath + inputFileName + " > " + outputFileWholePath + outputFileName);
                            execCreateCmd.withCmd("bash", "-c", "/usr/bin/time g++ -o /data/program /data/main.cpp && /data/program");
                    case JAVA ->
//                            execCreateCmd.withCmd("bash", "-c", "javac " + codeFileWholeNameWithType
//                                + " && java -cp " + configBindTargetPath + " " + submitCodeDTO.getFileName()
//                                + " > " + outputFileName);
                            execCreateCmd.withCmd("bash", "-c", "time javac /data/Main.java && java -cp /data/ Main");

                    default -> throw new RuntimeException("No Such Code Type" + submitCodeDTO.getType().getValue());
                }
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        dockerClient.execStartCmd(execCreateCmd.exec().getId()).withDetach(false).exec(callback).awaitCompletion();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
//                    dockerClient.waitContainerCmd(containerId).exec(waitCallback);
                }, ThreadPoolUtil.monitorThreadPool);
                try {
                    future.get(20, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    resultEnum = CodeResultEnum.TIME_LIMIT_EXCEEDED;
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    resultEnum = CodeResultEnum.RUNTIME_ERROR;
                }
                future.join();
//                if (!JudgeUtil.compareFiles(resultOutputFileWholePath + outputFileName, outputFileWholePath + outputFileName)) {
//                    resultEnum = CodeResultEnum.WRONG_ANSWER;
//                    break;
//                }
            }
//            Thread.sleep(5000);
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
            codeResult.setResult(resultEnum.getResult());
            return null;

//            try {
//                Integer exitCode = waitCallback.awaitStatusCode(3L, TimeUnit.SECONDS);
//                codeResult.setCode(exitCode);
//                if (exitCode == 0) {
//                    // 函数执行成功
//                    long endTime = System.currentTimeMillis();
//                    codeResult.setTime((int) (endTime - startTime));
//                    codeResult.setStatus(CodeStatusEnum.SUCCESS.getStatus());
//                } else {
//                    // 函数执行出错
//                    codeResult.setStatus(CodeStatusEnum.FAIL.getStatus());
//                    codeResult.setResult(CodeResultEnum.COMPILE_ERROR.getResult());
//                }
//            } catch (Exception e) {
//                dockerClient.stopContainerCmd(containerId).exec();
//                codeResult.setCode(-1);
//                codeResult.setStatus(CodeStatusEnum.FAIL.getStatus());
//                codeResult.setResult(CodeResultEnum.TIME_LIMIT_EXCEEDED.getResult());
//                codeResult.setTime(CodeResultEnum.TIME_LIMIT_EXCEEDED.getLimit());
//                log.error("Docker Client Exception: Time out");
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            codeResult.setStatus(CodeStatusEnum.FAIL.getStatus());
//            codeResult.setResult(CodeResultEnum.RUNTIME_ERROR.getResult());
//            log.error("Docker Client Exception: Unknown");
//            e.printStackTrace();
//        } finally {
//            try {
//                latch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            dockerClient.removeContainerCmd(containerId).exec();
//            if (codeResult.getStatus().equals(CodeStatusEnum.SUCCESS.getStatus())) {
//                String fileWholePath = configBindSourcePath + submitCodeDTO.getAuthorId() + SeparatorEnum.SLASH.getSeparator();
//                String fileWholeName = fileWholePath + submitCodeDTO.getFileName();
//                String resultName = configBindSourceResultPath + submitCodeDTO.getQuestionId() + SeparatorEnum.UNDERLINE.getSeparator() + submitCodeDTO.getType().getValue() + ".out";
//
//                if (submitCodeDTO.getIsResult()) {
//                    codeResult.setResult(CodeResultEnum.ACCEPT.getResult());
//                } else {
//                    try {
//                        if (JudgeUtil.compareFiles(fileWholeName + ".out", resultName)) {
//                            codeResult.setResult(CodeResultEnum.ACCEPT.getResult());
//                        } else {
//                            codeResult.setResult(CodeResultEnum.WRONG_ANSWER.getResult());
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        codeResult.setResult(CodeResultEnum.RUNNING.getResult());
//                    }
//                }
//            }
//        }
//        return codeResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeResult;
    }
}
