- The mpioco-check is implemented as a Java tool which can be executed via the main class as usual.
- In the main class, the mpioco parameters (beta, gamma) can be set as described in the paper.
- In addition, the folder(s) containing the specification model and
implemenation model can be specified.
- By default, the specification is taken from the folder "tosca_spec" and the implementation is taken from the folder "tosca_impl".
- For checking management models with multiple nodes, either the individual nodes have to be manually copied into the respective folders one-by-one or
or the respective composite applications. In all cases, the file format is ".tosca".
