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

NRUNS=1         # Reduced for sake of running time
MYMAINPATH="../../"

###################################
### GENERAL PARAMETERS
###

seed=1			# Set to 0 for random
lpt=SIMPLE      # SIMPLE is fast for dense traffic matrices
                # MCFFC is fast for sparse traffic matrices

###################################
### TOPOLOGY SPECIFIC PARAMETERS
###

topology=FT	# JF = JellyFish,
			# FT = fat-tree, 
			# XP = Xpander
			
kft=16
perpod=8

###################################
### PATH EVALUATION PARAMETERS
###

patheval=SLACK	# SLACK = Use slack such that flow cannot deviate more than SLACK from shortest path src-dst
				# NEIGH = Use neighbor's shortest path to destination		
				# KSHRT = K-shortest paths
				# VALIA = K-valiant load balancing
				
slack=-1			# Slack value [0,inf], set -1 for infinite slack
#kvlb=20				# K-value for either k-shortest path or k-valiant load balancing


###################################
### TRAFFIC GENERATOR SPECIFIC PARAMETERS
###

tmode=ATAFPO
			
###################################
### EXECUTE RUNS
###
#                  16   24   32   40   48   56   64  72   80   88   96   104  112  120  128
for trafficFrac in 0.13 0.19 0.25 0.32 0.38 0.44 0.5 0.57 0.63 0.69 0.75 0.82 0.88 0.94 1.0
do

	# Clock start
	before="$(date +%s)"

	# Executing NRUNS times, store each resulting flow into flowtmp_c
	rm -rf flowtmp_c
	for (( i=0 ; i < $NRUNS ; i++ ))
	do
			cd $MYMAINPATH
			
			# Generate linear program and additional information in temp/
			# There are five parts to the command: general, selectors, topology parameters, path evaluator parameters, and traffic parameters
			java -jar TopoBench.jar \
			-mode PRODUCE -seed $seed -lpt $lpt \
			-gt $topology -pe $patheval -tm $tmode \
			-kft $kft \
			-slack $slack \
			-pp $perpod -tfr $trafficFrac
			
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
	echo "$patheval SL=$slack $topology $switches $kft $tmode $trafficFrac $avgstdflow $time_taken" >> ../../results/thesis/thesis_fat_tree_0.txt

done
