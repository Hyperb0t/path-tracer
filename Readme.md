# Federated renderer
Developed by ITMO second year master students.

## How to build
### Prerequsites
* Java 11 or newer
* Maven 3.6 or newer
### Build command
```mvn clean install```

### 

## How to run
Assuming you have just build the jar from source code,
the jar should be run with the following command:

``` java -jar target/path-tracer-1.0.jar```
### Run parameters
```
--mode, -m 	 default Standalone
one of: Standalone, Scene, Renderer
--algorithm, -a 	 default PT
Rendering algorith for standalone or renderer mode, one of: pt, ptopfd
--ip, -i 	 default localhost
address of Renderer to connect to in Scene mode
--port, -p 	 default 50051
port for Renderer mode to listen or Scene mode to connect to
--output, -o 	 default output.hdr
output filename
--help, -h 	 default false
display this help message
--scene, -s 	 default models/cornell_box.obj
path to scene obj file
```