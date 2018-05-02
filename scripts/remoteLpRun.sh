#!/usr/bin/env bash

# Use timestamp to ensure uniqueness on the remote server
if [ ! -f scripts/MID ]; then
	basetime=$(date +%s%N)
    echo "$basetime" > scripts/MID
fi

# Read in machine identifier
read -r mid < scripts/MID

# Copy linear program
scp temp/program.lp USER@MACHINE:"/home/USER/program$mid.lp"

# Execute solver
ssh USER@MACHINE "gurobi_cl Method=2 Crossover=0 BarConvTol=1e-8 PreDual=0 Presolve=2 ResultFile=\"vector$mid.sol\" program$mid.lp"

# Copy results
scp USER@MACHINE:"/home/USER/vector$mid.sol" "temp/vector.sol"
