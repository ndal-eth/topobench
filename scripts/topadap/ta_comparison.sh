#!/usr/bin/env bash
###
###  Released under the MIT License (MIT) --- see ../LICENSE
###  Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
###  Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
###  Alexandra Kolla, Simon Kassing
###  

NRUNS=1         # Reduced for sake of running time.
MYJAVAPATH="../../"

topology=JF	# JF = JellyFish,
			# FT = fat-tree, 
			# XP = Xpander
			
patheval=SLACK	# SLACK = Use slack such that flow cannot deviate more than SLACK from shortest path src-dst
				# NEIGH = Use neighbor's shortest path to destination
slack=-1

linkFailRate=0
numsvr=0
switches=90
svrports=10
netports=5
totport=`expr $svrports + $netports`
portus=`expr $netports + 1`

seed=1

tmode=ATAF	# RPP = Rand. Permutation Pairs,
			# ATA = All-to-All,
			# ATAF = All-to-All Fraction,
			# AT1 = All-to-One, 
			# STR = Stride, 
			# MIWP = Min. Weight Pairs
			# MAWP = Max. Weight Pairs

for trafficFrac in 0.1
do
	
	# Clock start
	before="$(date +%s)"

	# Executing NRUNS times, store each resulting flow into flowtmp_ta
	rm -rf flowtmp_ta
	for (( i=0 ; i < $NRUNS ; i++ ))
	do
			cd $MYJAVAPATH
			echo "Testing traffic fraction $trafficFrac"
			echo "-gt $topology -tm $tmode -tfr $trafficFrac -lfr $linkFailRate -switches $switches -switchports $portus -netports $netports -servers $numsvr"
			java -jar TSim.jar -gt $topology -tm $tmode -pe $patheval -slk $slack -tfr $trafficFrac -lfr $linkFailRate -seed $seed -switches $switches -switchports $portus -netports $netports -servers $numsvr
			sh scripts/localLpRun.sh
			java -jar AnalyzeTSim.jar -tm $tmode
			cd -

			flowVal=$(cat ../../temp/final/objective.txt)
			echo "$flowVal" >> flowtmp_ta
	done

	# Clock end
	after="$(date +%s)"
	time_taken=`expr $after - $before`
	time_taken=`expr $time_taken / 60`

	# Calculate average and standard deviation
	avgstdflow=`cat flowtmp_ta | awk 'BEGIN{sum=0; count=0}{thr=$1; sum+=thr; val[count]=thr; count++}END{mean=sum/count; sq_sum=0; for (i=0; i < count; i++) sq_sum+=(val[i] - mean)*(val[i] - mean); variance=sqrt(sq_sum/count)/mean; rnd_mean=int(mean * 100000) / 100000; rnd_variance=int(variance*100000)/100000; print rnd_mean, rnd_variance}'`

	# Echo result
	echo "$patheval $slack $topology $switches $totport $netports $svrports $tmode $trafficFrac $avgstdflow $time_taken" >> ../../results/topadap/ta_comparison.txt
	
done
