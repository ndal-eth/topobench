#!/usr/bin/env bash
###
###  Released under the MIT License (MIT) --- see ../LICENSE
###  Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
###  Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
###  Alexandra Kolla, Simon Kassing
###  

NRUNS=1         # Reduced for sake of running time.
MYJAVAPATH="../../"

topology=XP	# JF = JellyFish,
			# FT = fat-tree, 
			# XP = Xpander
			
patheval=KSHRT	# SLACK = Use slack such that flow cannot deviate more than SLACK from shortest path src-dst
				# NEIGH = Use neighbor's shortest path to destination
				# KSHRT = Use K-shortest path
				
linkFailRate=0
numsvr=0
switches=231
svrports=10
netports=10
totport=`expr $svrports + $netports`
portus=`expr $netports + 1`

tmode=MIWP	# RPP = Rand. Permutation Pairs,
			# ATA = All-to-All, 
			# AT1 = All-to-One, 
			# STR = Stride, 
			# MIWP = Min. Weight Pairs
			# MAWP = Max. Weight Pairs

peprep=0	# True iff it start with a path evaluator preparation run

# 300 275 250 225 200 190 180 170 160 150 140 130 120 110 100 95 90 85 80 75 70 65 60 55 50 45 40 35 30 25 22 20 19 18 17 16 15 14 13 12 11 10 9 8 7 6 5 4 3 2 1
# 20 19 18 17 16 15 14 13 12 11 10 9 8 7 6 5 4 3 2 1
# 5 4 3 2 1
for ksp in 300 275 250 225 200 190 180 170 160 150 140 130 120 110 100 95 90 85 80 75 70 65 60 55 50 45 40 35 30 25 22 20 19 18 17 16 15 14 13 12 11 10 9 8 7 6 5 4 3 2 1
do			

	for trafficFrac in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0
	do
		
		# Clock start
		before="$(date +%s)"

		# Executing NRUNS times, store each resulting flow into flowtmp_ksp_miwp
		rm -rf flowtmp_ksp_miwp
		seed=1
		for (( i=0 ; i < $NRUNS ; i++ ))
		do
				cd $MYJAVAPATH
				echo "Testing traffic fraction $trafficFrac"
				echo "-gt $topology -tm $tmode -tfr $trafficFrac -lfr $linkFailRate -switches $switches -switchports $portus -netports $netports -servers $numsvr"
				java -jar TSim.jar -gt $topology -tm $tmode -pe $patheval -peprep $peprep -ksp $ksp -tfr $trafficFrac -lfr $linkFailRate -seed $seed -switches $switches -switchports $portus -netports $netports # -servers $numsvr
				sh scripts/remoteLpRun.sh
				java -jar AnalyzeTSim.jar -tm $tmode
				cd -

				flowVal=$(cat ../../temp/final/objective.txt)
				echo "$flowVal" >> flowtmp_ksp_miwp
				seed=`expr $seed + 1`
				peprep=0
		done

		# Clock end
		after="$(date +%s)"
		time_taken=`expr $after - $before`
		time_taken=`expr $time_taken / 60`

		# Calculate average and standard deviation
		avgstdflow=`cat flowtmp_ksp_miwp | awk 'BEGIN{sum=0; count=0}{thr=$1; sum+=thr; val[count]=thr; count++}END{mean=sum/count; sq_sum=0; for (i=0; i < count; i++) sq_sum+=(val[i] - mean)*(val[i] - mean); variance=sqrt(sq_sum/count)/mean; rnd_mean=int(mean * 100000) / 100000; rnd_variance=int(variance*100000)/100000; print rnd_mean, rnd_variance}'`

		# Echo result
		echo "$patheval K=$ksp $topology $switches $totport $netports $svrports $tmode $trafficFrac $avgstdflow $time_taken" >> ../../results/patheval/ksp_comparison_miwp.txt
		
	done

done