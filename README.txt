I. ABOUT THIS MICRO-BENCHMARK
=============================

This micro-benchmark is an application (AppRoot) that holds a set of integer
wrappers (IntContainer), through an indirection level to allow only loading
some IntContainers.

                  1     *                  1     1
          AppRoot <-----> IndirectionLevel <-----> IntContainer

Each transaction will iterate over the set of IndirectionLevels and for each
one will either:

a) do a read of the int value
b) do a write to the int value
c) do nothing

The idea is to simulate a given workload and to test how the different
backends react to it.  At the time of writing the available backends are
MySQL/OJB, HBase and BerkeleyDB.  The benchmark is highly configurable.  Run
it with '-help' to see a list of parameters.

II. CONFIGURATION
=================

Please put the desired fenix-framework jar file in the 'lib' dir.  This
depends on which version you wish test (OJB or HBase/BerkeyDB).

Additionaly, configure the framework in src/java/Configuration.java with the
correct configuration depending on the fenix-framework in use.

You can also edit 'build.xml' to choose with object layout to generate, by
changing the 'fenix.framework.codeGenerator' property.  At the time of writing
possible choices are:

- 'pt.ist.fenixframework.pstm.dml.FenixCodeGenerator'
- 'pt.ist.fenixframework.pstm.dml.FenixCodeGeneratorOneBoxPerObject'


III. RUNNING THE BENCHMARK
==========================

Run 'ant' to compile.  Then, start by populating the database with

./populate.sh <#nbr of items>

Afterwards you can run the benchmark as many time as desired without
repopulating.  Run the benchmark with:

./run.sh <options go here>

or use  ./run.sh -help to see the default options.

