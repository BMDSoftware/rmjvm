 # Resource Monitor for Java (rmjvm)
  
 ## Description 
 
 Resource Monitor for Java is a tool that helps to monitor Java process resources and detect memory leaks/CPU constrains.
 
 ## How to use?
 
 Before you use this tool, you need to be aware what are your goals. First step, is to understand what kind of application that
 you need to monitor and what are the moments to monitor and actions that you need to trigger. 

```
rmjvm 1.0
usage: rmjvm [-c] [-e <arg>] [-ed <arg>] [-h] [-ho <arg>] [-p <arg>] [-s
       <arg>]
 -c,--check                    check will run all the actionsand wait
                               until it is requested to stop. Meanwhile it
                               will monitoring the memory and compare
 -e,--export <arg>             export format (csv, output)
 -ed,--exportdirectory <arg>   export directory where will be stored the
                               files.
 -h,--help                     help shows how to use the rmjvm and what
                               its core funcionality
 -ho,--host <arg>              set the hostname for JMX of java listen
                               process
 -p,--port <arg>               set the port for JMX of java listen process
 -s,--skip <arg>               skip the cpu or memory (--skip=mem,cpu)

```

## Examples  

Few examples of use of the application:
  
 ```

$ rmjvm --help 
$ rmjvm --version 
$ rmjvm --check 
$ rmjvm --skip=mem
$ rmjvm --skip=cpu,mem
$ rmjvm --export=csv --exportdirectory=/tmp/dump-reports

```


 ## Recommended tools 
 
 You should be aware at least of two tools:
 
 - jConsole 
 - Mission Control and Flight Recorder
 - Oracle VisualVM
 - JVM tools: https://github.com/aragozin/jvm-tools/
 - Eclipse Memory Analyser: MAT
  
## Other resources

 - https://www.sderosiaux.com/articles/2017/02/14/all-the-things-we-can-do-with-jmx/  
 - https://sysdig.com/blog/jmx-monitoring-custom-metrics/ 
 
 ## Contribute 
 
 You can contribute for the project by send Pull Requests.
 
 Build >= JDK12  
 
## Support 

It is an open source project and no enterprise support is provided, only by the community.
 
[<img src="https://raw.githubusercontent.com/wiki/BMDSoftware/dicoogle/images/bmd.png" height="64" alt="BMD Software">](https://www.bmd-software.com)

Please contact [BMD Software](https://www.bmd-software.com) for more information.