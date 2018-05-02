###  
###  Released under the MIT License (MIT) --- see ../LICENSE
###  Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi, Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey, Alexandra Kolla
###  

NRUNS=1         # Reduced for sake of running time.
MYJAVAPATH="../"

topology=JF		# JF = JellyFish, FT = fat-tree, XP = Xpander

linkFailRate=0
port=20

tmode=MWPT 	# MWPT = Max. Weight Pairs Truncated

# Different k-fat trees
for k in 12 24 36
do

	port=$k

	case $port in
			12) svrs=432;   fatswitches=180;  jfswitches=90;  jfsvrport=5;  jfnetports=7;  jfsvrs=450;;
			24) svrs=3456;  fatswitches=720;  jfswitches=360; jfsvrport=10; jfnetports=14; jfsvrs=3600;;
			36) svrs=11664; fatswitches=1620; jfswitches=810; jfsvrport=15; jfnetports=21; jfsvrs=12150;;
	esac
	portus=`expr $jfnetports + 1`

	for trafficFrac in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0
	do

		# Clock start
		before="$(date +%s)"
		
		# Executing NRUNS times, store each resulting flow into flowtmp_5a
		rm -rf flowtmp_5b
		for (( i=0 ; i < $NRUNS ; i++ ))
		do
				cd $MYJAVAPATH
				echo "Testing traffic fraction $trafficFrac"
				echo "-gt $topology -tm $tmode -tfr $trafficFrac -switches $switches -switchports $portus -netports $netports -servers 0"
				
				java -jar TSim.jar -gt $topology -tm $tmode -tfr $trafficFrac -switches $jfswitches -switchports $portus -netports $jfnetports -servers 0
				sh scripts/remoteLpRun.sh
				java -jar AnalyzeTSim.jar -tm $tmode
			
		
				cd -

				flowVal=$(cat ../temp/final/objective.txt)
				echo "$flowVal" >> flowtmp_5b
		done
		
		# Clock end
		after="$(date +%s)"
		time_taken=`expr $after - $before`
		time_taken=`expr $time_taken / 60`
		
		# Compute average and standard deviation
		avgstdflow=`cat flowtmp_5b | awk 'BEGIN{sum=0; count=0}{thr=$1; sum+=thr; val[count]=thr; count++}END{mean=sum/count; sq_sum=0; for (i=0; i < count; i++) sq_sum+=(val[i] - mean)*(val[i] - mean); variance=sqrt(sq_sum/count)/mean; rnd_mean=int(mean * 1000) / 1000; rnd_variance=int(variance*1000)/1000; print rnd_mean, rnd_variance}'`

		# Print result
		echo "$topology $jfswitches $port $jfnetports $jfsvrport $tmode $trafficFrac $avgstdflow $time_taken $jfsvrs" >> ../results/figure_5b.txt
		
	done
	
done
