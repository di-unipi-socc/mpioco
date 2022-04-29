# MPIOCO 

This repository provides a proof-of-concept implementation of the MPIOCO conformance testing framework presented in
> J. Soldani, L. Luthmann, N. Gottwald M. Lochau, A. Brogi. _Compositional Testing of Management Conformance for Multi-Component Enterprise Applications_. [Submitted for publication]

## Running MPIOCO
MPIOCO is released as a Java/Maven project. A runnable instance of MPIOCO can be obtained by cloning this repository and by running the following command in the cloned project's folder  
```
mvn clean install
```
As a result, Maven creates a file `mpioco-0.1.jar` in the `target` folder, which can be copied in the main project folder to run MPIOCO as follows:
```
java -jar mpioco-0.1.jar OPTIONS PATH_TO_SPEC PATH_TO_IMPL
```
where `PATH_TO_SPEC` and `PATH_TO_IMPL` are the paths to the TOSCA XML files specifying the management protocols of the specification and candidate implementation to be tested. `OPTIONS` is instead a list of options, which can be any the following:
* `-b strict|relaxed` (or `--beta strict|relaxed`) to configure the test to use the stricter/relaxed version of the _beta_ parameter; by default, MPIOCO uses the relaxed version of _beta_.
* `-g strict|relaxed` (or `--gamma strict|relaxed`) to configure the test to use the stricter/relaxed version of the _gamma_ parameter; by default, MPIOCO uses the relaxed version of _gamma_.
* `-c` to configure the test to consider the composition of the management protocols of all nodes forming an application, rather than only those of a single node (in this case, `PATH_TO_SPEC` and `PATH_TO_IMPL` must be paths to folders containing the TOSCA XML files specifying the management protocols of all nodes in an application); by default, MPIOCO runs on a single node.