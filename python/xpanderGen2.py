import random
import numpy as np
from numpy import linalg as LA
import math
import argparse
import sys

def get_spectral_gap(d):
    return 2*math.sqrt(d-1)

# d = the degree of the graph
# k = number of lifts to perform
# e.g.,: random_k_lift(4,6) will create a 4-regular graph with 30 nodes
def random_k_lift(d, k):
    num_nodes = (d+1)*k
    mat = np.zeros( (num_nodes,num_nodes) )

    # go over all meta nodes
    for meta1 in range(d+1):
        # connect to any other meta node
        for meta2 in range(meta1+1, d+1):

            # connect the ToRs between the meta-nodes randomly
            perm = np.random.permutation(k)
            for src_ind in range(k):
                src = meta1*k + src_ind
                dst = meta2*k + perm[src_ind]

                # connect the link
                mat[src,dst] = 1
                mat[dst,src] = 1

    eig,vecs = LA.eig(mat)
    eig = np.abs(eig)
    eig.sort()
    if eig[-2] < get_spectral_gap(d):
        return random_k_lift(d,k)

    return mat

# Open file
fIn = open(sys.argv[1], 'r')
fOut = open(sys.argv[2], 'w')
sys.stdout = fOut

d = int(fIn.readline())
k = int(fIn.readline())
seed = int(fIn.readline())

if (seed != 0):
	np.random.seed(seed)

res = random_k_lift(d, k);
for i in range((d+1)*k):
	for j in range(i + 1,(d+1)*k):
		if (res[i, j] == 1):
			print i, j

fIn.close()
fOut.close()