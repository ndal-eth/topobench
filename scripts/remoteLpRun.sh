# Use timestamp to ensure uniqueness on the remote server
if [ ! -f scripts/MID ]; then
	basetime=$(date +%s%N)
    echo "$basetime" > scripts/MID
fi

# Read in machine identifier
read -r mid < scripts/MID

# Copy linear program
scp temp/program.lp kassings@sgs-r815-01.ethz.ch:"/home/kassings/program$mid.lp"

# Execute solver
ssh kassings@sgs-r815-01.ethz.ch "gurobi_cl Method=2 Crossover=0 BarConvTol=1e-8 PreDual=0 Presolve=2 ResultFile=\"vector$mid.sol\" program$mid.lp"

# Copy results
scp kassings@sgs-r815-01.ethz.ch:"/home/kassings/vector$mid.sol" "temp/vector.sol"
