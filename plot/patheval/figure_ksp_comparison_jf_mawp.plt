###  
###  Released under the MIT License (MIT) --- see ../LICENSE
###  Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi, Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey, Alexandra Kolla, Simon Kassing
###  

# Note you need gnuplot 4.4 for the pdfcairo terminal.
set terminal pdfcairo font "Gill Sans, 16" linewidth 1.8 rounded dashed

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
set style line 1 lt rgb "#048b9a" lw 1 pt 4
set style line 2 lt rgb "#048b9a" lw 2 pt 2 ps 0.8
set style line 3 lt rgb "#048b9a" lw 1.3 pt 6 ps 0.8
set style line 4 lt rgb "#048b9a" lw 1 pt 8
set style line 5 lt rgb "#CD0000" lw 1.9 pt 7 ps 0.75
set style line 6 lt rgb "#CD0000" lw 1 pt 4
set style line 7 lt rgb "#CD0000" lw 2 pt 2 ps 0.8
set style line 8 lt rgb "#CD0000" lw 1.3 pt 6 ps 0.8
set style line 9 lt rgb "#CD0000" lw 1 pt 8
set style line 10 lt rgb "#CD0000" lw 1.5 pt 7 ps 0.5
set style line 11 lt rgb "#CD0000" lw 1 pt 4 ps 0.5

set output "figure_ksp_comparison_jf_mawp.pdf"
set xlabel "Fraction of servers with traffic demand"
set ylabel "Throughput per server"

set xrange [0.1:1]
set yrange [0:1]

set size .75, 1
set key at 1.52,1
# set key top right
#set key at 0.5,0.37
#set key opaque

plot "../../results/patheval/ksp_comparison_jf_mawp.txt" using 9:((strcol(2) eq "K=100") ? (($10/$7) > 1 ? 1 : ($10/$7)) : 1/0) title "K=100"  w lp ls 5, \
	 "../../results/patheval/ksp_comparison_jf_mawp.txt" using 9:((strcol(2) eq "K=25") ? (($10/$7) > 1 ? 1 : ($10/$7)) : 1/0) title "K=25"  w lp ls 6, \
	 "../../results/patheval/ksp_comparison_jf_mawp.txt" using 9:((strcol(2) eq "K=20") ? (($10/$7) > 1 ? 1 : ($10/$7)) : 1/0) title "K=20"  w lp ls 7, \
	 "../../results/patheval/ksp_comparison_jf_mawp.txt" using 9:((strcol(2) eq "K=10") ? (($10/$7) > 1 ? 1 : ($10/$7)) : 1/0) title "K=10"  w lp ls 8, \
	 "../../results/patheval/ksp_comparison_jf_mawp.txt" using 9:((strcol(2) eq "K=5") ? (($10/$7) > 1 ? 1 : ($10/$7)) : 1/0) title "K=5"  w lp ls 9, \
	 "../../results/patheval/ksp_comparison_jf_mawp.txt" using 9:((strcol(2) eq "K=2") ? (($10/$7) > 1 ? 1 : ($10/$7)) : 1/0) title "K=2"  w lp ls 10, \
	 "../../results/patheval/ksp_comparison_jf_mawp.txt" using 9:((strcol(2) eq "K=1") ? (($10/$7) > 1 ? 1 : ($10/$7)) : 1/0) title "K=1"  w lp ls 11
     
	 
     
     