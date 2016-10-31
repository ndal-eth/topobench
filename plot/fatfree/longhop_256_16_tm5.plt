###  
###  Released under the MIT License (MIT) --- see ../LICENSE
###  Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi, Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey, Alexandra Kolla
###  

# Note you need gnuplot 4.4 for the pdfcairo terminal.
set terminal pdfcairo font "Gill Sans, 18" linewidth 2 rounded

# Line style for axes
set style line 80 lt rgb "#808080" 

# Line style for grid
set style line 81 lt 0  # dashed
set style line 81 lt rgb "#808080"  # grey

set grid back linestyle 81
set border 3 back linestyle 80 # Remove border on top and right.  These
             # borders are useless and make it harder
	                  # to see plotted lines near the border.
			      # Also, put it in grey; no need for so much emphasis on a border.
			      set xtics nomirror
			      set ytics nomirror

#set log x
#set mxtics 10    # Makes logscale look good.

# Line styles: try to pick pleasing colors, rather
# than strictly primary colors or hard-to-see colors
# like gnuplot's default yellow.  Make the lines thick
# so they're easy to see in small plots in papers.
#set style line 1 lt rgb "#5060D0" lw 2 pt 1
#set style line 2 lt rgb "#A00000" lw 2 pt 2
#set style line 3 lt rgb "#00A000" lw 2 pt 6
#set style line 4 lt rgb "#F25900" lw 2 pt 9
#set style line 5 lt rgb "#996600" lw 2 pt 4
set style line 1 lt rgb "#5060D0" lw 1.5 pt 7
set style line 2 lt rgb "#00A000" lw 2 pt 6
set style line 3 lt rgb "#A00000" lw 1.2 pt 6
set style line 4 lt rgb "#A00000" lw 1 pt 7
set style line 5 lt rgb "#00A000" lw 2 pt 8
set style line 6 lt rgb "#00A000" lw 1 pt 9

set output "gen-plots/longhop_256_16_tm5.pdf"
set xlabel "Fraction of servers with traffic demand"
set ylabel "Throughput per server"

set xrange [0.1:1]
set yrange [0:1]

set key top right 

plot "../results/longhop_256_16_tm5.txt" using 1:4 title "Ideal" w l ls 3, \
     "../results/longhop_256_16_tm5.txt" using 1:3 title "Jellyfish" w lp ls 2, \
     "../results/longhop_256_16_tm5.txt" using 1:2 title "Longhop" w lp ls 1
     
#plot "../results/slimfly.txt" using 6:(($1==98 && $5==5)? ($7>10 ? 1: $7/10) : 1/0) title "SF-11"  w lp ls 1, \
#     "../results/slimfly_JF.txt" using 6:(($1==98 && $5==5)? ($7>10 ? 1: $7/10) : 1/0) title "JF-11"  w lp ls 2, \
#     "../results/slimfly.txt" using 6:(($1==242 && $5==5)? ($7>10 ? 1: $7/10) : 1/0) title "SF-17"  w lp ls 3, \
#     "../results/slimfly_JF.txt" using 6:(($1==242 && $5==5)? ($7>10 ? 1: $7/10) : 1/0) title "JF-17"  w lp ls 4
     
     # 0.25 title "25% Fat-tree" w l ls 2
