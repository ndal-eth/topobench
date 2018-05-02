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

topology=JF	# JF = JellyFish,
			# FT = fat-tree, 
			# XP = Xpander
			
linkFailRate=0.0	# Link failure rate [0.0, 1.0]
switches=128		# Number of switches
svrports=1			
netports=8
totport=`expr $svrports + $netports`
portus=`expr $netports + 1`

###################################
### PATH EVALUATION PARAMETERS
###

patheval=SLACK	

###################################
### TRAFFIC GENERATOR SPECIFIC PARAMETERS
###

tmode=MIWP
declare -a candidateseeds=(2137231718 792485483 426505641 2003346559 1848098663 755918586 606831205 254331942 1720774984 195840085);

for m in 0 1
do

	# Two modes: shortest path and all paths
	if [ $m -eq 0 ]
	then
		lpt=MCFFC
		slack=0
	else
		lpt=MCFFC
		slack=-1
	fi
			
	###################################
	### EXECUTE RUNS
	### 
	#                  12  16   24   32   40   48   56   64  72   80   88   96   104  112  120  128
	for trafficFrac in 0.1 0.13 0.19 0.25 0.32 0.38 0.44 0.5 0.57 0.63 0.69 0.75 0.82 0.88 0.94 1.0
	do

		# Clock start
		before="$(date +%s)"

		# Executing NRUNS times, store each resulting flow into flowtmp_c
		rm -rf flowtmp_c
		for (( i=0 ; i < $NRUNS ; i++ ))
		do
				cd $MYMAINPATH
				
				seed=${candidateseeds[$i]}
				
				# Generate linear program and additional information in temp/
				# There are five parts to the command: general, selectors, topology parameters, path evaluator parameters, and traffic parameters
				java -jar TopoBench.jar \
				-mode PRODUCE -seed $seed -lpt $lpt \
				-gt $topology -pe $patheval -tm $tmode \
				-switches $switches -switchports $totport -netports $netports \
				-slack $slack \
				-tfr $trafficFrac
				
				# Help convergence of linear program
				str="c3_0: K >= 0.02"
				sed -i '5i '"$str"'' temp/program.lp
				
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
		echo "$patheval SL=$slack $topology $switches $tmode $trafficFrac $avgstdflow $time_taken" >> ../../results/thesis/thesis_jellyfish_miwp_fraction.txt

	done

done
