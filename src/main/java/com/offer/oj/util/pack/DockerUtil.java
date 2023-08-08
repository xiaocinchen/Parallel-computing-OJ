package com.offer.oj.util.pack;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.common.collect.ImmutableSet;
import com.offer.oj.domain.dto.CodeExecDTO;
import com.offer.oj.domain.dto.CodeResultDTO;
import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.enums.CodeResultEnum;
import com.offer.oj.domain.enums.CodeStatusEnum;
import com.offer.oj.domain.enums.CodeTypeEnum;
import com.offer.oj.domain.enums.SeparatorEnum;
import com.offer.oj.util.FileUtil;
import com.offer.oj.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.Duration;
import java.util.Set;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private final Pattern pattern = Pattern.compile("Time: (\\d+\\.\\d+)s, Memory: (\\d+)KB");


    private static final Set<CodeTypeEnum> NEED_COMPILE_CODE_TYPE_SET = ImmutableSet.of(
            CodeTypeEnum.C_PLUS_PLUS,
            CodeTypeEnum.JAVA
    );

    private static final Set<String> ERROR_COMPILE_KEYWORDS_SET = ImmutableSet.of(

    );

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
                return client.createContainerCmd("java:time").withUser("root")
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
        String codeFileWholePath, codeFileSourceWhilePath;
        if (submitCodeDTO.getIsResult()) {
            codeFileWholePath = configBindTargetResultPath
                    + "question" + submitCodeDTO.getQuestionId()
                    + SeparatorEnum.SLASH.getSeparator()
                    + "code" + SeparatorEnum.SLASH.getSeparator();
            codeFileSourceWhilePath = configBindSourceResultPath;
        } else {
            codeFileWholePath = configBindTargetPath + submitCodeDTO.getAuthorId() + SeparatorEnum.SLASH.getSeparator()
                    + "question" + submitCodeDTO.getQuestionId() + SeparatorEnum.SLASH.getSeparator()
                    + "code" + SeparatorEnum.SLASH.getSeparator();
            codeFileSourceWhilePath = codeFileWholePath.replace(configBindTargetPath, configBindSourcePath);
        }
//        String codeFileWholeName = codeFileWholePath + submitCodeDTO.getFileName();
//        String codeFileWholeNameWithType = codeFileWholeName + SeparatorEnum.DOT.getSeparator() + submitCodeDTO.getType().getValue();

        //组装输入路径
        String inputSourceFileWholePath = configBindSourceInputPath + "question" + submitCodeDTO.getQuestionId() + SeparatorEnum.SLASH.getSeparator();
        String inputFileWholePath = configBindTargetInputPath + "question" + submitCodeDTO.getQuestionId() + SeparatorEnum.SLASH.getSeparator();
        //组装输出路径
        String outputSourceFileWhilePath = configBindSourcePath + submitCodeDTO.getAuthorId() + SeparatorEnum.SLASH.getSeparator() + "question" + submitCodeDTO.getQuestionId() + "/output/";
        String outputFileWholePath = configBindTargetPath + submitCodeDTO.getAuthorId() + SeparatorEnum.SLASH.getSeparator() + "question" + submitCodeDTO.getQuestionId() + "/output/";
        //组装答案路径
        String resultTargetFileWholePath = configBindTargetResultPath + "question" + submitCodeDTO.getQuestionId() + SeparatorEnum.SLASH.getSeparator();
        String resultTargetCodeFileWholePath = resultTargetFileWholePath + "code" + SeparatorEnum.SLASH.getSeparator();
        String resultTargetOutputFileWholePath = resultTargetFileWholePath + "output" + SeparatorEnum.SLASH.getSeparator();

        String resultOutputFileWholePath = configBindSourceResultPath + "question" + submitCodeDTO.getQuestionId() + SeparatorEnum.SLASH.getSeparator() + "output" + SeparatorEnum.SLASH.getSeparator();

        CodeResultDTO codeResult = new CodeResultDTO();
        codeResult.setFileName(submitCodeDTO.getFileName());
        codeResult.setAcNumber(0);
        codeResult.setTestNumber(0);

        //创建容器
        CreateContainerResponse createContainerResponse = createContainers(submitCodeDTO);
        String containerId = createContainerResponse.getId();
