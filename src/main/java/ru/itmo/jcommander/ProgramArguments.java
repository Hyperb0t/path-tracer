package ru.itmo.jcommander;

import com.beust.jcommander.Parameter;

public class ProgramArguments {

    public ProgramArguments() {
    }

    public ProgramArguments(String scene, String output, int port, String ip) {
        this.scene = scene;
        this.output = output;
        this.port = port;
        this.ip = ip;
    }

    @Parameter(names = {"--help", "-h"}, help = true, description = "display this help message")
    private boolean help = false;

    @Parameter(names = {"--mode", "-m"}, description = "one of: Standalone, Scene, Renderer")
    private RunMode runMode = RunMode.Standalone;

    @Parameter(names = {"--scene", "-s"}, description = "path to scene obj file")
    private String scene = "models/cornell_box.obj";

    @Parameter(names = {"--output", "-o"}, description = "output filename")
    private String output = "output.hdr";

    @Parameter(names = {"--port", "-p"}, description = "port for Renderer mode to listen or Scene mode to connect to")
    private int port = 50051;

    @Parameter(names = {"--ip", "-i"}, description = "address of Renderer to connect to in Scene mode")
    private String ip = "localhost";

    @Parameter(names = {"--algorithm", "-a"}, description = "Rendering algorith for standalone or renderer mode, one of: pt, ptopfd")
    private Algorithm algorithm = Algorithm.PT;

    public boolean isHelp() {
        return help;
    }

    public RunMode getRunMode() {
        return runMode;
    }

    public String getScene() {
        return scene;
    }

    public String getOutput() {
        return output;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public String toString() {
        return "ProgramArguments{" +
                "help=" + help +
                ", runMode=" + runMode +
                ", scene='" + scene + '\'' +
                ", output='" + output + '\'' +
                ", port=" + port +
                ", ip='" + ip + '\'' +
                ", algorithm=" + algorithm +
                '}';
    }
}
