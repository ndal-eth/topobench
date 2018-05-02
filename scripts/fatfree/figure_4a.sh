###  
###  Released under the MIT License (MIT) --- see ../LICENSE
###  Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi, Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey, Alexandra Kolla, Simon Kassing
###  

NRUNS=1         # Reduced for sake of running time.
MYJAVAPATH="../"

topology=XP	# JF = JellyFish, 
			# FT = fat-tree, 
			# XP = Xpander
			
numsvr=0
switches=590
svrport=24
netports=25
totport=49
portus=`expr $netports + 1`

tmode=MWPT 	# MWPT = Max. Weight Pairs Truncated

# Try for many traffic fractions
for trafficFrac in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0
do

	# Clock start
    before="$(date +%s)"
	
	# Executing NRUNS times, store each resulting flow into flowtmp_4a
	rm -rf flowtmp_4a
	for (( i=0 ; i < $NRUNS ; i++ ))
    do
            cd $MYJAVAPATH
			echo "Testing traffic fraction $trafficFrac"
			echo "-gt $topology -tm $tmode -tfr $trafficFrac -switches $switches -switchports $portus -netports $netports -servers 0"
			
			java -jar TSim.jar -gt $topology -tm $tmode -tfr $trafficFrac -switches $switches -switchports $portus -netports $netports
			sh scripts/remoteLpRun.sh
			java -jar AnalyzeTSim.jar -tm $tmode
		
			cd -

            flowVal=$(cat ../temp/final/objective.txt)
            echo "$flowVal" >> flowtmp_4a
    done
	
	# Clock end
    after="$(date +%s)"
    time_taken=`expr $after - $before`
    time_taken=`expr $time_taken / 60`
	
	# Calculate average and standard deviation
    avgstdflow=`cat flowtmp_4a | awk 'BEGIN{sum=0; count=0}{thr=$1; sum+=thr; val[count]=thr; count++}END{mean=sum/count; sq_sum=0; for (i=0; i < count; i++) sq_sum+=(val[i] - mean)*(val[i] - mean); variance=sqrt(sq_sum/count)/mean; rnd_mean=int(mean * 1000) / 1000; rnd_variance=int(variance*1000)/1000; print rnd_mean, rnd_variance}'`

	# Echo result
	echo "$topology $switches $totport $netports $svrport $tmode $trafficFrac $avgstdflow $time_taken" >> ../results/figure_4a.txt
    
done