//        WaitContainerResultCallback waitCallback = new WaitContainerResultCallback();
        CodeExecDTO codeExecDTO;
        if (submitCodeDTO.getIsResult()) {
            codeExecDTO = new CodeExecDTO(containerId, submitCodeDTO.getType(), submitCodeDTO.getFileName(), resultTargetCodeFileWholePath, inputFileWholePath, resultTargetOutputFileWholePath);
        } else {
            codeExecDTO = new CodeExecDTO(containerId, submitCodeDTO.getType(), submitCodeDTO.getFileName(), codeFileWholePath, inputFileWholePath, outputFileWholePath);
        }

        try {
            dockerClient.startContainerCmd(containerId).exec();
            //先编译
            if (NEED_COMPILE_CODE_TYPE_SET.contains(submitCodeDTO.getType())) {
                StringBuilder compileOutput = new StringBuilder();
                AtomicReference<CompletableFuture<Void>> compileFutureRef = new AtomicReference<>();
                compileFutureRef.set(CompletableFuture.runAsync(() -> {
                    try {
                        dockerClient.execStartCmd(getCompileCmdId(codeExecDTO)).withDetach(false).exec(new ResultCallback.Adapter<Frame>() {
                            @Override
                            public void onNext(Frame item) {
                                compileOutput.append(item);
                            }

                            @Override
                            public void onComplete() {
                                compileFutureRef.get().complete(null);
                            }
                        }).awaitCompletion();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }));
                try {
                    compileFutureRef.get().get(50, TimeUnit.SECONDS);
                } catch (Exception e) {
                    codeResult.setStatus(CodeStatusEnum.FAIL.getStatus());
                    codeResult.setResult(CodeResultEnum.COMPILE_ERROR.getResult());
                    codeResult.setStatus(CodeStatusEnum.FAIL.getStatus());
                    return codeResult;
                }
                if (compileOutput.toString().contains("STDERR")) {
                    codeResult.setStatus(CodeStatusEnum.FAIL.getStatus());
                    codeResult.setResult(CodeResultEnum.COMPILE_ERROR.getResult());
                    codeResult.setStatus(CodeStatusEnum.FAIL.getStatus());
                    codeResult.setError(compileOutput.toString());
                    return codeResult;
                }
            }
            codeResult.setStatus(CodeStatusEnum.SUCCESS.getStatus());

            File[] inputFiles = FileUtil.getDir(inputSourceFileWholePath).listFiles();
            FileUtil.getDir(outputSourceFileWhilePath);
            FileUtil.getDir(codeFileSourceWhilePath);
            FileUtil.getDir(resultOutputFileWholePath);
            assert inputFiles != null;
            codeResult.setResult(CodeResultEnum.ACCEPT.getResult());
            codeResult.setTestNumber(inputFiles.length);
            int acNumber = 0;
            for (File inputFile : inputFiles) {
                if (!inputFile.isFile()) {
                    continue;
                }
                String outputFileName;
                if (submitCodeDTO.getIsResult()){
                    outputFileName = inputFile.getName().split("\\.")[0] + ".out";
                }else {
                    outputFileName = submitCodeDTO.getFileName() + SeparatorEnum.UNDERLINE.getSeparator() + inputFile.getName().split("\\.")[0] + ".out";
                }
                String resultFileName = inputFile.getName().split("\\.")[0] + ".out";
                String inputFileName = inputFile.getName();
//                execCreateCmd.withCmd("bash", "-c", "time -p python3 /data/1.py </data/input/question1/input%s.txt >/data/%s.out".formatted(i + 1, i + 1));
                StringBuilder execOutput = new StringBuilder();
                AtomicReference<CompletableFuture<Void>> execFutureRef = new AtomicReference<>();
                execFutureRef.set(CompletableFuture.runAsync(() -> {
                    try {
                        dockerClient.execStartCmd(getExecCodeCmdId(codeExecDTO, inputFileName, outputFileName)).withDetach(false).exec(new ResultCallback.Adapter<Frame>() {
                            @Override
                            public void onNext(Frame frame) {
                                log.info(frame.toString());
                                execOutput.append(frame);
                            }

                            @Override
                            public void onComplete() {
                                execFutureRef.get().complete(null);
                            }
                        }).awaitCompletion();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
                try {
                    execFutureRef.get().get(50, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    codeResult.setResult(CodeResultEnum.TIME_LIMIT_EXCEEDED.getResult());
                    codeResult.setTime(2000);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    codeResult.setResult(CodeResultEnum.RUNTIME_ERROR.getResult());
                    codeResult.setError(execOutput.toString());
                    break;
                }
                if (execOutput.toString().contains("Exception")) {
                    codeResult.setResult(CodeResultEnum.RUNTIME_ERROR.getResult());
                    codeResult.setError(execOutput.toString());
                    break;
                }

                Matcher matcher = pattern.matcher(execOutput.toString());
                if (matcher.find()) {
                    int time = (int) (Double.parseDouble(matcher.group(1)) * 1000);
                    int memory = Integer.parseInt(matcher.group(2));
                    if (time > 2000) {
                        codeResult.setResult(CodeResultEnum.TIME_LIMIT_EXCEEDED.getResult());
                        codeResult.setTime(2000);
                        break;
                    } else {
                        if (codeResult.getTime() == null || time > codeResult.getTime()) {
                            codeResult.setTime(time);
                        }
                    }
                    //256 * 1024 KB
                    if (memory > 262144) {
                        codeResult.setMemory(262144);
                        codeResult.setResult(CodeResultEnum.MEMORY_LIMIT_EXCEEDED.getResult());
                        break;
                    } else {
                        if (codeResult.getMemory() == null || memory > codeResult.getMemory()) {
                            codeResult.setMemory(memory);
                        }
                    }
                } else {
                    throw new RuntimeException("No time and Memory" + submitCodeDTO.getFileName() + execOutput);
                }
                if (!submitCodeDTO.getIsResult()) {
                    if (!FileUtil.compareFiles(resultOutputFileWholePath + resultFileName, outputSourceFileWhilePath + outputFileName)) {
                        codeResult.setResult(CodeResultEnum.WRONG_ANSWER.getResult());
                        break;
                    }
                }
                acNumber++;
            }
            codeResult.setAcNumber(acNumber);
            if (submitCodeDTO.getIsResult()) {
                codeResult.setResult(CodeResultEnum.ACCEPT.getResult());
                codeResult.setStatus(CodeStatusEnum.SUCCESS.getStatus());
            }
            return codeResult;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
        }
        return codeResult;
    }

    private String getCompileCmdId(CodeExecDTO codeExecDTO) {

        ExecCreateCmd execCreateCmd = dockerClient.execCreateCmd(codeExecDTO.getContainerId());
        execCreateCmd.withAttachStderr(true).withAttachStdout(true).withTty(true);
        switch (codeExecDTO.getCodeTypeEnum()) {
            case C_PLUS_PLUS ->
                    execCreateCmd.withCmd("bash", "-c", "g++ -o " + codeExecDTO.getCodeFileWholeName() + " " + codeExecDTO.getCodeFileWholeNameWithType());
            case JAVA -> execCreateCmd.withCmd("bash", "-c", "javac " + codeExecDTO.getCodeFileWholeNameWithType());
            default -> throw new RuntimeException("No such Code Type" + codeExecDTO.getCodeTypeEnum().getValue());
        }
        return execCreateCmd.exec().getId();
    }

    private String getExecCodeCmdId(CodeExecDTO codeExecDTO, String inputFileName, String outputFileName) {
        ExecCreateCmd execCreateCmd = dockerClient.execCreateCmd(codeExecDTO.getContainerId());
        execCreateCmd.withAttachStderr(true).withAttachStdout(true).withTty(true);
        switch (codeExecDTO.getCodeTypeEnum()) {
            case PYTHON ->
//                            execCreateCmd.withCmd("bash", "-c", "/usr/bin/time -f \"Time: %Us, Memory: %MKB\" python3 " + codeFileWholeNameWithType + " < " + inputFileWholePath + inputFileName + " > " + outputFileWholePath + outputFileName);
                    execCreateCmd.withCmd("bash", "-c", "/usr/bin/time -f \"Time: %Us, Memory: %MKB\" python3 "
                            + codeExecDTO.getCodeFileWholeNameWithType()
                            + " < " + codeExecDTO.getInputFileWholePath() + inputFileName
                            + " > " + codeExecDTO.getOutputFileWholePath() + outputFileName);
            case C_PLUS_PLUS ->
//                            execCreateCmd.withCmd("bash", "-c", "g++ -o " + codeFileWholeName + "program " + codeFileWholeNameWithType + "&&" + codeFileWholePath + "program " + " < " + inputFileWholePath + inputFileName + " > " + outputFileWholePath + outputFileName);
                    execCreateCmd.withCmd("bash", "-c", "/usr/bin/time -f \"Time: %Us, Memory: %MKB\" "
                            + codeExecDTO.getCodeFileWholePath()
                            + codeExecDTO.getFileName()
                            + SeparatorEnum.SPACE.getSeparator()
                            + " < " + codeExecDTO.getInputFileWholePath() + inputFileName
                            + " > " + codeExecDTO.getOutputFileWholePath() + outputFileName);
            case JAVA ->
//                            execCreateCmd.withCmd("bash", "-c", "javac " + codeFileWholeNameWithType
//                                + " && java -cp " + configBindTargetPath + " " + submitCodeDTO.getFileName()
//                                + " > " + outputFileName);
                    execCreateCmd.withCmd("bash", "-c", "/usr/bin/time -f \"Time: %Us, Memory: %MKB\" java -cp /data/ Main");
            default -> throw new RuntimeException("No Such Code Type" + codeExecDTO.getCodeTypeEnum().getValue());
        }
        return execCreateCmd.exec().getId();
    }

}
