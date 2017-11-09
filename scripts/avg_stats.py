from __future__ import print_function
import os, sys
import numpy as np
import pandas as pd

# python scripts/avg_stats.py carpool.log AppMatch threshold_C51_0.5 100 0 50 20
log_path = sys.argv[1]
app = sys.argv[2]
graph_base = sys.argv[3]
num_user = int(sys.argv[4])
num_spot = int(sys.argv[5])
num_node = int(sys.argv[6])

basedir = os.path.dirname(os.path.realpath(__file__))
dir_path = '{}/../data/{}_{}_{}_{}'.format(basedir, graph_base, num_user, num_spot, num_node)
base_file_prefix = '{}_{}_{}_{}'.format(graph_base, num_user, num_spot, num_node)

def avg_one_setting(df):

	exp_name = df.ix[0, 2].strip()
	before = np.array(df.ix[:, 3:30].mean().tolist())
	pickup_total = df.ix[:, 31].map(lambda str: np.array(eval(str)))
	pickup_total_avg = np.average(np.array(pickup_total.tolist()), axis=0)

	pickup_commuter = df.ix[:, 32].map(lambda str: np.array(eval(str)))
	pickup_commuter_avg = np.average(np.array(pickup_total.tolist()), axis=0)

	pickup_business= df.ix[:, 33].map(lambda str: np.array(eval(str)))
	pickup_business_avg = np.average(np.array(pickup_total.tolist()), axis=0)

	after = np.array(df.ix[:, 34:].mean().tolist())

	hstack = np.hstack((before, pickup_total_avg, pickup_commuter_avg, pickup_business_avg, after))

	return exp_name, hstack


np.set_printoptions(suppress=True)
df = pd.read_csv(log_path, sep='|', header=None)
df_filtered = df[(df[1].str.strip() == app) & (df[2].str.strip().str.startswith(base_file_prefix))]


filepath = dir_path + '/'  + base_file_prefix + '_avg_stats.txt'

mode = 'a' if os.path.exists(filepath) else 'w'
f = open(filepath, mode)


exp_name, arr = avg_one_setting(df)
arr_str = np.char.mod('%.2f', arr)

# fields: 1 + 28 + 10 + 10 + 10 + 9 = 68
str = '|'.join(arr_str)
f.write(exp_name + '|' + str +  '\n')

f.close()
