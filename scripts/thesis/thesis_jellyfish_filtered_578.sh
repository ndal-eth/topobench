#!/usr/bin/env bash

###
###  Released under the MIT License (MIT) --- see ../LICENSE
###  Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
###  Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
###  Alexandra Kolla, Simon Kassing
###  

###################################
### SCRIPT PARAMETERS
###

NRUNS=10         # Reduced for sake of running time
MYMAINPATH="../../"

###################################
### TOPOLOGY SPECIFIC PARAMETERS
###

topology=FILE
switches=578
partswitches=578

###################################
### PATH EVALUATION PARAMETERS
###

patheval=SLACK	

###################################
### TRAFFIC GENERATOR SPECIFIC PARAMETERS
###

tmode=MAWP
declare -a candidateseeds=(2137231718 792485483 426505641 2003346559 1848098663 755918586 606831205 254331942 1720774984 195840085);

lpt=MCFFC
slack=-1

		
###################################
### EXECUTE RUNS
### 
for trafficFrac in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0
do

	# Clock start
	before="$(date +%s)"

	# Executing NRUNS times, store each resulting flow into flowtmp_c
	rm -rf flowtmp_c
	for (( i=0 ; i < $NRUNS ; i++ ))
	do
			cd $MYMAINPATH
			filename="empirical-graphs/n=578_d=25_time=${i}.topo.txt"
			seed=${candidateseeds[$i]}
			
			# Generate linear program and additional information in temp/
			# There are five parts to the command: general, selectors, topology parameters, path evaluator parameters, and traffic parameters
			java -jar TopoBench.jar \
			-mode PRODUCE -seed $seed -lpt $lpt \
			-gt $topology -pe $patheval -tm $tmode \
			-switches $switches -partswitches $partswitches -filename $filename \
			-slack $slack \
			-tfr $trafficFrac
			
			# Execute solver
			sh scripts/remoteLpRun.sh	# Local: scripts/localLpRun.sh, Remote: scripts/remoteLpRun.sh
			
			# Run analysis (result will be in analysis/<time-specific-folder-name>/)
			java -jar TopoBench.jar \
			-mode ANALYZE -seed $seed -lpt $lpt \
			-tm $tmode
			
			cd -

			# Add to list of received flow values
			flowVal=$(cat ../../temp/objective.txt)
			echo "$flowVal" >> flowtmp_c
			
	done

	# Clock end
	after="$(date +%s)"
	time_taken=`expr $after - $before`
	time_taken=`expr $time_taken / 60`

	# Calculate average and standard deviation
	avgstdflow=`cat flowtmp_c | awk 'BEGIN{sum=0; count=0}{thr=$1; sum+=thr; val[count]=thr; count++}END{mean=sum/count; sq_sum=0; for (i=0; i < count; i++) sq_sum+=(val[i] - mean)*(val[i] - mean); variance=sqrt(sq_sum/count)/mean; rnd_mean=int(mean * 100000) / 100000; rnd_variance=int(variance*100000)/100000; print rnd_mean, rnd_variance}'`

	# Write result to file
	echo "$patheval SL=$slack ${topology}(${filename}) $switches $tmode $trafficFrac $avgstdflow $time_taken" >> ../../results/thesis/thesis_jellyfish_filtered_578_mawp_fraction.txt

done
