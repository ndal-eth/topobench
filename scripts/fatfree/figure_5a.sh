###  
###  Released under the MIT License (MIT) --- see ../LICENSE
###  Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi, Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey, Alexandra Kolla, Simon Kassing
###  

NRUNS=1         # Reduced for sake of running time.
MYJAVAPATH="../"

topology=XP	# JF = JellyFish, 
			# FT = fat-tree, 
			# XP = Xpander
			
tmode=MWPT 	# MWPT = Max. Weight Pairs Truncated

# Different fraction of switches
for switches in 200 250 400		# {200, 250, 500} are {40%, 50%, 80%} of 500 switches
do

	# Fat-tree with k = 20 would have 20 * 20 * 5/4 = 500 switches
	# Fat-tree with k = 20 would have 20 * 20 * 20 / 4 = 8000 / 4 = 2000 servers
	port=20
	netports=`expr $port - 2000 / $switches`
	svrport=`expr $port - $netports`
	portus=`expr $netports + 1`

	for trafficFrac in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0
	do

		# Clock start
		before="$(date +%s)"
		
		# Executing NRUNS times, store each resulting flow into flowtmp_5a
		rm -rf flowtmp_5a
		for (( i=0 ; i < $NRUNS ; i++ ))
		do
            cd $MYJAVAPATH
			echo "Testing traffic fraction $trafficFrac"
			echo "-gt $topology -tm $tmode -tfr $trafficFrac -switches $switches -switchports $portus -netports $netports -servers 0"
			
			java -jar TSim.jar -gt $topology -tm $tmode -tfr $trafficFrac -switches $switches -switchports $portus -netports $netports # -servers 0
			sh scripts/remoteLpRun.sh
			java -jar AnalyzeTSim.jar -tm $tmode
		
			cd -

            flowVal=$(cat ../temp/final/objective.txt)
            echo "$flowVal" >> flowtmp_5a
		done
		
		# Clock end
		after="$(date +%s)"
		time_taken=`expr $after - $before`
		time_taken=`expr $time_taken / 60`
		
		# Compute average and standard deviation
		avgstdflow=`cat flowtmp_5a | awk 'BEGIN{sum=0; count=0}{thr=$1; sum+=thr; val[count]=thr; count++}END{mean=sum/count; sq_sum=0; for (i=0; i < count; i++) sq_sum+=(val[i] - mean)*(val[i] - mean); variance=sqrt(sq_sum/count)/mean; rnd_mean=int(mean * 1000) / 1000; rnd_variance=int(variance*1000)/1000; print rnd_mean, rnd_variance}'`

		# Print result
		echo "$topology $switches $port $netports $svrport $tmode $trafficFrac $avgstdflow $time_taken 2000" >> ../results/figure_5a.txt
		
	done
	
done
