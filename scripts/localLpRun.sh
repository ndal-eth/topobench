#!/usr/bin/env bash

# Execute local gurobi solver
gurobi_cl Method=2 Crossover=0 BarConvTol=1e-8 PreDual=0 Presolve=0 ResultFile="temp/vector.sol" temp/program.lp